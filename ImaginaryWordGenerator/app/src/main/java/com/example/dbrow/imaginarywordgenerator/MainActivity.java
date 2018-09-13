package com.example.dbrow.imaginarywordgenerator;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_KEY = "IWG_LOG";

    private WordGenerator wordGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        wordGenerator = new WordGenerator(this);

        initSeekbar();

        initGenerateButton();

        initViewSavedButton();

        showSavedWordsFragment();
    }


    private void initSeekbar(){
        final SeekBar seekBar = findViewById(R.id.numOfWordsSeekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView label = findViewById(R.id.seekerViewLabel);
                label.setText(Integer.toString(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initGenerateButton(){
        final Button button = findViewById(R.id.generateButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count = getCount();
                if(count < 1) { return; }

                String[] words = wordGenerator.getWords(count, getProper(), getPunctuate());

                showWordListFragment(words);
            }
        });
    }

    private void initViewSavedButton(){
        final Button button = findViewById(R.id.viewSavedButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavedWordsFragment();
            }
        });

    }

    private int getCount(){
        TextView label = findViewById(R.id.seekerViewLabel);

        return Integer.parseInt(label.getText().toString());
    }

    private boolean getProper(){
        CheckBox proper = findViewById(R.id.properCheckbox);
        return proper.isChecked();
    }

    private boolean getPunctuate(){
        CheckBox punctuate = findViewById(R.id.punctuateCheckbox);
        return punctuate.isChecked();
    }

    private void showWordListFragment(String[] words){
        Bundle wordListBundle = new Bundle();
        wordListBundle.putStringArray(WordListFragment.WORDLIST_KEY, words);

        WordListFragment wordListFragment = new WordListFragment();
        wordListFragment.setArguments(wordListBundle);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.wordListFragment, wordListFragment).commit();
    }

    private void showSavedWordsFragment(){

        SavedWordsFragment savedWordsFragment = new SavedWordsFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.wordListFragment, savedWordsFragment).commit();

    }
}
