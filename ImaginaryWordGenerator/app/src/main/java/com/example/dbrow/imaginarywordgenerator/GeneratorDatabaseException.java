package com.example.dbrow.imaginarywordgenerator;

public class GeneratorDatabaseException extends Exception {
    public GeneratorDatabaseException(String message, Exception e){
        super(message, e);
    }
}
