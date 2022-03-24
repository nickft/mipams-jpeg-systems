package com.mipams.jumbf.core.entities;

import java.util.UUID;

import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;
import com.mipams.jumbf.core.util.MipamsException;


import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor  
@ToString  
public class DescriptionBox extends XTBox {

    private static final Logger logger = LoggerFactory.getLogger(DescriptionBox.class);

    private @Getter @Setter UUID uuid;

    private @Getter @Setter int toggle;

    private @Getter @Setter String label;

    private @Getter @Setter Integer id;

    private @Getter @Setter byte[] signature;

    @Override
    public String getBoxType() {
        return BoxTypeEnum.DescriptionBox.getType();
    }

    @Override
    public int getBoxTypeId() {
        return BoxTypeEnum.DescriptionBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {
        long sum = getUuidSize();

        sum += getToggleSize();

        if(labelExists()) sum += getLabelSize();

        if(idExists()) sum += getIdSize();

        if(signatureExists()) sum += getSignatureSize();

        return sum;
    }

    int getUuidSize(){
        return CoreUtils.UUID_BYTE_SIZE;
    }

    int getToggleSize(){
        return 1;
    }

    int getIdSize(){
        return CoreUtils.INT_BYTE_SIZE;
    }

    int getSignatureSize(){
        return 32;
    }

    long getLabelSize(){
        return CoreUtils.addEscapeCharacterToText(getLabel()).length();
    }
   
    public boolean labelExists(){
        return CoreUtils.isBitAtGivenPositionSet(toggle, 2);
    }

    public boolean idExists(){
        return CoreUtils.isBitAtGivenPositionSet(toggle, 3);
    }

    public boolean signatureExists(){
        return CoreUtils.isBitAtGivenPositionSet(toggle, 4);
    }

    public String getLabelWithEscapeCharacter(){
        return CoreUtils.addEscapeCharacterToText(getLabel());
    }
    
    
}