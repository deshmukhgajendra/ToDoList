package com.example.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EmojiAdapter extends ArrayAdapter<String> {

    Context context;
    String[] emojis;


    public EmojiAdapter(@NonNull Context context, String[] emojis) {
        super(context, R.layout.emoji_item,emojis);
        this.context=context;
        this.emojis=emojis;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.emoji_item, parent, false);
        }

        TextView emojiText = convertView.findViewById(R.id.emojiText);
        emojiText.setText(emojis[position]);

        return convertView;
    }
}
