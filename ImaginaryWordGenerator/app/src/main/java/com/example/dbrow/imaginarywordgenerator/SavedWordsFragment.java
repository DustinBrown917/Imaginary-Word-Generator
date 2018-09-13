package com.example.dbrow.imaginarywordgenerator;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SavedWordsFragment extends Fragment {
    public static final String SAVEDLIST_KEY = "SDL";

    private SavedWordsList savedWordsList;

    public SavedWordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedWordsList = new SavedWordsList(getActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.fragment_saved_words, container, false);

        ListView listView = view.findViewById(R.id.savedWordsList);

        if(savedWordsList.getSize() == 0){
            return null;
        }

        SavedWordsFragment.WordListItemAdapter adapter = new SavedWordsFragment.WordListItemAdapter(getActivity().getApplicationContext(), savedWordsList.getSavedWords());

        listView.setAdapter(adapter);

        return view;
    }

    private class WordListItemAdapter extends ArrayAdapter<String> {

        private List<String> words;

        private WordListItemAdapter(Context context, List<String> words){
            super(context,  -1, words);
            this.words = words;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent){

            final SavedWordViewHolder holder;

            if(convertView == null){
                convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(
                        R.layout.saved_word_list_item,
                        parent,
                        false
                );
                holder = new SavedWordViewHolder();
                holder.wordText = convertView.findViewById(R.id.savedWordText);
                holder.deleteItemButton = convertView.findViewById(R.id.deleteItemButton);

                convertView.setTag(holder);
            } else {
                holder = (SavedWordViewHolder) convertView.getTag();
            }

            holder.deleteItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), words.get(position) + " Deleted!",
                            Toast.LENGTH_SHORT).show();
                    savedWordsList.removeWordAt(position);
                    words.remove(position);
                    notifyDataSetChanged();
                }
            });

            holder.wordText.setText(words.get(position));

            return convertView;
        }

    }

    public static class SavedWordViewHolder{
        public TextView wordText;
        public ImageButton deleteItemButton;
    }

}
