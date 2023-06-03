package com.capstone481p.snoozeyoulose.ui.users;

public class ModelUsers {
    String name;

    String email;

    String image;

    String uid;

    String accountability;

    String bedTime;

    String wakeupTime;

    public ModelUsers() {
        // This class is used to model users to add and retrieve user info from a database
    }

    public ModelUsers(String name, String email, String image, String uid, String accountability, String bedTime, String wakeupTime) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.uid = uid;
        this.accountability = accountability;
        this.bedTime = bedTime;
        this.wakeupTime = wakeupTime;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}
