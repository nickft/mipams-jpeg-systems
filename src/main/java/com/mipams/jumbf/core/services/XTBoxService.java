package com.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import com.mipams.jumbf.core.entities.XTBox;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.CorruptedJumbfFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XTBoxService implements BoxServiceInterface{

    private static final Logger logger = LoggerFactory.getLogger(XTBoxService.class); 

    @Override
    public final XTBox writeToJumbfFileFromRequest(ObjectNode inputNode, FileOutputStream fileOutputStream) throws MipamsException{

        XTBox xtBox = discoverXTBox(inputNode);

        logger.debug(xtBox.toString());

        writeBoxToJumbfFile(xtBox, fileOutputStream);
        return xtBox;
    }

    protected final XTBox discoverXTBox(ObjectNode inputNode) throws MipamsException{
        XTBox xtBox = initializeBox();
        populateBox(xtBox, inputNode);
        xtBox.setXTHeadersBasedOnBox();
        return xtBox;
    }

    protected abstract void populateBox(XTBox xtBox, ObjectNode input) throws MipamsException;
    
    
    protected final void writeBoxToJumbfFile(XTBox xtBox, FileOutputStream fileOutputStream) throws MipamsException{
        writeXTBoxHeadersToJumbfFile(xtBox, fileOutputStream);
        writeXTBoxPayloadToJumbfFile(xtBox, fileOutputStream);
    }

    private final void writeXTBoxHeadersToJumbfFile(XTBox xtBox, FileOutputStream fileOutputStream) throws MipamsException{
        try{

            fileOutputStream.write(CoreUtils.convertIntToByteArray(xtBox.getLBox()));
            fileOutputStream.write(CoreUtils.convertIntToByteArray(xtBox.getTBox()));

            if(xtBox.isXBoxEnabled()) {
                fileOutputStream.write(CoreUtils.convertLongToByteArray(xtBox.getXBox()));
            }
        }  catch (IOException e) {
            throw new MipamsException("Could not write to file", e);
        }
    }

    protected abstract void writeXTBoxPayloadToJumbfFile(XTBox xtBox, FileOutputStream fileOutputStream) throws MipamsException;

    @Override
    public final XTBox parseFromJumbfFile(InputStream input) throws MipamsException{
        logger.debug("Start parsing a new XTBox");

        XTBox xtBox = initializeBox(); 

        populateHeadersFromJumbfFile(xtBox, input);

        populatePayloadFromJumbfFile(xtBox, input);

        logger.debug("The box "+ xtBox.getBoxType() +" has a total length of "+xtBox.getBoxSizeFromXTBoxHeaders());

        return xtBox;
    }

    protected abstract XTBox initializeBox() throws MipamsException;

    private void populateHeadersFromJumbfFile(XTBox xtBox, InputStream input) throws MipamsException{
        byte[] temp = new byte[4];
        int value;

        try{

            if(input.available() == 0){
                return;
            }

            if(input.read(temp, 0, 4) == -1){
                throw new CorruptedJumbfFileException("Failed to parse LBox value from byte stream");
            }

            value = CoreUtils.convertByteArrayToInt(temp);
            xtBox.setLBox(value);

            if(input.read(temp, 0, 4) == -1){
                throw new CorruptedJumbfFileException("Failed to parse TBox value from byte stream");
            }

            value = CoreUtils.convertByteArrayToInt(temp);

            BoxTypeEnum boxType = BoxTypeEnum.getBoxTypeFromId(value);

            if(!xtBox.checkEqualityWithBoxType(boxType.getType())){
                throw new CorruptedJumbfFileException("TBox Id "+value+" does not match with box "+xtBox.getBoxType());
            }

            xtBox.setTBox(value);
            
            if(xtBox.getLBox() == 1){
                temp = new byte[8];
                long longVal;

                if(input.read(temp, 0, 8) == -1){
                    throw new CorruptedJumbfFileException("Failed to parse XBox value from byte stream");
                }

                longVal = CoreUtils.convertByteArrayToLong(temp);
                xtBox.setXBox(longVal);
            }
        } catch (IOException e){
            throw new MipamsException("Could not read XTBox values", e);
        }
    }

    protected void verifyBoxSize(XTBox xtBox, long actualSize) throws MipamsException{
        if (xtBox.getPayloadSizeFromXTBoxHeaders() != actualSize){
            throw new MipamsException("Mismatch in the byte counting(Nominal: "+xtBox.getPayloadSizeFromXTBoxHeaders()+", Actual: "+Long.toString(actualSize)+") of the Box: "+xtBox.toString());
        }
    }

    protected abstract void populatePayloadFromJumbfFile(XTBox xtBox, InputStream input) throws MipamsException;


    public abstract BoxTypeEnum serviceIsResponsibleForBoxType();

}