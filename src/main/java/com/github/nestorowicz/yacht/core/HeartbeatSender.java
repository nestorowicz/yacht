package com.github.nestorowicz.yacht.core;

import com.github.nestorowicz.yacht.config.Config;
import com.github.nestorowicz.yacht.util.RefreshableTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO Under construction
public class HeartbeatSender {

    private final Logger logger = LoggerFactory.getLogger(HeartbeatSender.class);

    private final Config config;
    private final AppendEntriesSender appendEntriesSender;
    private final RefreshableTimer refreshableTimer;

    public HeartbeatSender(Config config, AppendEntriesSender appendEntriesSender, RefreshableTimer refreshableTimer) {
        this.config = config;
        this.appendEntriesSender = appendEntriesSender;
        this.refreshableTimer = refreshableTimer;
    }

    public void startSending() {
        while(true) {
            refreshableTimer.start(config.getHeartbeatInterval());
            appendEntriesSender.send();
        }
    }
}
