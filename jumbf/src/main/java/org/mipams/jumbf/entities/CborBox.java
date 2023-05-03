package org.mipams.jumbf.entities;

public class CborBox extends MemoryBox {

    public static int TYPE_ID = 0x63626F72;
    public static String TYPE = "cbor";

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
        return "CborBox(" + super.toString() + ")";
    }
}