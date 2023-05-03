package org.mipams.jumbf.entities;

public class XmlBox extends MemoryBox {

    public static int TYPE_ID = 0x786D6C20;
    public static String TYPE = "xml";

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
        return "XmlBox(" + super.toString() + ")";
    }
}