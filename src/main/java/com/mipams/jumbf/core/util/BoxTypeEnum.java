package com.mipams.jumbf.core.util;

public enum BoxTypeEnum{
    
    JUMBFBox("jumb", 0x6A756D62),
    DescriptionBox("jumd", 0x6A756D64),
    JSONBox("json", 0x6A736F6E);

    private String type;
    private int typeId;

    BoxTypeEnum(String type, int typeId){
        setTypeId(typeId);
        setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}