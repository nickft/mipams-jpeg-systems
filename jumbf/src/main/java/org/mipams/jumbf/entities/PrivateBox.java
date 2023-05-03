package org.mipams.jumbf.entities;

public class PrivateBox extends JumbfBox {

    public static int TYPE_ID = 0x70726976;
    public static String TYPE = "priv";

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
