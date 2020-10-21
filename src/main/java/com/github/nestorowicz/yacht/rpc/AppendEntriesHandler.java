package com.github.nestorowicz.yacht.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nestorowicz.yacht.core.ConsensusModule;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

// TODO Under construction
public class AppendEntriesHandler extends RaftHandler {

    public static final String PATH = "/appendEntries";
    private final Logger logger = LoggerFactory.getLogger(AppendEntriesHandler.class);

    private final ObjectMapper objectMapper;
    private final ConsensusModule consensusModule;

    public AppendEntriesHandler(ObjectMapper objectMapper, ConsensusModule consensusModule) {
        this.objectMapper = objectMapper;
        this.consensusModule = consensusModule;
    }

    @Override
    protected ServletHolder getServlet() {
        return new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                String requestString = req.getReader().lines().collect(Collectors.joining("\n"));
                logger.debug("Received AppendEntries request: {}", requestString);
                consensusModule.handleAppendEntries();
//                String responseString = objectMapper.writeValueAsString(response);
//                logger.info("RequestVote from {} result is: {}", request.getCandidateId(), requestString);
                resp.getWriter().write("{}");
            }
        });
    }

    @Override
    protected String getPath() {
        return PATH;
    }
}
