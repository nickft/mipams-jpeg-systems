package mipams.jumbf.core.util;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.util.UUID;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreUtils{
    private static final Logger logger = LoggerFactory.getLogger(CoreUtils.class);

    public static final int INT_BYTE_SIZE = 4;

    public static final int LONG_BYTE_SIZE = 4;

    public static final int UUID_BYTE_SIZE = 16;

    public static int convertByteArrayToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static byte[] convertIntToByteArray(int num) {
        return ByteBuffer.allocate(4).putInt(num).array();
    }

    public static byte[] convertIntToSingleElementByteArray(int num) {
        return ByteBuffer.allocate(1).put((byte) num).array();
    }

    public static long convertByteArrayToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static byte[] convertLongToByteArray(long num) {
        return ByteBuffer.allocate(8).putLong(num).array();
    }

    public static byte[] convertStringToByteArray(String text) {
        return text.getBytes();
    }

    public static UUID convertByteArrayToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        UUID uuid = new UUID(high, low);
        return uuid;
    }

    public static byte[] convertUUIDToByteArray(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);

        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

    public static boolean isBitAtGivenPositionSet(int n, int positition){
        int new_num = n >> (positition - 1);
    
        return (new_num & 1) == 1;
    }    

    public static int setBitValueAtGivenPosition(int n, int position, int val){
        int mask = 1 << position;
        return (n & ~mask) | ((val << position) & mask);
    }

    public static double getFileSizeFromPath(String filePath) throws MipamsException{
        try{
            File f = new File(filePath);
            return f.length();
        } catch (SecurityException e){
            throw new MipamsException(e.getMessage());
        } catch (NullPointerException e){
            throw new MipamsException(e.getMessage());
        }
    } 

    public static String randomStringGenerator(){
        return UUID.randomUUID().toString();
    }

    public static String getFullPath(String folder, String fileName){
        StringBuilder fullPath = new StringBuilder(folder);

        if(!folder.endsWith("/")) fullPath.append("/");

        fullPath.append(fileName);

        return fullPath.toString();
    }

    public static String addEscapeCharacterToText(String text){
        return text + "\0";
    }

    public static void writeFileContentToOutput(String path, FileOutputStream fileOutputStream) throws MipamsException{

        try (FileInputStream inputStream = new FileInputStream(path)){
            int n;
            while ((n = inputStream.read()) != -1) {
                fileOutputStream.write(n);
            }  
        } catch(FileNotFoundException e){
            throw new MipamsException("Coulnd not locate file", e);
        } catch (IOException e){
            throw new MipamsException("Coulnd not write to file", e);
        }
    }

    public static MediaType getMediaTypeFromString(String input) throws IllegalArgumentException{
        MediaType mediaType ;
    
        mediaType = MediaType.valueOf(input);

        if(mediaType.getSubtype() == null){
            throw new IllegalArgumentException("Subtype needs to be specified.");
        }

        return mediaType;
    }
}