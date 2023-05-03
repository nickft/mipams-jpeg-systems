package org.mipams.jumbf.entities;

public class ContiguousCodestreamBox extends FileBox {

    public static int TYPE_ID = 0x6A703263;
    public static String TYPE = "jp2c";

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
        return "ContiguousCodestreamBox(" + super.toString() + ")";
    }
}