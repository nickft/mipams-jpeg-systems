package org.mipams.jumbf.entities;

public class PrivateBox extends JumbfBox {

    @Override
    public int getTypeId() {
        return 0x70726976;
    }

    @Override
    public String getType() {
        return "priv";
    }

}
