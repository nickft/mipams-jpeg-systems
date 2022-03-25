package com.mipams.jumbf.core.util;

import lombok.Getter;  
import lombok.Setter;  
public enum BoxTypeEnum{
    
    JumbfBox("jumb", 0x6A756D62),
    DescriptionBox("jumd", 0x6A756D64),
    JsonBox("json", 0x6A736F6E),
    ContiguousCodestreamBox("jp2c", 0x6A703263),
    EmbeddedFileBox("bidb", 0x6266462),
    EmbeddedFileDescriptionBox("bfdb", 0x62666462);

    private @Getter @Setter String type;
    private @Getter @Setter int typeId;

    BoxTypeEnum(String type, int typeId){
        setTypeId(typeId);
        setType(type);
    }

    public static BoxTypeEnum getBoxTypeFromId(int id){
        
        for (BoxTypeEnum boxType: values()){
            if(boxType.getTypeId() == id ){
                return boxType;
            }
        }
        return null;
    }

    public static BoxTypeEnum getBoxTypeFromString(String type){
        for (BoxTypeEnum boxType: values()){
            if(boxType.getType().equals(type) ){
                return boxType;
            }
        }
        return null;
    }
}