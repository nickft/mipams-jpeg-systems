package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import org.mipams.jumbf.core.entities.XTBox;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.CorruptedJumbfFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XTBoxService<T extends XTBox> implements BoxServiceInterface{

    private static final Logger logger = LoggerFactory.getLogger(XTBoxService.class); 

    @Override
    public final T writeToJumbfFileFromRequest(ObjectNode inputNode, FileOutputStream fileOutputStream) throws MipamsException{

        T xtBox = discoverXTBox(inputNode);

        logger.debug(xtBox.toString());

        writeBoxToJumbfFile(xtBox, fileOutputStream);
        return xtBox;
    }

    protected final T discoverXTBox(ObjectNode inputNode) throws MipamsException{
        T xtBox = initializeBox();
        populateBox(xtBox, inputNode);
        xtBox.setXTHeadersBasedOnBox();
        return xtBox;
    }

    protected abstract void populateBox(T xtBox, ObjectNode input) throws MipamsException;
    
    
    protected final void writeBoxToJumbfFile(T xtBox, FileOutputStream fileOutputStream) throws MipamsException{
        writeXTBoxHeadersToJumbfFile(xtBox, fileOutputStream);
        writeXTBoxPayloadToJumbfFile(xtBox, fileOutputStream);
    }

    private final void writeXTBoxHeadersToJumbfFile(T xtBox, FileOutputStream fileOutputStream) throws MipamsException{
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

    protected abstract void writeXTBoxPayloadToJumbfFile(T xtBox, FileOutputStream fileOutputStream) throws MipamsException;

    @Override
    public final T parseFromJumbfFile(InputStream input) throws MipamsException{
        logger.debug("Start parsing a new XTBox");

        T xtBox = initializeBox(); 

        populateHeadersFromJumbfFile(xtBox, input);

        populatePayloadFromJumbfFile(xtBox, input);

        logger.debug("The box "+ BoxTypeEnum.getBoxTypeAsStringFromId(xtBox.getTypeId()) +" has a total length of "+xtBox.getBoxSizeFromXTBoxHeaders());

        return xtBox;
    }

    protected abstract T initializeBox() throws MipamsException;

    private void populateHeadersFromJumbfFile(T xtBox, InputStream input) throws MipamsException{
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

            BoxTypeEnum boxType = BoxTypeEnum.getBoxTypeFromIdOrNull(value);

            if(boxType != null && xtBox.getTypeId() != boxType.getTypeId()){
                throw new CorruptedJumbfFileException("TBox Id "+value+" does not match with box "+BoxTypeEnum.getBoxTypeAsStringFromId(xtBox.getTypeId()));
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

    protected void verifyBoxSize(T xtBox, long actualSize) throws MipamsException{
        if (xtBox.getPayloadSizeFromXTBoxHeaders() != actualSize){
            throw new MipamsException("Mismatch in the byte counting(Nominal: "+xtBox.getPayloadSizeFromXTBoxHeaders()+", Actual: "+Long.toString(actualSize)+") of the Box: "+xtBox.toString());
        }
    }

    protected abstract void populatePayloadFromJumbfFile(T xtBox, InputStream input) throws MipamsException;


    public abstract int serviceIsResponsibleForBoxTypeId();

}