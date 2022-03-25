package mipams.jumbf.provenance.util;

import java.util.UUID;

import lombok.Getter;  
import lombok.Setter;  

public enum BoxTypeEnum {
    
    AssertionBox("mpas", 0x6D706173, "6D706173-0011-0010-8000-00AA00389B71");

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