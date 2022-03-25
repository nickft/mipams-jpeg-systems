package mipams.jumbf.core;

import mipams.jumbf.core.util.BoxTypeEnum;
import mipams.jumbf.core.util.MipamsException;
import mipams.jumbf.core.services.XTBoxService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BoxServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(BoxServiceManager.class); 

    @Autowired
    List<XTBoxService> boxServiceList;

    public XTBoxService getSuperBoxService() throws MipamsException{
        return generateServiceBasedOnBoxWithId(BoxTypeEnum.JumbfBox.getTypeId());
    }

    public XTBoxService getServiceBasedOnContentUUID(UUID uuid) throws MipamsException{

        BoxTypeEnum boxType = BoxTypeEnum.getBoxTypeFromContentUuidOrNull(uuid);

        if(boxType == null){
            throw new MipamsException("Box " + boxType.toString() + " is not a Content Box");
        }

        return generateServiceBasedOnBoxWithId(boxType.getTypeId());   
    }

    public XTBoxService generateServiceBasedOnBoxWithId(int boxId) throws MipamsException{
        for(XTBoxService service : boxServiceList){
            if(boxId == service.serviceIsResponsibleForBoxTypeId()){
                return service;
            }
        }
        throw new MipamsException("Box type with id: " + Integer.toString(boxId) + "is not supported yet");
    }
}