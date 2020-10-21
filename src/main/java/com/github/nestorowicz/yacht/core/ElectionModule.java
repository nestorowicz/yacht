package com.github.nestorowicz.yacht.core;

import com.github.nestorowicz.yacht.config.Config;
import com.github.nestorowicz.yacht.rpc.RPCException;
import com.github.nestorowicz.yacht.rpc.RemoteProcedureCaller;
import com.github.nestorowicz.yacht.rpc.RequestVoteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class ElectionModule {

    private final Logger logger = LoggerFactory.getLogger(ElectionModule.class);

    private final Config config;
    private final RemoteProcedureCaller remoteProcedureCaller;

    public ElectionModule(Config config, RemoteProcedureCaller remoteProcedureCaller) {
        this.config = config;
        this.remoteProcedureCaller = remoteProcedureCaller;
    }

    public ElectionResult callElection(Integer currentTerm) {
        logger.info("Starting election for term: {}", currentTerm);
        String[] nodes = config.getNodes();
        logger.info("Nodes: " + Arrays.toString(nodes) + " " + nodes.length);
        if (nodes.length == 0) {
            logger.info("This is a 1 cluster node. Election won.");
            return ElectionResult.GOT_MAJORITY;
        }
        int votes = 0;
        for (String node : nodes) {
            // TODO handle situation when there's not enough responses in election timeout timeframe. (due to http timeouts)
            Optional<RequestVoteResponse> responseOptional = sendRequest(node + RequestVoteHandler.PATH, currentTerm);
            if (responseOptional.isEmpty()) {
                continue;
            }
            RequestVoteResponse response = responseOptional.get();
            votes += response.getVoteGranted() ? 1 : 0;
            if (response.getTerm() > currentTerm) {
                return ElectionResult.LEADER_FOUND;
            }
        }
        if (votes > nodes.length / 2) {
            return ElectionResult.GOT_MAJORITY;
        } else {
            return ElectionResult.GOT_MINORITY;
        }
    }

    private Optional<RequestVoteResponse> sendRequest(String node, Integer currentTerm) {
        RequestVoteRequest request = new RequestVoteRequest(currentTerm, config.getNodeId());
        try {
            RequestVoteResponse response = remoteProcedureCaller.call(node, request, RequestVoteResponse.class);
            return Optional.of(response);
        } catch (RPCException e) {
            logger.warn("Error: " + node + " " + e.getMessage());
            logger.debug("Error: ", e);
            return Optional.empty();
        }
    }

    public enum ElectionResult {
        GOT_MAJORITY, GOT_MINORITY, LEADER_FOUND
    }
}
