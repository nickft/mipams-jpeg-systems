package org.mipams.jumbf.core.util;

import java.util.UUID;

import lombok.Getter;  
import lombok.Setter;

public enum BoxTypeEnum {
    
    JumbfBox("jumb", 0x6A756D62, "6A756D62-0011-0010-8000-00AA00389B71"),
    DescriptionBox("jumd", 0x6A756D64, null),
    JsonBox("json", 0x6A736F6E, "6A736F6E-0011-0010-8000-00AA00389B71"),
    ContiguousCodestreamBox("jp2c", 0x6A703263, "6A703263-0011-0010-8000-00AA00389B71"),
    EmbeddedFileBox("bidb", 0x6266462, "40CB0C32-BB8A-489D-A70B-2AD6F47F4369"),
    EmbeddedFileDescriptionBox("bfdb", 0x62666462, null);

    private @Getter @Setter String type;
    private @Getter @Setter int typeId;
    private @Getter @Setter UUID contentUuid;

    BoxTypeEnum(String type, int typeId, String uuidAsString){
        setTypeId(typeId);
        setType(type);
        if(uuidAsString != null) setContentUuid(UUID.fromString(uuidAsString));
    }

    public static String getBoxTypeAsStringFromId(int boxId){
        BoxTypeEnum result = getBoxTypeFromIdOrNull(boxId);

        return (result != null) ? result.getType() : "";
    }


    public static BoxTypeEnum getBoxTypeFromIdOrNull(int boxId){
        BoxTypeEnum resultType = null;
        
        for (BoxTypeEnum boxType: values()){
            if(boxType.getTypeId() == boxId ){
                resultType = boxType;
                break;
            }
        }

        return resultType;
    }

    public static BoxTypeEnum getBoxTypeFromContentUuidOrNull(UUID uuid){
        BoxTypeEnum resultType = null;

        for (BoxTypeEnum boxType: values()){
            if(boxType.isContentBox()){
                if(boxType.getContentUuid().equals(uuid) ){
                    resultType = boxType;
                    break;
                }
            }
        }

        return resultType;
    }

    public boolean isContentBox(){
        return getContentUuid() != null;
    }

    public static BoxTypeEnum getBoxTypeFromString(String type){
        BoxTypeEnum resultType = null;
        
        for (BoxTypeEnum boxType: values()){
            if(boxType.getType().equals(type) ){
                resultType = boxType;
                break;
            }
        }

        return resultType;
    }
}