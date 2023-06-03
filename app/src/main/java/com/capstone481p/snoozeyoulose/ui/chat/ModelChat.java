package com.capstone481p.snoozeyoulose.ui.chat;

public class ModelChat {
    String message;
    String receiver;
    String sender;
    String type;
    String timestamp;


    public ModelChat() {
        // This class models chat information in order to retrieve info from the database
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ModelChat(String message, String receiver, String sender, String type, String timestamp) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.timestamp = timestamp;
    }



}