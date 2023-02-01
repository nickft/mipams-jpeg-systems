package org.mipams.jumbf.entities;

public class JsonBox extends MemoryBox {

    @Override
    public int getTypeId() {
        return 0x6A736F6E;
    }

    @Override
    public String getType() {
        return "json";
    }

    @Override
    public String toString() {
        return "JsonBox(" + super.toString() + ")";
    }
}