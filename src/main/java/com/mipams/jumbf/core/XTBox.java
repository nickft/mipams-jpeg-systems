package com.mipams.jumbf.core;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.CorruptedJumbfFileException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;  
import lombok.Setter;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class XTBox implements BoxInterface{

    private static final Logger logger = LoggerFactory.getLogger(XTBox.class);

    private @Getter @Setter int LBox;

    private @Getter @Setter int TBox;

    private @Getter @Setter Long XBox;

    final public void populate(ObjectNode input) throws MipamsException{
        populateBody(input);

        setTBox(getBoxTypeId());

        long size = 4 + 4 + calculatePayloadSizeInBytes();
        
        if(size > Integer.MAX_VALUE){
            setLBox(1);
            size += 4;
            setXBox(size);
        } else {
            setLBox((int) size);
        }
    }

    public abstract void populateBody(ObjectNode input) throws MipamsException;

    public abstract long calculatePayloadSizeInBytes() throws MipamsException;

    @Override
    public void toBytes(FileOutputStream fileOutputStream) throws MipamsException {  
        
        try{
            fileOutputStream.write(CoreUtils.convertIntToByteArray(LBox));
            fileOutputStream.write(CoreUtils.convertIntToByteArray(TBox));

            if(isXBoxEnabled()){
                fileOutputStream.write(CoreUtils.convertLongToByteArray(XBox));
            }
        }  catch (IOException e){
            logger.error("Could not write to file.", e);
        }
    }

    @Override
    final public void parse(InputStream input) throws MipamsException{      
        logger.debug("Start parsing a new XTBox");

        parseHeaders(input);

        parsePayload(input);

        logger.debug("The box "+getBoxType()+" has a total length of "+getNominalBoxSizeInBytes());
        
        return;
    }

    private void parseHeaders(InputStream input) throws MipamsException{
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
            setLBox(value);

            if(input.read(temp, 0, 4) == -1){
                throw new CorruptedJumbfFileException("Failed to parse TBox value from byte stream");
            }

            value = CoreUtils.convertByteArrayToInt(temp);

            if(!getBoxType().equals(BoxTypeEnum.getBoxTypeFromId(value).getType())){
                throw new CorruptedJumbfFileException("Box type does not match with description type.");
            }
            setTBox(value);
            

            if(LBox == 1){
                temp = new byte[8];
                long longVal;

                if(input.read(temp, 0, 8) == -1){
                    throw new CorruptedJumbfFileException("Failed to parse XBox value from byte stream");
                }

                longVal = CoreUtils.convertByteArrayToLong(temp);
                setXBox(longVal);
            }
        } catch (IOException e){
            logger.error("Could not read XTBox values", e);
        }
    }

    public abstract void parsePayload(InputStream input) throws MipamsException;

    public long getNominalPayloadSizeInBytes(){
        long payloadSize = isXBoxEnabled() ? XBox - (4 + 4 + 8) : LBox - (4 + 4);
        return payloadSize;
    }

    protected long getNominalBoxSizeInBytes(){
        return isXBoxEnabled() ? XBox : LBox;
    }

    public boolean isXBoxEnabled(){
        return LBox == 1 && (XBox != null);
    }

    public abstract String getBoxType();

    public abstract int getBoxTypeId();
}
