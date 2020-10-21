package com.github.nestorowicz.yacht.core;

import com.github.nestorowicz.yacht.config.Config;
import com.github.nestorowicz.yacht.rpc.AppendEntriesHandler;
import com.github.nestorowicz.yacht.rpc.RPCException;
import com.github.nestorowicz.yacht.rpc.RemoteProcedureCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

// TODO Under construction
public class AppendEntriesSender {

    private final Logger logger = LoggerFactory.getLogger(AppendEntriesSender.class);

    private final Config config;
    private final RemoteProcedureCaller remoteProcedureCaller;

    public AppendEntriesSender(Config config, RemoteProcedureCaller remoteProcedureCaller) {
        this.config = config;
        this.remoteProcedureCaller = remoteProcedureCaller;
    }

    public void send() {
        for (String node : config.getNodes()) {
            try {
                remoteProcedureCaller.call(node + AppendEntriesHandler.PATH, "{}", Map.class);
            } catch (RPCException e) {
                logger.error("Error while sending AppendEntries request: {}", e.getMessage());
                logger.debug("Error while sending AppendEntries request. ", e);
            }
        }
    }
}
