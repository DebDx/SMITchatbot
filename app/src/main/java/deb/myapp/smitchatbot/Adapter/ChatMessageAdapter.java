package deb.myapp.smitchatbot.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import deb.myapp.smitchatbot.Model.ChatMessage;
import deb.myapp.smitchatbot.R;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private static final int MY_MESSAGE=0;
    private static final int BOT_MESSAGE=1;

    public ChatMessageAdapter(@NonNull Context context, List<ChatMessage> data) {
        super(context, R.layout.user_query_layout,data);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);
        if(item.isMine() && !item.isImage()){
            return MY_MESSAGE;
        }
        else{
            return BOT_MESSAGE;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        int viewType =  getItemViewType(position);
        if(viewType == MY_MESSAGE){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_query_layout,parent,false);

            TextView textView = convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());
        }
        else if(viewType == BOT_MESSAGE){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bot_reply_layout,parent,false);

            TextView textView = convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());

        }
        convertView.findViewById(R.id.chatMessageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Clicked...",Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}