package org.mipams.jumbf.core.entities;

public class ServiceMetadata {

    private int boxTypeId;

    private String boxType;

    public ServiceMetadata(int boxTypeId, String boxType) {
        setBoxTypeId(boxTypeId);
        setBoxType(boxType);
    }

    public int getBoxTypeId() {
        return this.boxTypeId;
    }

    public void setBoxTypeId(int boxTypeId) {
        this.boxTypeId = boxTypeId;
    }

    public String getBoxType() {
        return this.boxType;
    }

    public void setBoxType(String boxType) {
        this.boxType = boxType;
    }

    @Override
    public String toString() {
        return "ServiceMetadata(boxTypeId=" + getBoxTypeId() + ", boxType=" + getBoxType() + ")";
    }

}
