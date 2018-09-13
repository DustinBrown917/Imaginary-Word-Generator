package com.example.dbrow.imaginarywordgenerator;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

public class SavedWordsList {
    private static final String LOG_KEY = "SWL";
    private static final String FILE_NAME = "savedWords.txt";

    private ArrayList<String> savedWords;
    private File file;
    private Context context;

    public SavedWordsList(Context context){
        this.context = context;
        loadWords();
    }

    public int getSize(){
        return savedWords.size();
    }

    public boolean removeWordAt(int index){
        if(index < 0 || index >= savedWords.size()){
            Log.e(LOG_KEY, "Index " + index + " is invalid in list of size " + getSize() + ".");
            return false;
        }
        savedWords.remove(index);
        saveWords();
        return true;
    }

    public void addWord(String word){
        savedWords.add(word);
        saveWords();
    }

    public ArrayList<String> getSavedWords() {
        return new ArrayList<>(savedWords);
    }

    private void saveWords(){

        try{
            FileOutputStream fos = new FileOutputStream(context.getFilesDir() + FILE_NAME);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            for(String s: savedWords){
                osw.write(s + '\n');
            }
            osw.close();
            fos.close();
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private void loadWords(){
        savedWords = new ArrayList<>();
        try{
            File file = new File(context.getFilesDir() + FILE_NAME);
            if(!file.exists()){
                file.createNewFile();
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String st;
            while ((st = reader.readLine()) != null){
                savedWords.add(st);
            }
            reader.close();
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void test(){
        savedWords.add("one");
        savedWords.add("two");
        savedWords.add("three");

        saveWords();
        loadWords();
    }
}
