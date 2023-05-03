package org.mipams.jumbf.entities;

public class JsonBox extends MemoryBox {

    public static int TYPE_ID = 0x6A736F6E;
    public static String TYPE = "json";

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "JsonBox(" + super.toString() + ")";
    }
}