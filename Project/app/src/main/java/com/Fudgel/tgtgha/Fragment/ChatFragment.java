package com.Fudgel.tgtgha.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Fudgel.tgtgha.Adapter.ChatAdapter;
import com.Fudgel.tgtgha.Model.ChatModel;
import com.Fudgel.tgtgha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


public class ChatFragment extends Fragment {

    private DatabaseReference dbRef;
    private EditText textMessage;
    private ChatAdapter chatAdapter;

    FirebaseDatabase fDB;
    FirebaseUser fbUser;
    DatabaseReference dbUser;
    DatabaseReference dbRefer;

    String Username;
    String UserID;
    String chat;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        fDB = FirebaseDatabase.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        Username = fbUser.getDisplayName();
        UserID = fbUser.getUid();

        dbRefer = fDB.getReference();
        dbUser = fDB.getReference("Users/" + UserID);

        setupConnection();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        textMessage = root.findViewById(R.id.chat_input);
        textMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                ChatModel data = new ChatModel();

                data.setMessage(textMessage.getText().toString());
                data.setID(UserID);
                data.setName(Username);

                dbRefer.child("chats").child(chat).child(String.valueOf(new Date().getTime())).setValue(data);

                clearEditText();

                return true;
            }
        });

        RecyclerView chat = root.findViewById(R.id.chat_message);
        chat.setLayoutManager(new LinearLayoutManager(getContext()));

        chatAdapter = new ChatAdapter();
        chat.setAdapter(chatAdapter);

        return root;
    }

    private void clearEditText() {
        InputMethodManager man = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        man.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        textMessage.setText("");
    }

    private void setupConnection() {

        dbRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Chat","SUCCESS!");
                handleData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Chat","ERROR: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Connection refused!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleData(DataSnapshot dataSnapshot) {
        chatAdapter.clearMessages();

        chat = dataSnapshot.child("Users").child(UserID).child("Chat").getValue().toString();

        DataSnapshot chats = dataSnapshot.child("chats").child(chat);

        for(DataSnapshot item : chats.getChildren()) {
            ChatModel data = item.getValue(ChatModel.class);
            chatAdapter.addMessage(data);
        }

        chatAdapter.notifyDataSetChanged();
    }
}
