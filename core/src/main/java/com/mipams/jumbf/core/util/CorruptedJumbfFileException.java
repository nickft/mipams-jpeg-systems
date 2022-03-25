package mipams.jumbf.core.util;

public class CorruptedJumbfFileException extends MipamsException{
    
    private static final long serialVersionUID=10l;
    private static final String DISPLAY_TEXT = "Jumbf file is corrupted: ";

    public CorruptedJumbfFileException() {
    }

    public CorruptedJumbfFileException(String message) {
        super(message);
    }

    public CorruptedJumbfFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CorruptedJumbfFileException(Throwable cause) {
        super(cause);
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