package com.capstone481p.snoozeyoulose.ui.users;

public class ModelUsers {
    String name;

    public ModelUsers() {
    }

    String onlineStatus;
    String typingTo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setAccountability(String accountability) { this.accountability = accountability; }

    public String getAccountability() { return accountability; }
    public ModelUsers(String name, String onlineStatus, String typingTo, String email, String image, String uid, String accountability) {
        this.name = name;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.email = email;
        this.image = image;
        this.uid = uid;
        this.accountability = accountability;
    }

    String email;

    String image;

    String uid;

    String accountability;
}
