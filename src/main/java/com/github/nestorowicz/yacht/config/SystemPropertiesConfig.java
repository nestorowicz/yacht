package com.github.nestorowicz.yacht.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// TODO add validation, probably in a separate class for reuse
public class SystemPropertiesConfig implements Config {

    private final Logger logger = LoggerFactory.getLogger(SystemPropertiesConfig.class);

    private Properties properties = new Properties();
    private String nodeId;
    private String[] nodes;
    private Integer electionTimeoutFrom;
    private Integer electionTimeoutTo;

    public SystemPropertiesConfig() {
        String configPath = System.getProperty("raft.config.path", "");
        if (!configPath.isBlank()) {
            loadProperties(configPath);
        }
        System.getProperties().forEach((key, val) -> properties.setProperty(key.toString(), val.toString()));
        nodeId = properties.getProperty("raft.node.id");
        nodes = initNodes();
        electionTimeoutFrom = Integer.valueOf(properties.getProperty("raft.electionTimeout.from"));
        electionTimeoutTo = Integer.valueOf(properties.getProperty("raft.electionTimeout.to"));
    }

    private void loadProperties(String configPath) {
        logger.info("Reading properties from {}", configPath);
        try (FileInputStream stream = new FileInputStream(configPath)) {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] initNodes() {
        String property = properties.getProperty("raft.nodes", "");
        return property.split(",");
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    public String[] getNodes() {
        return nodes;
    }

    public Integer getElectionTimeoutFrom() {
        return electionTimeoutFrom;
    }

    public Integer getElectionTimeoutTo() {
        return electionTimeoutTo;
    }

    @Override
    public Integer getPort() {
        return Integer.valueOf(properties.getProperty("raft.port"));
    }

    @Override
    public Integer getHeartbeatInterval() {
        return Integer.valueOf(properties.getProperty("raft.heartbeatInterval"));
    }
}
