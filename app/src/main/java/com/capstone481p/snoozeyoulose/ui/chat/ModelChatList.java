package com.capstone481p.snoozeyoulose.ui.chat;

public class ModelChatList {

    String id;
    public ModelChatList() {
        // This class is used to model information displayed in the chat list
        // so it can be retrieved from the database
    }

    public ModelChatList(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
