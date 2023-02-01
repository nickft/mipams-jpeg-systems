package org.mipams.jumbf.entities;

public class CborBox extends MemoryBox {

    @Override
    public int getTypeId() {
        return 0x63626F72;
    }

    @Override
    public String getType() {
        return "cbor";
    }

    @Override
    public String toString() {
        return "CborBox(" + super.toString() + ")";
    }
}