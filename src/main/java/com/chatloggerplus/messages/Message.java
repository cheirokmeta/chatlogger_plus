package com.chatloggerplus.messages;

import lombok.ToString;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.util.Text;


@ToString
public class Message {
    private final int timestamp;
    private final long id;
    private final ChatType type;
    private final String channel;
    private final String sender;
    private final int rank;
    private final String message;

    private Message(long id, ChatType type, int timestamp, String channel, String sender, int rank, String message) {
        this.id = id;
        this.type = type;
        this.timestamp = timestamp;
        this.channel = channel;
        this.sender = sender;
        this.rank = rank;
        this.message = message;
    }

    public static Message from(long id, ChatType type, int timestamp, String channel, int rank, ChatMessage chatMessage) {
        String sender = chatMessage.getName().isEmpty() ? channel : Text.removeFormattingTags(chatMessage.getName());
        return new Message(id, type, timestamp, Text.standardize(channel), sender, rank, chatMessage.getMessage());
    }

    public enum ChatType {
        FRIENDS,
        CLAN
    }
}
