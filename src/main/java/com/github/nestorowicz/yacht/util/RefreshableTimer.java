package com.github.nestorowicz.yacht.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class RefreshableTimer {

    private final Logger logger = LoggerFactory.getLogger(RefreshableTimer.class);
    private final Object lock = new Object();

    private volatile long nextTimeout = 0L;
    private final DateTimeUtil dateTimeUtil;

    public RefreshableTimer(DateTimeUtil dateTimeUtil) {
        this.dateTimeUtil = dateTimeUtil;
    }

    public void start(long timeoutMillis) {
        synchronized (lock) {
            logger.debug("Starting timer for: " + timeoutMillis);
            refresh(timeoutMillis);
            long currentTimeMillis = dateTimeUtil.getCurrentTimeMillis();
            while (currentTimeMillis < nextTimeout) {
                logger.debug("Starting wait for: {}", nextTimeout - currentTimeMillis);
                try {
                    lock.wait(nextTimeout - currentTimeMillis);
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Thread interrupted.", e);
                }
                currentTimeMillis = dateTimeUtil.getCurrentTimeMillis();
            }
        }
    }

    public void startRandomized(long from, long to) {
        start(getRandomizedTimeout(from, to));
    }

    public void refresh(long timeoutMillis) {
        synchronized (lock) {
            logger.debug("Refreshing timeout to: {}", timeoutMillis);
            if (timeoutMillis < 0) {
                throw new IllegalStateException("Timeout cannot be negative.");
            }
            nextTimeout = dateTimeUtil.getCurrentTimeMillis() + timeoutMillis;
            lock.notifyAll();
        }
    }

    public void refreshRandomized(long from, long to) {
        refresh(getRandomizedTimeout(from, to));
    }

    private long getRandomizedTimeout(long from, long to) {
        return ThreadLocalRandom.current().nextLong(from, to);
    }
}
