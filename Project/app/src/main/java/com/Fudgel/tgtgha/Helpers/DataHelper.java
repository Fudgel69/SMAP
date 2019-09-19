package com.Fudgel.tgtgha.Helpers;

import com.Fudgel.tgtgha.Model.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class DataHelper {

    private String USERS = "Users";
    private String USERNAME = "UserName";

    public DataHelper(){}

    public String GetName(DataSnapshot dataSnapshot){
        String User = (String) dataSnapshot.child(USERS).child(FirebaseAuth.getInstance().getUid()).child(USERNAME).getValue();
        return User;
    }

    public List<ChatModel> GetChats(DataSnapshot dataSnapshot){

    }

    public List<MessageModel> GetMessages(DataSnapshot dataSnapshot){

    }
}
