package org.mipams.jumbf.core;

import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.core.services.content_types.ContentTypeService;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ContentTypeDiscoveryManager {

    private static final Logger logger = LoggerFactory.getLogger(ContentTypeDiscoveryManager.class);

    @Autowired
    protected List<ContentTypeService> contentTypeServiceList;

    public ContentTypeService getContentBoxServiceBasedOnContentUuid(String uuidAsString) throws MipamsException {

        UUID inputUuid = UUID.fromString(uuidAsString);

        for (ContentTypeService service : contentTypeServiceList) {

            logger.debug(service.getClass().getName());

            UUID serviceUuid = UUID.fromString(service.getContentTypeUuid());

            if (serviceUuid.equals(inputUuid)) {
                return service;
            }
        }

        throw new MipamsException("Box with uuid " + uuidAsString + " is not a Content Box");
    }
}