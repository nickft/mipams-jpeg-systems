package org.mipams.jumbf.core;

import org.mipams.jumbf.core.util.MipamsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.ContentBoxService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("rawtypes")
public class ContentBoxDiscoveryManager {

    private static final Logger logger = LoggerFactory.getLogger(ContentBoxDiscoveryManager.class);

    @Autowired
    protected List<ContentBoxService> contentBoxServiceList;

    public ContentBoxService getContentBoxServiceBasedOnContentUUID(UUID uuid) throws MipamsException {

        for (ContentBoxService service : contentBoxServiceList) {

            if (service.getServiceMetadata().getContentTypeUuid().equals(uuid)) {
                return service;
            }

        }

        throw new MipamsException("Box with uuid " + uuid.toString() + " is not a Content Box");
    }

    public ServiceMetadata getMetadataForContentBoxServiceWithType(String type) throws MipamsException {

        for (ContentBoxService service : contentBoxServiceList) {

            if (service.getServiceMetadata().getBoxType().equals(type)) {
                return service.getServiceMetadata();
            }

        }

        throw new MipamsException("Box " + type + " is not supported yet");
    }

    public ContentBoxService generateContentBoxServiceBasedOnBoxWithId(int boxId) throws MipamsException {

        for (ContentBoxService service : contentBoxServiceList) {

            logger.debug(service.getClass().getName());

            if (boxId == service.getServiceMetadata().getBoxTypeId()) {
                return service;
            }

        }

        throw new MipamsException("Box type with id: 0x" + Integer.toHexString(boxId) + " is not supported yet");
    }

}