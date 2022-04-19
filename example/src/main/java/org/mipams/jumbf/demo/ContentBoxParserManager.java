package org.mipams.jumbf.demo;

import java.util.List;

import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.demo.services.core.ContentBoxParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentBoxParserManager {

    private static final Logger logger = LoggerFactory.getLogger(ContentBoxParserManager.class);

    @Autowired
    ContentBoxDiscoveryManager contentBoxDiscoveryManager;

    @Autowired
    List<ContentBoxParser> contentBoxParserList;

    public ContentBoxParser getParserBasedOnContentUUID(String contentTypeUuid) throws MipamsException {
        for (ContentBoxParser service : contentBoxParserList) {

            logger.debug(service.getServiceMetadata().getContentTypeUuid());

            if (service.getServiceMetadata().getContentTypeUuid().equalsIgnoreCase(contentTypeUuid)) {
                return service;
            }
        }

        throw new MipamsException("Box with uuid " + contentTypeUuid + " is not a Content Box");

    }
}
