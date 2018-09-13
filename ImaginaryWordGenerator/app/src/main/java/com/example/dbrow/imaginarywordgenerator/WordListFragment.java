package com.example.dbrow.imaginarywordgenerator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WordListFragment extends Fragment {

    public static final String WORDLIST_KEY = "WORDLIST_DATA";
    private static final String LOG_KEY = "WLF";
    private SavedWordsList savedWordsList;

    public WordListFragment(){
        //Required empty constructor
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState ){
        savedWordsList = new SavedWordsList(getActivity().getApplicationContext());
        Log.d(LOG_KEY, "word list created");
        View view = inflater.inflate(R.layout.fragment_word_list, container, false);

        ListView listView = view.findViewById(R.id.wordList);

        String[] words = getArguments().getStringArray(WORDLIST_KEY);

        if(words == null || words.length == 0){
            return null;
        }

        WordListItemAdapter adapter = new WordListItemAdapter(getActivity().getApplicationContext(), Arrays.asList(words));

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

            final WordViewHolder holder;

            if(convertView == null){
                convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(
                        R.layout.word_list_item,
                        parent,
                        false
                );

                holder = new WordViewHolder();
                holder.wordText = convertView.findViewById(R.id.wordText);
                holder.saveItemButton = convertView.findViewById(R.id.saveItemButton);

                convertView.setTag(holder);
            } else{
                holder = (WordViewHolder)convertView.getTag();
            }

            holder.saveItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d(LOG_KEY, savedWordsList.toString());
                    savedWordsList.addWord(words.get(position));
                    holder.saveItemButton.setEnabled(false);
                    Toast.makeText(getActivity(), words.get(position) + " Saved!",
                            Toast.LENGTH_SHORT).show();
                }
            });

            holder.wordText.setText(words.get(position));

            return convertView;
        }

    }

    public static class WordViewHolder{
        public TextView wordText;
        public ImageButton saveItemButton;
    }

}
