package com.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import com.mipams.jumbf.core.entities.EmbeddedFileBox;
import com.mipams.jumbf.core.entities.EmbeddedFileDescriptionBox;
import com.mipams.jumbf.core.entities.XTBox;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BadRequestException;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmbeddedFileDescriptionBoxService extends XTBoxService<EmbeddedFileDescriptionBox>{

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedFileDescriptionBoxService.class); 

    @Override
    protected EmbeddedFileDescriptionBox initializeBox() throws MipamsException{
        return new EmbeddedFileDescriptionBox();
    }

    @Override
    protected void populateBox(EmbeddedFileDescriptionBox embeddedFileDescriptionBox, ObjectNode input) throws MipamsException{
              
        String type = input.get("type").asText();

        if( !embeddedFileDescriptionBox.getBoxType().equals(type)){
            throw new BadRequestException("Box type does not match with description type.");
        }

        int toggle = 0;
        
        try{
            embeddedFileDescriptionBox.setMediaTypeFromString(input.get("mediaType").asText());
        } catch (MipamsException e){
            throw new BadRequestException(e);
        } catch (NullPointerException e){
            throw new BadRequestException("Media type not specified", e);
        }

        JsonNode node = input.get("fileName");

        if(node != null){
            toggle = 1;
            embeddedFileDescriptionBox.setFileName(node.asText());
        }
        
        node = input.get("externalFile");

        if(node != null) {
            toggle = toggle | 2;
            if(node.asBoolean()){
                embeddedFileDescriptionBox.markAsExternalFile();
            } else {
                embeddedFileDescriptionBox.markAsInternalFile();
            }
            
        } else {
            embeddedFileDescriptionBox.markAsExternalFile();
        }
    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(EmbeddedFileDescriptionBox embeddedFileDescriptionBox, FileOutputStream fileOutputStream) throws MipamsException{
        
        try{        
            fileOutputStream.write(CoreUtils.convertIntToSingleElementByteArray(embeddedFileDescriptionBox.getToggle()));
            fileOutputStream.write(CoreUtils.convertStringToByteArray(embeddedFileDescriptionBox.getMediaTypeWithEscapeCharacter()));

            if(embeddedFileDescriptionBox.fileNameExists()){
                fileOutputStream.write(CoreUtils.convertStringToByteArray(embeddedFileDescriptionBox.getFileNameWithEscapeCharacter()));
            }
        } catch (IOException e){
            throw new MipamsException("Could not write to file.", e);
        }

    }

    @Override
    protected void populatePayloadFromJumbfFile(EmbeddedFileDescriptionBox embeddedFileDescriptionBox, InputStream input) throws MipamsException{
        logger.debug("Embedded File Description box");

        long actualSize = 0;

        try{

            int toggleValue = 0;
            if((toggleValue = input.read()) == -1){
                throw new MipamsException();
            }
            actualSize ++;
            embeddedFileDescriptionBox.setToggle(toggleValue);

            char charVal;
            StringBuilder str = new StringBuilder();

            while((charVal = (char) input.read()) != '\0') {
                str.append(charVal);
                actualSize ++;
            }
            //For the null character that we are not included in the variable
            actualSize ++;

            try{
                embeddedFileDescriptionBox.setMediaTypeFromString(str.toString());
            } catch (MipamsException e){
                throw new BadRequestException(e);
            }

            if(embeddedFileDescriptionBox.fileNameExists()){
                str = new StringBuilder();

                while((charVal = (char) input.read()) != '\0') {
                    str.append(charVal);
                    actualSize ++;
                }
                //For the null character that we are not included in the variable
                actualSize ++;

                embeddedFileDescriptionBox.setFileName(str.toString());
            }

        } catch (IOException e){
            throw new MipamsException("Failed to read description box after {"+Long.toString(actualSize)+"} bytes.", e);
        }

        verifyBoxSize(embeddedFileDescriptionBox, actualSize);

        logger.debug("Discovered box: " + embeddedFileDescriptionBox.toString());
    }

    @Override
    public BoxTypeEnum serviceIsResponsibleForBoxType(){
        return BoxTypeEnum.EmbeddedFileDescriptionBox;
    }
}