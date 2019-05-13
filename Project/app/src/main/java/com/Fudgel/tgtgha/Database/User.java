package com.Fudgel.tgtgha.Database;

public class User {

    String GUID;
    String userName;
    String userAge;
    String userImageURL;
    String userGender;

    public User (){}

    public User(String GUID, String userName, String userAge, String userImageURL, String userGender) {
        this.GUID = GUID;
        this.userName = userName;
        this.userAge = userAge;
        this.userImageURL = userImageURL;
        this.userGender = userGender;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }
}
