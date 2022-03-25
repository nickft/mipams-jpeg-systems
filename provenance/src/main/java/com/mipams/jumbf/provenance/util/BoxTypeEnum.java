package mipams.jumbf.provenance.util;

import lombok.Getter;  
import lombok.Setter;  

public enum BoxTypeEnum {
    
    JumbfBox("jumb", 0x6A756D62);

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