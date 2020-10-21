package com.github.nestorowicz.yacht.config;

public interface Config {

    String getNodeId();

    String[] getNodes();

    Integer getElectionTimeoutFrom();

    Integer getElectionTimeoutTo();

    Integer getPort();

    Integer getHeartbeatInterval();
}
