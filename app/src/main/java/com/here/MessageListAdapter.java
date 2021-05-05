package com.here;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.here.models.LocalBusiness;
import com.squareup.picasso.Picasso;

import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> messageList;

    public MessageListAdapter(Context mContext, List<Message> messageList) {
        this.mContext = mContext;
        this.messageList = messageList;
    }

    public void setList(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getType() == 1) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView messageBody, timeText, nameText;
        ImageView profilePicture;
        RecyclerView businessRecyclerView;
        ImageView imageView;
        ResponseAdapter responseAdapter;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.received_message_body);
            timeText = itemView.findViewById(R.id.received_message_timestamp);
            nameText = itemView.findViewById(R.id.recevied_message_sender_name);
            profilePicture = itemView.findViewById(R.id.received_message_sender_profile_picture);
            imageView = itemView.findViewById(R.id.iv_image);

            responseAdapter = new ResponseAdapter(mContext);

        }

        void bind(Message message) {

            messageBody.setText(message.getMessage());
            timeText.setText(DateUtils.formatDateTime(mContext, message.getCreatedAt(), DateUtils.FORMAT_SHOW_TIME));

            String[] test = message.getMessage().split(":");
            if(test.length>=3) {
                if(test[1].equals("http") || test[1].equals("https")) {
                    StringBuilder fin = new StringBuilder("https:");
                    for(int i=2; i<test.length; i++) {
                        fin.append(test[i]);
                    }
                    if(test[0].equals("m")) {
                        messageBody.setText("Here is a meme for you!");
                    } else if(test[0].equals("i")) {
                        messageBody.setText("Here is something inspirational for you!");
                    } else {
                        messageBody.setText("Here is something which might help you!");
                    }

                    Picasso.get()
                            .load(fin.toString())
                            .into(imageView);

                } else {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }
            nameText.setText(message.getSender().getName());

        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageBody, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.sent_message_body);
            timeText = itemView.findViewById(R.id.sent_message_timestamp);
        }

        void bind(Message message) {
            messageBody.setText(message.getMessage());
            timeText.setText(DateUtils.formatDateTime(mContext, message.getCreatedAt(), DateUtils.FORMAT_SHOW_TIME));
        }
    }
}
