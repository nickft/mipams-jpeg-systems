package com.mipams.jumbf.core.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class CoreUtils{

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
}