package com.mipams.jumbf.core.entities;

import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;
import com.mipams.jumbf.core.util.MipamsException;

import com.mipams.jumbf.core.entities.BoxInterface;

import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;

@NoArgsConstructor  
@ToString  
public abstract class XTBox implements BoxInterface{

    private @Getter @Setter int LBox;

    private @Getter @Setter int TBox;

    private @Getter @Setter Long XBox;

    public abstract String getBoxType();

    public abstract int getBoxTypeId();

    public final long getPayloadSizeFromXTBoxHeaders(){

        long payloadSize = getBoxSizeFromXTBoxHeaders();

        payloadSize -= getLBoxSize() + getTBoxSize();

        if(isXBoxEnabled()) payloadSize -= getXBoxSize();

        return payloadSize;
    }

    public final long getBoxSizeFromXTBoxHeaders(){
        return isXBoxEnabled() ? getXBox() : getLBox();
    }

    public final boolean isXBoxEnabled(){
        return LBox == 1 && (XBox != null);
    }
    
    public final void setXTHeadersBasedOnBox() throws MipamsException{   

        setTBox(getBoxTypeId());

        long size = getLBoxSize() + getTBoxSize() +calculatePayloadSize();

        if(size > Integer.MAX_VALUE){                       
            size += getXBoxSize();
            setLBox(1);
            setXBox(size);
        } else {
            setLBox((int) size);
        }
    }

    int getLBoxSize(){
        return CoreUtils.INT_BYTE_SIZE;
    }

    int getTBoxSize(){
        return CoreUtils.INT_BYTE_SIZE;
    }

    int getXBoxSize(){
        return CoreUtils.LONG_BYTE_SIZE;
    }

    public abstract long calculatePayloadSize() throws MipamsException;

    public boolean checkEqualityWithBoxType(String boxType){
        return (boxType != null) ? getBoxType().equals(boxType): false;
    }
}
