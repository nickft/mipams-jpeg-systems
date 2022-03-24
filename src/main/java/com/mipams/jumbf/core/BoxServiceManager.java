package com.mipams.jumbf.core;

import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.ContentTypeEnum;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.services.XTBoxService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class BoxServiceManager {

    @Autowired
    List<XTBoxService> boxServiceList;

    public XTBoxService getSuperBoxService() throws MipamsException{
        return generateServiceBasedOnBoxType(BoxTypeEnum.JumbfBox);
    }

    public XTBoxService getServiceBasedOnContentUUID(UUID uuid) throws MipamsException{

        BoxTypeEnum boxType = ContentTypeEnum.getBoxTypeFromUUID(uuid);

        if(boxType == null){
            throw new MipamsException("Box " + boxType.toString() + " is not a Content Box");
        }

        return generateServiceBasedOnBoxType(boxType);   
    }

    public XTBoxService generateServiceBasedOnString(String type) throws MipamsException{

        BoxTypeEnum boxType = BoxTypeEnum.getBoxTypeFromString(type);

        if(boxType == null){
            throw new MipamsException("Uknown box in this Factory");
        }

        return generateServiceBasedOnBoxType(boxType);   
    }

    public XTBoxService generateServiceBasedOnBoxType(BoxTypeEnum boxType) throws MipamsException{

        for(XTBoxService service : boxServiceList){
            if(boxType.equals(service.serviceIsResponsibleForBoxType())){
                return service;
            }
        }

        throw new MipamsException("Box " + boxType.toString() + " not supported yet");
    }
}