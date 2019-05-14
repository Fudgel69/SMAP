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

    FirebaseUser User;

    String Username;
    String UserID;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        User = FirebaseAuth.getInstance().getCurrentUser();
        Username = User.getDisplayName();
        UserID = User.getUid();

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

                dbRef.child(String.valueOf(new Date().getTime())).setValue(data);

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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("chats/Chat1");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Chat","SUCCESS!");
                handleReturn(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Chat","ERROR: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Connection refused!", Toast.LENGTH_SHORT).show();
                //mCallback.logout();
            }
        });
    }

    private void handleReturn(DataSnapshot dataSnapshot) {
        chatAdapter.clearMessages();

        for(DataSnapshot item : dataSnapshot.getChildren()) {
            ChatModel data = item.getValue(ChatModel.class);
            chatAdapter.addMessage(data);
        }

        chatAdapter.notifyDataSetChanged();
    }
}
