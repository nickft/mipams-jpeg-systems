package com.mipams.jumbf.core.util;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreUtils{
    private static final Logger logger = LoggerFactory.getLogger(CoreUtils.class);

    public static long MAX_FILE_SIZE = 52428800;

    public static String IMAGE_FOLDER = "/home/nikos/Desktop";

    public long getMaxFileSize(){
        return MAX_FILE_SIZE;
    }

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

    public static boolean doesFileSizeExceedApplicationLimits(String filePath) throws MipamsException{
        double size = getFileSizeFromPath(filePath);
        return size > MAX_FILE_SIZE || size > Long.MAX_VALUE;        
    }

    public static boolean doesFileSizeExceedApplicationLimits(double fileSize) throws MipamsException{
        return fileSize > MAX_FILE_SIZE || fileSize > Long.MAX_VALUE;        
    }

    public static String randomStringGenerator(){
        return UUID.randomUUID().toString();
    }

    public static String getFullPath(String fileName){
        StringBuilder fullPath = new StringBuilder(IMAGE_FOLDER);

        if(!IMAGE_FOLDER.endsWith("/")) fullPath.append("/");

        fullPath.append(fileName);

        return fullPath.toString();
    }
}