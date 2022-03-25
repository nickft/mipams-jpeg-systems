package com.mipams.jumbf.core.util;

import java.util.UUID;

import lombok.Getter;  
import lombok.Setter;  
public enum ContentTypeEnum{
    Json(BoxTypeEnum.JsonBox, "6A736F6E-0011-0010-8000-00AA00389B71"),
    Jumbf(BoxTypeEnum.JumbfBox, "6A756D62-0011-0010-8000-00AA00389B71"),
    ContiguousCodestream(BoxTypeEnum.ContiguousCodestreamBox, "6A703263-0011-0010-8000-00AA00389B71"),
    EmbeddedFile(BoxTypeEnum.EmbeddedFileBox , "40CB0C32-BB8A-489D-A70B-2AD6F47F4369");

    private @Getter @Setter BoxTypeEnum boxType;
    private @Getter @Setter UUID typeId;

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
}