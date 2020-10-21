package com.github.nestorowicz.yacht;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nestorowicz.yacht.config.SystemPropertiesConfig;
import com.github.nestorowicz.yacht.core.*;
import com.github.nestorowicz.yacht.rpc.AppendEntriesHandler;
import com.github.nestorowicz.yacht.rpc.RemoteProcedureCaller;
import com.github.nestorowicz.yacht.rpc.RequestVoteHandler;
import com.github.nestorowicz.yacht.rpc.Server;
import com.github.nestorowicz.yacht.util.DateTimeUtil;
import com.github.nestorowicz.yacht.util.RefreshableTimer;
import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;

public class RaftApplication {

    public static void main(String[] args) {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);

        MutablePicoContainer container = new PicoBuilder().withReflectionLifecycle().withCaching().build();
        container.addComponent(HttpClient.newHttpClient());
        container.addComponent(ObjectMapper.class);
        container.addComponent(RequestVoteHandler.class);
        container.addComponent(AppendEntriesHandler.class);
        container.addComponent(AppendEntriesSender.class);
        container.addComponent(Server.class);
        container.addComponent(SystemPropertiesConfig.class);
        container.addComponent(DateTimeUtil.class);
        container.addComponent(RemoteProcedureCaller.class);
        container.addComponent(HeartbeatSender.class);
        container.as(Characteristics.NO_CACHE).addComponent(RefreshableTimer.class);
        container.addComponent(ElectionModule.class);
        container.addComponent(ConsensusModule.class);
        container.start();
        Server component = container.getComponent(Server.class);
        ConsensusModule consensusModule = container.getComponent(ConsensusModule.class);
        consensusModule.startConsensusModule(); // TODO get rid of this - ex. start separate thread
        component.join();
    }
}
