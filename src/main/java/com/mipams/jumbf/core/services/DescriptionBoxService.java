package com.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Service;

import com.mipams.jumbf.core.entities.DescriptionBox;
import com.mipams.jumbf.core.entities.XTBox;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.ContentTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BadRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DescriptionBoxService extends XTBoxService<DescriptionBox>{

    private static final Logger logger = LoggerFactory.getLogger(DescriptionBoxService.class); 

    @Override
    protected DescriptionBox initializeBox() throws MipamsException{
        return new DescriptionBox();
    }

    @Override
    protected void populateBox(DescriptionBox descriptionBox, ObjectNode input) throws MipamsException{
        
        String type = input.get("type").asText();
        
        ContentTypeEnum contentType = ContentTypeEnum.getContentTypeFromString(type);

        if(contentType == null){
            throw new BadRequestException("Content Type: "+type+" is not supported");
        }

        descriptionBox.setUuid(contentType.getTypeId());

        JsonNode node = input.get("requestable");
        int toggle = (node == null) ? 0 : 1;

        node = input.get("label");

        if(node != null){
            descriptionBox.setLabel(node.asText());
            toggle = toggle | 2;
        }

        node = input.get("id");

        if(node != null){
            descriptionBox.setId(node.asInt());
            toggle = toggle | 4;
        }

        descriptionBox.setToggle(toggle);

        logger.debug("Signature is not supported");
    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(DescriptionBox descriptionBox, FileOutputStream fileOutputStream) throws MipamsException{
        
        try{        
            fileOutputStream.write(CoreUtils.convertUUIDToByteArray(descriptionBox.getUuid()));
            fileOutputStream.write(CoreUtils.convertIntToSingleElementByteArray(descriptionBox.getToggle()));

            if(descriptionBox.labelExists()){
                fileOutputStream.write(CoreUtils.convertStringToByteArray(descriptionBox.getLabelWithEscapeCharacter()));
            }

            if(descriptionBox.idExists()){
                fileOutputStream.write(CoreUtils.convertIntToByteArray(descriptionBox.getId()));
            }

            if(descriptionBox.signatureExists()){
                fileOutputStream.write(descriptionBox.getSignature());
            }
        } catch (IOException e){
            throw new MipamsException("Could not write to file.", e);
        }
    }

    @Override
    protected void populatePayloadFromJumbfFile(DescriptionBox descriptionBox, InputStream input) throws MipamsException{
        
        logger.debug("Description box");

        long actualSize = 0;

        try{
            byte[] uuidTemp = new byte[16];

            if(input.read(uuidTemp, 0, 16) == -1){
                throw new MipamsException();
            }

            UUID uuidVal = CoreUtils.convertByteArrayToUUID(uuidTemp);
            descriptionBox.setUuid(uuidVal);
            actualSize +=16;

            int toggleValue = 0;
            if((toggleValue = input.read()) == -1){
                throw new MipamsException();
            }
            actualSize ++;
            descriptionBox.setToggle(toggleValue);

            if(descriptionBox.labelExists()){
                char charVal;
                StringBuilder str = new StringBuilder();

                while((charVal = (char) input.read()) != '\0') {
                    str.append(charVal);
                    actualSize ++;
                }
                //For the null character that we are not included in the variable
                actualSize ++;

                descriptionBox.setLabel(str.toString());
            }

            if(descriptionBox.idExists()){

                byte[] temp = new byte[4];

                if(input.read(temp, 0, 4) == -1){
                    throw new MipamsException();
                }

                int idVal = CoreUtils.convertByteArrayToInt(temp);
                descriptionBox.setId(idVal);
                actualSize +=4;
            }

            if(descriptionBox.signatureExists()){
                byte[] signatureVal = new byte[32];

                if(input.read(signatureVal, 0, 32) == -1){
                    throw new MipamsException();
                }

                descriptionBox.setSignature(signatureVal);
                actualSize +=32;
            }
        } catch (IOException e){
            throw new MipamsException("Failed to read description box after {"+Long.toString(actualSize)+"} bytes.", e);
        }

        verifyBoxSize(descriptionBox, actualSize);

        logger.debug("Discovered box: " + descriptionBox.toString());
    }

    @Override
    public BoxTypeEnum serviceIsResponsibleForBoxType(){
        return BoxTypeEnum.DescriptionBox;
    }
}