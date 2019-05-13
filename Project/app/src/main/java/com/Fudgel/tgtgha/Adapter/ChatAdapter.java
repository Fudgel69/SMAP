package com.Fudgel.tgtgha.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Fudgel.tgtgha.Model.ChatModel;
import com.Fudgel.tgtgha.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    static final class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView message;

        ChatViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.item_username);
            message = view.findViewById(R.id.item_message);
        }
    }

    private List<ChatModel> messages = new ArrayList<>();

    public void clearMessages() {
        messages.clear();
    }

    public void addMessage(ChatModel data) {
        messages.add(data);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatModel data = messages.get(position);

        holder.message.setText(data.getMessage());
        holder.name.setText(data.getName());
    }
}
