package com.github.nestorowicz.yacht.core;

import com.github.nestorowicz.yacht.util.DateTimeUtil;
import com.github.nestorowicz.yacht.util.RefreshableTimer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@ExtendWith(MockitoExtension.class)
class RefreshableTimerTest {

    public static final long ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;
    private TestTimeUtil testTimeUtil;
    @Mock
    private DateTimeUtil dateTimeUtilMock;
    private RefreshableTimer refreshableTimer;

    @BeforeEach
    public void setup() {
        testTimeUtil = new TestTimeUtil();
        refreshableTimer = new RefreshableTimer(testTimeUtil);
    }

    @Test
    public void shouldTimeOutWhenNextTimeoutPasses() {
        refreshableTimer = new RefreshableTimer(dateTimeUtilMock);
        Mockito.when(dateTimeUtilMock.getCurrentTimeMillis()).thenReturn(0L, ONE_DAY_MILLIS + 1);
        Assertions.assertTimeoutPreemptively(Duration.of(60, SECONDS), () -> {
            refreshableTimer.start(ONE_DAY_MILLIS);
        });
    }

    @Test
    public void shouldTimeOutAfterRefresh() {
        refreshableTimer = new RefreshableTimer(testTimeUtil);
        Thread timerThread = new Thread(() -> refreshableTimer.start(ONE_DAY_MILLIS));
        testTimeUtil.currentTime = 0;

        Assertions.assertTimeoutPreemptively(Duration.of(60, SECONDS), () -> {
            timerThread.start();
            while (timerThread.getState() != Thread.State.TIMED_WAITING) {
            }
            refreshableTimer.refresh(1);
            testTimeUtil.currentTime = 2;
            while (timerThread.isAlive()) {
            }
        });
    }

    // TODO Add tests for randomized methods

    private static class TestTimeUtil extends DateTimeUtil {
        public volatile long currentTime = 0L;

        @Override
        public long getCurrentTimeMillis() {
            return currentTime;
        }
    }
}