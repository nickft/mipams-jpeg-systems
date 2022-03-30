package org.mipams.jumbf.core;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.services.XTBoxService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("rawtypes")
public class BoxServiceManager {

    @Autowired
    protected List<XTBoxService> boxServiceList;

    public XTBoxService getSuperBoxService() throws MipamsException {
        return generateServiceBasedOnBoxWithId(BoxTypeEnum.JumbfBox.getTypeId());
    }

    public XTBoxService getServiceBasedOnContentUUID(UUID uuid) throws MipamsException {

        BoxTypeEnum boxType = BoxTypeEnum.getBoxTypeFromContentUuidOrNull(uuid);

        if (boxType == null) {
            throw new MipamsException("Box with uuid" + uuid.toString() + " is not a Content Box");
        }

        return generateServiceBasedOnBoxWithId(boxType.getTypeId());
    }

    public XTBoxService generateServiceBasedOnBoxWithId(int boxId) throws MipamsException {
        for (XTBoxService service : boxServiceList) {
            if (boxId == service.serviceIsResponsibleForBoxTypeId()) {
                return service;
            }
        }
        throw new MipamsException("Box type with id: " + Integer.toString(boxId) + "is not supported yet");
    }
}