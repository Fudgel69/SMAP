package com.Fudgel.tgtgha.Model;

public class RouteModel {

    private String ID;
    private String Time;
    private String Goal;
    private String Users;

    public RouteModel(){
        Users = "";
    }

    public String getID() { return ID; }

    public void setID(String id) {
        ID = id;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String message) { Time = message; }

    public String getGoal() {
        return Goal;
    }

    public void setGoal(String name) {
        Goal = name;
    }

}
