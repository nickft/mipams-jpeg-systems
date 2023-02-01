package org.mipams.jumbf.entities;

public class XmlBox extends MemoryBox {

    @Override
    public int getTypeId() {
        return 0x786D6C20;
    }

    @Override
    public String getType() {
        return "xml";
    }

    @Override
    public String toString() {
        return "XmlBox(" + super.toString() + ")";
    }
}