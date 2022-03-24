package com.mipams.jumbf.core.util;

@SuppressWarnings("serial")  
public class BadRequestException extends MipamsException{

    private static final String DISPLAY_TEXT = "Bad request: ";
    
    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString(){
        if(getMessage() == null){
            return (DISPLAY_TEXT + getMessage());
        } else {
            return (DISPLAY_TEXT);
        }
    }
}