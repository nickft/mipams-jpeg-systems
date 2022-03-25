package mipams.jumbf.provenance;

import mipams.jumbf.core.util.MipamsException;
import mipams.jumbf.core.services.XTBoxService;

import mipams.jumbf.provenance.util.BoxTypeEnum;
import mipams.jumbf.provenance.util.ContentTypeEnum;

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

    public XTBoxService generateServiceBasedOnBoxType(BoxTypeInterface boxType) throws MipamsException{

        for(XTBoxService service : boxServiceList){
            if(boxType.equals(service.serviceIsResponsibleForBoxType())){
                return service;
            }
        }

        throw new MipamsException("Box " + boxType.toString() + " not supported yet");
    }
}