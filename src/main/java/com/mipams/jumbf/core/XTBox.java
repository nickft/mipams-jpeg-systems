package com.mipams.jumbf.core;

import java.nio.ByteBuffer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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

    private @Getter @Setter ByteArrayOutputStream payload;

    private @Getter @Setter long actualSize;

    public void populate(ObjectNode input) throws Exception{
        setTBox(getBoxTypeId());
    }

    @Override
    public ByteArrayOutputStream toBytes() throws Exception {
        setLBox(getPayload().size() + 8);   

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        output.write(CoreUtils.convertIntToByteArray(LBox));
        output.write(CoreUtils.convertIntToByteArray(TBox));
        output.write(getPayload().toByteArray());

        return output;
    }

    @Override
    public void parse(ByteArrayInputStream input) throws Exception{      
        logger.debug("Start parsing a new box");

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
        incrementActualSizeBy(4);

        if(input.read(temp, 0, 4) == -1){
            throw new Exception("JUMBF Box is corrupted");
        }

        value = CoreUtils.convertByteArrayToInt(temp);
        setTBox(value);
        incrementActualSizeBy(4);

        if(LBox == 1){
            temp = new byte[8];
            long longVal;

            if(input.read(temp, 0, 8) == -1){
                throw new Exception("JUMBF Box is corrupted");
            }

            longVal = CoreUtils.convertByteArrayToLong(temp);
            setXBox(longVal);
            incrementActualSizeBy(8);
        }

        logger.debug("The box is of type: "+Integer.toString(TBox) + " and has a total length of "+getPayloadSizeInBytes());
        
        return;
    }

    protected long getNominalBoxSizeInBytes(){
        return isXBoxEnabled() ? XBox : LBox;
    }

    public boolean isXBoxEnabled(){
        return LBox == 1 && (XBox != null);
    }

    public long getPayloadSizeInBytes(){
        long payloadSize = (LBox == 1) ? XBox - (4 + 4 + 8) : LBox - (4 + 4);
        return payloadSize;
    }

    protected void incrementActualSizeBy(long byteLength){
        setActualSize(actualSize+byteLength);
    }

    public abstract String getBoxType();

    public abstract int getBoxTypeId();
}
