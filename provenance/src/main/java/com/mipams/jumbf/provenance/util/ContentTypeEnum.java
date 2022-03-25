package mipams.jumbf.provenance.util;

import java.util.UUID;

import lombok.Getter;  
import lombok.Setter;
  
public enum ContentTypeEnum{
    Jumbf(BoxTypeEnum.JumbfBox, "6A756D62-0011-0010-8000-00AA00389B71");

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