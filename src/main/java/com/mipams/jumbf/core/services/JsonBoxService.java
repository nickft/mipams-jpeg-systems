package com.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.stereotype.Service;

import com.mipams.jumbf.core.entities.JsonBox;
import com.mipams.jumbf.core.entities.XTBox;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BadRequestException;
import com.mipams.jumbf.core.util.BoxTypeEnum;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JsonBoxService extends XTBoxService{

    private static final Logger logger = LoggerFactory.getLogger(JsonBoxService.class); 

    @Override
    protected JsonBox initializeBox() throws MipamsException{
        return new JsonBox();
    }

    @Override
    protected void populateBox(XTBox xtBox, ObjectNode input) throws MipamsException{
        JsonBox jsonBox = (JsonBox) xtBox;
        String type = input.get("type").asText();

        if( !jsonBox.getBoxType().equals(type)){
            throw new BadRequestException("Box type does not match with description type.");
        }

        ObjectNode payloadNode = (ObjectNode) input.get("payload");
        jsonBox.setJsonContent(payloadNode);
    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(XTBox xtBox, FileOutputStream fileOutputStream) throws MipamsException{
        JsonBox jsonBox = (JsonBox) xtBox;
        ObjectMapper om = new ObjectMapper();
        final ObjectWriter writer = om.writer();

        try{
            byte[] bytes = writer.writeValueAsBytes(jsonBox.getJsonContent());
            fileOutputStream.write(bytes);
        } catch (JsonProcessingException e){
            logger.error("Coulnd not convert json to byte array", e);
        } catch (IOException e){
            logger.error("Coulnd not write to file", e);
        }   
    }

    @Override
    protected void populatePayloadFromJumbfFile(XTBox xtBox, InputStream input) throws MipamsException{
        JsonBox jsonBox = (JsonBox) xtBox;
        logger.debug("Json box");

        long actualSize = 0;

        try{
            if(jsonBox.isXBoxEnabled()){
                throw new MipamsException("Json content is huge. Do not support it.");
            }
           
            int jsonSize = (int) jsonBox.getPayloadSizeFromXTBoxHeaders();

            ObjectMapper om = new ObjectMapper();
            final ObjectReader reader = om.reader();

            byte[] temp = new byte[jsonSize];

            input.read(temp, 0, jsonSize);

            ObjectNode jsonContent = (ObjectNode) reader.readTree(new ByteArrayInputStream(temp));
            jsonBox.setJsonContent(jsonContent);

            logger.debug("Discovered box: "+this.toString());
        } catch (IOException e){
            throw new MipamsException("Coulnd not read Json content", e);
        } 
    }

    @Override
    public BoxTypeEnum serviceIsResponsibleForBoxType(){
        return BoxTypeEnum.JsonBox;
    }
}