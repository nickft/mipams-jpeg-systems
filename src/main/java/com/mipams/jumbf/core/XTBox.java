package com.mipams.jumbf.core;

import java.nio.ByteBuffer;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

import com.mipams.jumbf.core.util.CoreUtils;

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

    final public void populate(ObjectNode input) throws Exception{
        populateBody(input);

        long size = calculatePayloadSizeInBytes();
        
        if(size > Integer.MAX_VALUE){
            setLBox(1);
            setXBox(size);
        } else {
            setLBox((int) size);
        }

        setTBox(getBoxTypeId());
    }

    public abstract void populateBody(ObjectNode input) throws Exception;

    public abstract long calculatePayloadSizeInBytes() throws Exception;

    @Override
    public void toBytes(FileOutputStream fileOutputStream) throws Exception {      
        fileOutputStream.write(CoreUtils.convertIntToByteArray(LBox));
        fileOutputStream.write(CoreUtils.convertIntToByteArray(TBox));

        if(isXBoxEnabled()){
            fileOutputStream.write(CoreUtils.convertLongToByteArray(XBox));
        }
    }

    @Override
    final public void parse(ByteArrayInputStream input) throws Exception{      
        logger.debug("Start parsing a new XTBox");

        parseHeaders(input);

        parsePayload(input);

        logger.debug("The box has a total length of "+getNominalPayloadSizeInBytes());
        
        return;
    }

    private void parseHeaders(ByteArrayInputStream input) throws Exception{
        byte[] temp = new byte[4];
        int value;

        if(input.available() == 0){
            logger.debug("No more XTBoxes to parse");
            return;
        }

        if(input.read(temp, 0, 4) == -1){
            throw new Exception("JUMBF Box is corrupted");
        }

        value = CoreUtils.convertByteArrayToInt(temp);
        setLBox(value);

        if(input.read(temp, 0, 4) == -1){
            throw new Exception("JUMBF Box is corrupted");
        }

        value = CoreUtils.convertByteArrayToInt(temp);
        setTBox(value);

        if(LBox == 1){
            temp = new byte[8];
            long longVal;

            if(input.read(temp, 0, 8) == -1){
                throw new Exception("JUMBF Box is corrupted");
            }

            longVal = CoreUtils.convertByteArrayToLong(temp);
            setXBox(longVal);
        }
    }

    public abstract void parsePayload(ByteArrayInputStream input) throws Exception;

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
