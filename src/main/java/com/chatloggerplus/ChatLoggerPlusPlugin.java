package com.chatloggerplus;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.chatloggerplus.statemanagement.PlayerStateManager;
import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import okhttp3.OkHttpClient;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;


@Slf4j
@PluginDescriptor(
        name = "Chat Logger+"
)
public class ChatLoggerPlusPlugin extends Plugin {

    private static final String BASE_DIRECTORY = RuneLite.RUNELITE_DIR + "/chatlogs/";
    private static final int CHANNEL_UNRANKED = -2;

    @Inject
    private Client client;

    @Inject
    private ChatLoggerPlusConfig config;

    @Inject
    private OkHttpClient httpClient;

    @Inject
    private Gson gson;
    private Logger publicChatLogger;
    private Logger privateChatLogger;
    private Logger friendsChatLogger;
    private Logger clanChatLogger;
    private Logger groupChatLogger;
    private Logger gameChatLogger;

    private boolean canLoad = false;

    private PlayerStateManager playerStateManager;

    @Provides
    ChatLoggerPlusConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ChatLoggerPlusConfig.class);
    }

    private void setCanLoad(boolean canLoad) {
        this.canLoad = canLoad;
    }

    private void triggerInit(Optional<GameStateChanged> event) {
        playerStateManager.setLocalPlayerInformation(event);
        setCanLoad(playerStateManager.getLocalPlayerName() != null);
    }

    @Override
    protected void startUp() {
        playerStateManager = new PlayerStateManager(client);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!ChatLoggerPlusConfig.CHAT_LOGGER_PLUS_GROUP.equals(event.getGroup())) {
            return;
        }
        // If we need to reload loggers
        if (event.getKey().equals("per_user") || event.getKey().equals("archive_count")) {
            setCanLoad(true);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        triggerInit(Optional.ofNullable(event));
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        // If we are logging per player, wait until we have the player login name
        if (canLoad && (!config.logChatPerUser() || client.getLocalPlayer().getName() != null)) {
            initLoggers();
            canLoad = false;
        }
    }

    private void initLoggers() {
        publicChatLogger = setupLogger("PublicChatLogger", "public");
        privateChatLogger = setupLogger("PrivateChatLogger", "private");
        friendsChatLogger = setupLogger("FriendsChatLogger", "friends");
        clanChatLogger = setupLogger("ClanChatLogger", "clan");
        groupChatLogger = setupLogger("GroupChatLogger", "group");
        gameChatLogger = setupLogger("GameChatLogger", "game");
    }

    private int friendsChatMemberRank(String name) {
        FriendsChatManager friendsChatManager = client.getFriendsChatManager();
        if (friendsChatManager != null) {
            FriendsChatMember member = friendsChatManager.findByName(Text.removeTags(name));
            return member != null ? member.getRank().getValue() : CHANNEL_UNRANKED;
        }
        return CHANNEL_UNRANKED;
    }

    private int clanChannelMemberRank(String name, String clanName) {
        String cleanName = Text.removeTags(name);
        ClanChannel clanChannel = client.getClanChannel();

        if (clanChannel != null) {
            ClanChannelMember member = clanChannel.findMember(cleanName);
            if (member != null && clanChannel.getName().equals(clanName)) {
                return member.getRank().getRank();
            }
        }
        clanChannel = client.getGuestClanChannel();

        if (clanChannel != null) {
            ClanChannelMember member = clanChannel.findMember(cleanName);
            if (member != null && clanChannel.getName().equals(clanName)) {
                return member.getRank().getRank();
            }
        }
        return CHANNEL_UNRANKED;
    }


    @Subscribe
    public void onChatMessage(ChatMessage event) {
        MessageNode node = event.getMessageNode();
        ChatMessageType type = node.getType();
    }

    private Logger setupLogger(String loggerName, String subFolder) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{HH:mm:ss} %msg%n");
        encoder.start();

        String directory = BASE_DIRECTORY;

        if (config.logChatPerUser()) {
            directory += client.getLocalPlayer().getName() + "/";
        }

        directory += subFolder + "/";

        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setFile(directory + "latest.log");
        appender.setAppend(true);
        appender.setEncoder(encoder);
        appender.setContext(context);

        TimeBasedRollingPolicy<ILoggingEvent> logFilePolicy = new TimeBasedRollingPolicy<>();
        logFilePolicy.setContext(context);
        logFilePolicy.setParent(appender);
        logFilePolicy.setFileNamePattern(directory + "chatlog_%d{yyyy-MM-dd}.log");
        logFilePolicy.setMaxHistory(config.archiveCount());
        logFilePolicy.start();

        appender.setRollingPolicy(logFilePolicy);
        appender.start();

        Logger logger = context.getLogger(loggerName);
        logger.detachAndStopAllAppenders();
        logger.setAdditive(false);
        logger.setLevel(Level.INFO);
        logger.addAppender(appender);

        return logger;
    }
}
