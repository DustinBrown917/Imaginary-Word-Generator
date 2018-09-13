package com.example.dbrow.imaginarywordgenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class WordGenerator {

    private static final String LOG_TAG = "WOG";
    private static final char[] VOWELS = {'A', 'E', 'I', 'O', 'U', 'Y'};
    private static final char[] CONSONANTS = {'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N',
                                                'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Z'};
    private static final char[] PUNCTUATION = {'\'', '-', ' '};

    private static final int WORD_MAX_LENGTH = 9;
    private static final int WORD_MIN_LENGTH = 3;

    private Random r;
    private int consecVowels;
    private int consecConsonants;
    private char[] wordCharacters;

    private GeneratorDatabaseHelper gdbHelper;

    public WordGenerator(Context context){
        r = new Random(System.currentTimeMillis());
        gdbHelper = new GeneratorDatabaseHelper(context);
        try{
            gdbHelper.createDatabase();
        } catch (GeneratorDatabaseException e){
            e.printStackTrace();
        }
    }

    private String getWord(boolean proper, boolean puncutated, int wordLength){

        wordCharacters = new char[wordLength];
        consecConsonants = 0;
        consecVowels = 0;
        int lastPunctuatedIndex = 0;
        buildWordByLetter();

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < wordCharacters.length; i++){
            if(i > 0 && proper){
                wordCharacters[i] = Character.toLowerCase(wordCharacters[i]);
            }
            sb.append(wordCharacters[i]);
            if(puncutated){
                if(lastPunctuatedIndex <= i - 3 && i < wordCharacters.length - 4){
                    sb.append(PUNCTUATION[r.nextInt(PUNCTUATION.length)]);
                    lastPunctuatedIndex = i;
                }
            }
        }

        return sb.toString();
    }

    public String[] getWords(int count, boolean proper, boolean punctuated){
        gdbHelper.openDataBase(SQLiteDatabase.OPEN_READONLY);
        String[] words = new String[count];

        for(int i = 0; i < count; i++){
            int length = r.nextInt((WORD_MAX_LENGTH - WORD_MIN_LENGTH) + 1) + WORD_MIN_LENGTH;
            words[i] = getWord(proper, punctuated, length);
        }

        gdbHelper.close();
        return words;
    }

    private void buildWordByLetter(){
        //Get the first letter of the word.
        wordCharacters[0] = getRandomLetter();
        ArrayList<Character> retrievedCharacters;

        int index = 1;

        while(index < wordCharacters.length){

            String query;

            if(index == 1){
                //If second letter in word
                if (consecVowels >= 2) {
                    //on a double vowel.
                    query = buildSelectQueryWithInnerJoin(
                            Character.toString(wordCharacters[index - 1]),
                            GeneratorDatabaseHelper.START_TABLE_NAME,
                            GeneratorDatabaseHelper.LETTER_COL_CONSONANTS,
                            GeneratorDatabaseHelper.LETTER_TABLE_NAME
                    );

                } else if (consecConsonants >= 2) {
                    //on a double consonant.
                    query = buildSelectQueryWithInnerJoin(
                            Character.toString(wordCharacters[index - 1]),
                            GeneratorDatabaseHelper.START_TABLE_NAME,
                            GeneratorDatabaseHelper.LETTER_COL_VOWELS,
                            GeneratorDatabaseHelper.LETTER_TABLE_NAME
                    );

                } else {
                    //no double types.
                    query = buildSelectQuery(
                            Character.toString(wordCharacters[index - 1]),
                            GeneratorDatabaseHelper.START_TABLE_NAME
                    );
                }
            } else if (index == wordCharacters.length - 1){
                //If last letter in word
                if (consecVowels >= 2) {
                    //on a double vowel.
                    query = buildSelectQueryWithInnerJoin(
                            Character.toString(wordCharacters[index - 1]),
                            GeneratorDatabaseHelper.END_TABLE_NAME,
                            GeneratorDatabaseHelper.LETTER_COL_CONSONANTS,
                            GeneratorDatabaseHelper.LETTER_TABLE_NAME
                    );

                } else if (consecConsonants >= 2) {
                    //on a double consonant.
                    query = buildSelectQueryWithInnerJoin(
                            Character.toString(wordCharacters[index - 1]),
                            GeneratorDatabaseHelper.END_TABLE_NAME,
                            GeneratorDatabaseHelper.LETTER_COL_VOWELS,
                            GeneratorDatabaseHelper.LETTER_TABLE_NAME
                    );

                } else {
                    //no double types.
                    query = buildSelectQuery(
                            Character.toString(wordCharacters[index - 1]),
                            GeneratorDatabaseHelper.END_TABLE_NAME
                    );
                }

            } else {
                //If mid word
                if (consecVowels >= 2) {
                    //on a double vowel.
                    query = buildSelectQueryWithInnerJoin(
                            Character.toString(wordCharacters[index - 1]),
                            GeneratorDatabaseHelper.MID_TABLE_NAME,
                            GeneratorDatabaseHelper.LETTER_COL_CONSONANTS,
                            GeneratorDatabaseHelper.LETTER_TABLE_NAME
                    );

                } else if (consecConsonants >= 2) {
                    //on a double consonant.
                    query = buildSelectQueryWithInnerJoin(
                            Character.toString(wordCharacters[index - 1]),
                            GeneratorDatabaseHelper.MID_TABLE_NAME,
                            GeneratorDatabaseHelper.LETTER_COL_VOWELS,
                            GeneratorDatabaseHelper.LETTER_TABLE_NAME
                    );

                } else {
                    //no double types.
                    query = buildSelectQuery(
                            Character.toString(wordCharacters[index - 1]),
                            GeneratorDatabaseHelper.MID_TABLE_NAME
                    );
                }
            }

            Cursor c = gdbHelper.rawQuery(query,null);

            retrievedCharacters = new ArrayList<>();
            if(c.moveToFirst()){
                while(!c.isAfterLast()){
                    if(c.getString(0) != null) {
                        retrievedCharacters.add(c.getString(0).toCharArray()[0]);
                    }
                    c.moveToNext();
                }
            }

            wordCharacters[index] = retrievedCharacters.get(r.nextInt(retrievedCharacters.size()));

            if(isVowel(wordCharacters[index])){
                consecVowels++;
                consecConsonants = 0;
            } else {
                consecConsonants++;
                consecVowels = 0;
            }

            index++;
        }
    }

    private String buildSelectQuery(String columnName, String tableName){
        return "SELECT " +
                columnName +
                " FROM " +
                tableName + ";";


    }

    private String buildSelectQueryWithInnerJoin(String column1Name, String table1Name, String column2Name, String table2Name){
        return "SELECT " +
                column1Name +
                " FROM " +
                table1Name +
                " INNER JOIN letters on " +
                table2Name + "." +
                column2Name + " = " +
                table1Name + "." +
                column1Name + ";";


    }

    private boolean isVowel(char c){

        for (char v : VOWELS){
            if(c == v){
                return true;
            }
        }

        return false;
    }

    private char getRandomLetter(){
        int choice = r.nextInt(26);
        if(choice > 5){
            consecConsonants += 1;
            return CONSONANTS[r.nextInt(CONSONANTS.length)];
        } else {
            consecVowels += 1;
            return VOWELS[r.nextInt(VOWELS.length)];
        }
    }
}
