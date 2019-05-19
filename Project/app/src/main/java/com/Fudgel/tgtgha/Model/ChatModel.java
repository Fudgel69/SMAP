package com.Fudgel.tgtgha.Model;

public class ChatModel {

    private String ID;
    private String Message;
    private String Name;

    public ChatModel(){ }

    public String getID() { return ID; }

    public void setID(String id) {
        ID = id;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) { Message = message; }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
