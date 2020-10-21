package com.github.nestorowicz.yacht.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nestorowicz.yacht.core.ConsensusModule;
import com.github.nestorowicz.yacht.core.RequestVoteRequest;
import com.github.nestorowicz.yacht.core.RequestVoteResponse;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class RequestVoteHandler extends RaftHandler {

    public static final String PATH = "/requestVote";
    private final Logger logger = LoggerFactory.getLogger(RequestVoteHandler.class);

    private final ObjectMapper objectMapper;
    private final ConsensusModule consensusModule;

    public RequestVoteHandler(ObjectMapper objectMapper, ConsensusModule consensusModule) {
        this.objectMapper = objectMapper;
        this.consensusModule = consensusModule;
    }

    @Override
    protected ServletHolder getServlet() {
        return new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                String requestString = req.getReader().lines().collect(Collectors.joining("\n"));
                logger.info("Received vote request: {}", requestString);
                RequestVoteRequest request = objectMapper.readValue(requestString, RequestVoteRequest.class);
                RequestVoteResponse response = consensusModule.handleVoteRequest(request);
                String responseString = objectMapper.writeValueAsString(response);
                resp.getWriter().write(responseString);
            }
        });
    }

    @Override
    protected String getPath() {
        return PATH;
    }
}
