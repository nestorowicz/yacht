package com.github.nestorowicz.yacht.core;

import com.github.nestorowicz.yacht.config.Config;
import com.github.nestorowicz.yacht.util.RefreshableTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsensusModule {

    private final Logger logger = LoggerFactory.getLogger(ConsensusModule.class);

    private volatile NodeStatus status = NodeStatus.FOLLOWER;
    // TODO State temporarily kept in memory only - to be extracted
    private Integer currentTerm = 0;
    private String votedFor;

    private final Config config;
    private final RefreshableTimer refreshableTimer;
    private final ElectionModule electionModule;
    private final HeartbeatSender heartbeatSender;

    public ConsensusModule(Config config, RefreshableTimer refreshableTimer, ElectionModule electionModule,
                           HeartbeatSender heartbeatSender) {
        this.config = config;
        this.refreshableTimer = refreshableTimer;
        this.electionModule = electionModule;
        this.heartbeatSender = heartbeatSender;
    }

    public Integer getCurrentTerm() {
        return currentTerm;
    }

    public void startConsensusModule() {
        logger.info("Starting ConsensusModule as a follower.");
        status = NodeStatus.FOLLOWER;
        while (true) {
            refreshableTimer.startRandomized(config.getElectionTimeoutFrom(), config.getElectionTimeoutTo());
            logger.info("Timed out. Converting to a candidate.");
            status = NodeStatus.CANDIDATE;
            if (callElection()) {
                logger.info("Converting to leader.");
                break;
            }
            logger.info("Converting to follower.");
        }
        status = NodeStatus.LEADER;
        startSendingHeartbeat();
    }


    private boolean callElection() {
        // TODO Elections must be done differently - timeout without waiting for all answers (handle http timeouts)
        // TODO Can be done async
        ElectionModule.ElectionResult electionResult;
        do {
            currentTerm++;
            electionResult = electionModule.callElection(currentTerm);
            logger.info("Election result for term {} is: {}", currentTerm, electionResult);
            if (electionResult == ElectionModule.ElectionResult.GOT_MAJORITY) {
                return true;
            } else if (electionResult == ElectionModule.ElectionResult.LEADER_FOUND) {
                return false;
            } else {
                refreshableTimer.startRandomized(config.getElectionTimeoutFrom(), config.getElectionTimeoutTo());
            }
        } while (electionResult == ElectionModule.ElectionResult.GOT_MINORITY);
        throw new IllegalStateException("Unexpected error.");
    }


    public synchronized RequestVoteResponse handleVoteRequest(RequestVoteRequest request) {
        if (request.getTerm() < getCurrentTerm()) {
            logger.info("Denying candidate vote request with id {} - candidate is outdated.", request.getCandidateId());
            return new RequestVoteResponse(currentTerm, false);
        } else if (votedFor == null || votedFor.equals(request.getCandidateId())) {
            logger.info("Approving candidate vote request with id {}.", request.getCandidateId());
            votedFor = request.getCandidateId();
            return new RequestVoteResponse(currentTerm, true);
        } else {
            logger.info("Denying candidate vote request with id {} - already voted in this term.", request.getCandidateId());
            return new RequestVoteResponse(currentTerm, false);
        }
    }

    private void startSendingHeartbeat() {
        heartbeatSender.startSending();
    }

    public void handleAppendEntries() {
        if (status == NodeStatus.FOLLOWER) {
            refreshableTimer.refreshRandomized(config.getElectionTimeoutFrom(), config.getElectionTimeoutTo());
        }
    }
}
