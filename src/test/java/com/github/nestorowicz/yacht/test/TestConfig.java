package com.github.nestorowicz.yacht.test;

import com.github.nestorowicz.yacht.config.Config;

public class TestConfig implements Config {

    public String nodeId = "1";
    public String[] nodes = {};
    public Integer port = 8081;
    public Integer electionTimeoutFrom = 150;
    public Integer electionTimeoutTo = 300;


    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String[] getNodes() {
        return nodes;
    }

    @Override
    public Integer getElectionTimeoutFrom() {
        return electionTimeoutFrom;
    }

    @Override
    public Integer getElectionTimeoutTo() {
        return electionTimeoutTo;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public Integer getHeartbeatInterval() {
        return null;
    }
}
