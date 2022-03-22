package com.mipams.jumbf.core;

import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.ContentTypeEnum;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class BoxFactory {

    public XTBox generateBoxBasedOnUUID(UUID uuid) throws Exception{

        BoxTypeEnum boxType = ContentTypeEnum.getBoxTypeFromUUID(uuid);

        if(boxType == null){
            throw new Exception("Uknown box in this Factory");
        }

        return generateBox(boxType);   
    }

    public XTBox generateBoxBasedOnType(String type) throws Exception{

        BoxTypeEnum boxType = ContentTypeEnum.getContentTypeFromString(type).getBoxType();

        if(boxType == null){
            throw new Exception("Uknown box in this Factory");
        }

        return generateBox(boxType);   
    }

    XTBox generateBox(BoxTypeEnum boxType) throws Exception{

        switch(boxType){
            case DescriptionBox:
                return new DescriptionBox();
            case JUMBFBox:
                return new JUMBFBox();
            case JSONBox:
                return new JSONBox();
            default:
                throw new Exception("Box not supported in this Factory");
        }
    }
}