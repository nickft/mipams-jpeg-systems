package org.mipams.jumbf.demo;

import java.util.List;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.demo.services.ContentTypeParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentTypeParserManager {

    private static final Logger logger = LoggerFactory.getLogger(ContentTypeParserManager.class);

    @Autowired
    List<ContentTypeParser> contentBoxParserList;

    public ContentTypeParser getParserBasedOnContentUUID(String contentTypeUuid) throws MipamsException {
        for (ContentTypeParser service : contentBoxParserList) {

            logger.debug(service.getContentTypeUuid());

            if (service.getContentTypeUuid().equalsIgnoreCase(contentTypeUuid)) {
                return service;
            }
        }

        throw new MipamsException("Box with uuid " + contentTypeUuid + " is not a Content Box");
    }
}
