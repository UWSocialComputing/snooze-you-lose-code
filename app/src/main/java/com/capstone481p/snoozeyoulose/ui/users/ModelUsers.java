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

    public void setBedTime(String bedTime) { this.bedTime = bedTime; }
    public String getBedTime() { return bedTime; }

    public void setWakeupTime(String wakeupTime) { this.wakeupTime = wakeupTime; }
    public String getWakeupTime() { return wakeupTime; }

    public void setAwakeCount(String awakeCount) { this.awakeCount = awakeCount; }
    public String getAwakeCount() { return awakeCount; }

    public void setSleepCount(String sleepCount) { this.sleepCount = sleepCount; }
    public String getSleepCount() { return sleepCount; }

    public void setCounter(String counter) { this.counter = counter; }
    public String getCounter() { return counter; }

    public void setOnlineStatus(String onlineStatus){this.onlineStatus = onlineStatus;}

    public String getOnlineStatus(){return onlineStatus;}

    public ModelUsers(String name, String onlineStatus, String typingTo, String email, String image, String uid, String accountability, String bedTime, String wakeupTime, String awakeCount, String sleepCount, String counter) {
        this.name = name;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.email = email;
        this.image = image;
        this.uid = uid;
        this.accountability = accountability;
        this.bedTime = bedTime;
        this.wakeupTime = wakeupTime;
        this.awakeCount = awakeCount;
        this.sleepCount = sleepCount;
        this.counter = counter;
    }

    String email;

    String image;

    String uid;

    String accountability;

    String bedTime;

    String wakeupTime;

    String awakeCount;

    String sleepCount;

    String counter;
}
