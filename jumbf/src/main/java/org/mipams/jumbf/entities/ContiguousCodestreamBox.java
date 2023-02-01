package org.mipams.jumbf.entities;

public class ContiguousCodestreamBox extends FileBox {

    @Override
    public int getTypeId() {
        return 0x6A703263;
    }

    @Override
    public String getType() {
        return "jp2c";
    }

    @Override
    public String toString() {
        return "ContiguousCodestreamBox(" + super.toString() + ")";
    }
}