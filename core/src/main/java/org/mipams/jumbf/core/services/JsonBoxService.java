package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.stereotype.Service;

import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.BoxTypeEnum;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JsonBoxService extends XTBoxService<JsonBox> {

    private static final Logger logger = LoggerFactory.getLogger(JsonBoxService.class);

    @Override
    protected JsonBox initializeBox() throws MipamsException {
        return new JsonBox();
    }

    @Override
    protected void populateBox(JsonBox jsonBox, ObjectNode input) throws MipamsException {
        ObjectNode payloadNode = (ObjectNode) input.get("payload");
        jsonBox.setJsonContent(payloadNode);
    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(JsonBox jsonBox, FileOutputStream fileOutputStream)
            throws MipamsException {
        ObjectMapper om = new ObjectMapper();
        final ObjectWriter writer = om.writer();

        try {
            byte[] bytes = writer.writeValueAsBytes(jsonBox.getJsonContent());
            fileOutputStream.write(bytes);
        } catch (JsonProcessingException e) {
            logger.error("Coulnd not convert json to byte array", e);
        } catch (IOException e) {
            logger.error("Coulnd not write to file", e);
        }
    }

    @Override
    protected void populatePayloadFromJumbfFile(JsonBox jsonBox, InputStream input) throws MipamsException {
        logger.debug("Json box");

        try {
            if (jsonBox.isXBoxEnabled()) {
                throw new MipamsException("Json content is huge. Do not support it.");
            }

            int jsonSize = (int) jsonBox.getPayloadSizeFromXTBoxHeaders();

            ObjectMapper om = new ObjectMapper();
            final ObjectReader reader = om.reader();

            byte[] temp = new byte[jsonSize];

            input.read(temp, 0, jsonSize);

            ObjectNode jsonContent = (ObjectNode) reader.readTree(new ByteArrayInputStream(temp));
            jsonBox.setJsonContent(jsonContent);

            logger.debug("Discovered box: " + this.toString());
        } catch (IOException e) {
            throw new MipamsException("Coulnd not read Json content", e);
        }
    }

    @Override
    public int serviceIsResponsibleForBoxTypeId() {
        return BoxTypeEnum.JsonBox.getTypeId();
    }

    @Override
    public String serviceIsResponsibleForBoxType() {
        return BoxTypeEnum.JsonBox.getType();
    }
}