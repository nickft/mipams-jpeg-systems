package com.mipams.jumbf.core.util;

import java.util.UUID;


public enum ContentTypeEnum{
    JSON(BoxTypeEnum.JSONBox, "6A736F6E-0011-0010-8000-00AA00389B71"),
    JUMBF(BoxTypeEnum.JUMBFBox, "6A756D62-0011-0010-8000-00AA00389B71"),
    ContiguousCodestream(BoxTypeEnum.ContiguousCodestreamBox, "6A703263-0011-0010-8000-00AA00389B71");

    private BoxTypeEnum boxType;
    private UUID typeId;

    ContentTypeEnum(BoxTypeEnum type, String uuidHexDigitString){
        setBoxType(type);
        setTypeId(UUID.fromString(uuidHexDigitString));
    }

    public static BoxTypeEnum getBoxTypeFromUUID(UUID uuid){
        
        for (ContentTypeEnum contentType: values()){
            if(contentType.getTypeId().equals(uuid) ){
                return contentType.getBoxType();
            }
        }
        return null;
    }

    public static ContentTypeEnum getContentTypeFromString(String type){
        for (ContentTypeEnum contentType: values()){
            if(contentType.getBoxType().getType().equals(type) ){
                return contentType;
            }
        }
        return null;
    }

    public static BoxTypeEnum getBoxTypeFromString(String type){
        
        ContentTypeEnum result = getContentTypeFromString(type);

        return (result != null) ? result.getBoxType() : null;
    }

    public BoxTypeEnum getBoxType() {
        return boxType;
    }

    void setBoxType(BoxTypeEnum boxType) {
        this.boxType = boxType;
    }

    public UUID getTypeId() {
        return typeId;
    }

    void setTypeId(UUID typeId) {
        this.typeId = typeId;
    }
}