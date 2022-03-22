package com.mipams.jumbf.core;

import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.ContentTypeEnum;
import com.mipams.jumbf.core.util.MipamsException;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class BoxFactory {

    public XTBox generateBoxBasedOnUUID(UUID uuid) throws MipamsException{

        BoxTypeEnum boxType = ContentTypeEnum.getBoxTypeFromUUID(uuid);

        if(boxType == null){
            throw new MipamsException("Uknown box in this Factory");
        }

        return generateBox(boxType);   
    }

    public XTBox generateBoxBasedOnType(String type) throws MipamsException{

        BoxTypeEnum boxType = ContentTypeEnum.getContentTypeFromString(type).getBoxType();

        if(boxType == null){
            throw new MipamsException("Uknown box in this Factory");
        }

        return generateBox(boxType);   
    }

    XTBox generateBox(BoxTypeEnum boxType) throws MipamsException{

        switch(boxType){
            case DescriptionBox:
                return new DescriptionBox();
            case JUMBFBox:
                return new JUMBFBox();
            case JSONBox:
                return new JSONBox();
            default:
                throw new MipamsException("Box not supported in this Factory");
        }
    }
}