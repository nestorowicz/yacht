package com.github.nestorowicz.yacht.rpc;

import com.github.nestorowicz.yacht.config.Config;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Server {

    private final Logger logger = LoggerFactory.getLogger(Server.class);

    private final org.eclipse.jetty.server.Server server;

    public Server(Config config, RaftHandler[] raftHandlers) {
        this.server = new org.eclipse.jetty.server.Server(config.getPort());
        logger.info("Raft handlers: {}", Arrays.toString(raftHandlers));
        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);
        for (RaftHandler raftHandler : raftHandlers) {
            servletHandler.addServletWithMapping(raftHandler.getServlet(), raftHandler.getPath());
        }
    }

    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void join() {
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
