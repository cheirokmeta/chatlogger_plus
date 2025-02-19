package com.chatloggerplus;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;


@ConfigGroup("chatloggerplus")
public interface ChatLoggerPlusConfig extends Config {

    String CHAT_LOGGER_PLUS_GROUP = "chatloggerplus";

    @ConfigSection(
            name = "Channel Select",
            description = "Enable/Disable chat channels",
            position = 0
    )
    String CHANNEL_SECT = "Channel Select";

    @ConfigSection(
            name = "Logging Options",
            description = "Change Logging behaviour",
            position = 10
    )
    String LOGGING_SECT = "Logging Options";

    @ConfigSection(
            name = "Remote Submission",
            description = "Settings for remote submission of chat messages",
            position = 20,
            closedByDefault = true
    )
    String REMOTE_SECT = "remote";

    // Channel Select
    @ConfigItem(
            section = CHANNEL_SECT,
            keyName = "game",
            name = "Game Chat",
            description = "Enable logging of Game Chat messages"
    )
    default boolean logGameChat() {
        return false;
    }

    @ConfigItem(
            section = CHANNEL_SECT,
            keyName = "public",
            name = "Public Chat",
            description = "Enables logging of Public Chat messages"
    )
    default boolean logPublicChat() {
        return false;
    }

    @ConfigItem(
            section = CHANNEL_SECT,
            keyName = "private",
            name = "Private Chat",
            description = "Enables logging of Private Chat messages"
    )
    default boolean logPrivateChat() {
        return true;
    }

    @ConfigItem(
            section = CHANNEL_SECT,
            keyName = "friends",
            name = "Friends Chat",
            description = "Enables logging of Friends Chat messages"
    )
    default boolean logFriendsChat() {
        return true;
    }

    @ConfigItem(
            section = CHANNEL_SECT,
            keyName = "clan",
            name = "Clan Chat",
            description = "Enables logging of Clan Chat messages"
    )
    default boolean logClanChat() {
        return true;
    }

    @ConfigItem(
            section = CHANNEL_SECT,
            keyName = "group_iron",
            name = "Group Iron Chat",
            description = "Enables logging of Group Ironman chat messages"
    )
    default boolean logGroupChat() {
        return true;
    }


    // Logging Config

    @ConfigItem(
            section = LOGGING_SECT,
            keyName = "file_format",
            name = "File Format",
            description = "The format of the log file"
    )

    default FileFormat fileFormat() {
        return FileFormat.TEXT;
    }

    @ConfigItem(
            section = LOGGING_SECT,
            keyName = "per_user",
            name = "Folder Per User",
            description = "Splits chats up into folders per logged in user"
    )
    default boolean logChatPerUser() {
        return true;
    }

    @ConfigItem(
            section = LOGGING_SECT,
            keyName = "archive_count",
            name = "Archive Count",
            description = "Number of archived days of chat to save (0 for infinite)"
    )
    default int archiveCount() {
        return 30;
    }

    // Remote Submission


    @ConfigItem(
            position = 0,
            section = REMOTE_SECT,
            keyName = "remotelogclan",
            name = "Remote Clan Chat",
            description = "Enables remote submission of Clan Chat messages"
    )
    default boolean remoteSubmitLogClanChat() {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "remotelogfriends",
            name = "Remote Friends Chat",
            description = "Enables remote submission of Friends Chat messages",
            section = REMOTE_SECT
    )
    default boolean remoteSubmitLogFriendsChat() {
        return false;
    }

    @ConfigItem(
            position = 2,
            section = REMOTE_SECT,
            keyName = "remotelogpgame",
            name = "Remote Game Chat",
            description = "Enables remote submission of Game Chat messages"
    )
    default boolean remoteSubmitLogGameChat() {
        return false;
    }

    @ConfigItem(
            position = 3,
            section = REMOTE_SECT,
            keyName = "remoteloggroup",
            name = "Remote Group Iron Chat",
            description = "Enables remote submission of Group Ironman chat messages"
    )
    default boolean remoteSubmitLogGroupChat() {
        return false;
    }

    @ConfigItem(
            position = 4,
            section = REMOTE_SECT,
            keyName = "remotelogprivate",
            name = "Remote Private Chat",
            description = "Enables remote submission of Private Chat messages"
    )
    default boolean remoteSubmitLogPrivateChat() {
        return false;
    }

    @ConfigItem(
            position = 5,
            section = REMOTE_SECT,
            keyName = "remotelogpublic",
            name = "Remote Public Chat",
            description = "Enables remote submission of Public Chat messages"
    )
    default boolean remoteSubmitLogPublicChat() {
        return false;
    }

    @ConfigItem(
            position = 99,
            section = REMOTE_SECT,
            keyName = "remoteendpoint",
            name = "Endpoint",
            description = "The endpoint that messages will be submitted to"
    )
    default String remoteEndpoint() {
        return null;
    }

    @ConfigItem(
            position = 100,
            section = REMOTE_SECT,
            keyName = "remoteauthorization",
            name = "Authorization",
            description = "The Authorization header value"
    )
    default String remoteEndpointAuthorization() {
        return null;
    }
}