package org.mipams.jumbf;

import org.mipams.jumbf.util.MipamsException;

import org.mipams.jumbf.services.content_types.ContentTypeService;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentTypeDiscoveryManager {

    private static Logger logger = Logger.getLogger(ContentTypeDiscoveryManager.class.getName());

    @Autowired
    protected List<ContentTypeService> contentTypeServiceList;

    public ContentTypeService getContentBoxServiceBasedOnContentUuid(String uuidAsString) throws MipamsException {

        UUID inputUuid = UUID.fromString(uuidAsString);

        for (ContentTypeService service : contentTypeServiceList) {

            logger.log(Level.FINE,service.getClass().getName());

            UUID serviceUuid = UUID.fromString(service.getContentTypeUuid());

            if (serviceUuid.equals(inputUuid)) {
                return service;
            }
        }

        throw new MipamsException("Box with uuid " + uuidAsString + " is not a Content Box");
    }
}