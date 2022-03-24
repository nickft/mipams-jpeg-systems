package com.mipams.jumbf.core.entities;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BadRequestException;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;
import com.mipams.jumbf.core.entities.XTBox;

import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  

import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor  
@ToString  
public class EmbeddedFileDescriptionBox extends XTBox {
  
    private @Getter @Setter int toggle;

    private @Getter @Setter MediaType mediaType;

    private @Getter @Setter String fileName;

    @Override
    public String getBoxType() {
        return BoxTypeEnum.EmbeddedFileDescriptionBox.getType();
    }

    @Override
    public int getBoxTypeId() {
        return BoxTypeEnum.EmbeddedFileDescriptionBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = getToggleSize();

        sum += getMediaTypeSize();

        if(fileNameExists()) sum += getFileNameSize();

        return sum;
    }

    public int getToggleSize(){
        return 1;
    }

    public int getMediaTypeSize(){
        return getMediaTypeWithEscapeCharacter().length();
    }

    public int getFileNameSize(){
        return getFileNameWithEscapeCharacter().length();
    }

    public boolean fileNameExists(){
        return CoreUtils.isBitAtGivenPositionSet(toggle, 0);
    }

    public boolean isContentReferencedExternally(){
        return CoreUtils.isBitAtGivenPositionSet(toggle, 1);
    }

    public void markAsExternalFile(){
        int value = 1;
        int updatedToggle = CoreUtils.setBitValueAtGivenPosition(toggle, 1, value);
    }

    public void markAsInternalFile(){
        int value = 0;
        int updatedToggle = CoreUtils.setBitValueAtGivenPosition(toggle, 1, value);
    }

    public String getMediaTypeWithEscapeCharacter(){
        return CoreUtils.addEscapeCharacterToText(getMediaType().toString());
    }

    public String getFileNameWithEscapeCharacter(){
        return CoreUtils.addEscapeCharacterToText(getFileName());
    }

    public void setMediaTypeFromString(String type) throws MipamsException{
        try{
            MediaType mediaType = CoreUtils.getMediaTypeFromString(type);
            setMediaType(mediaType);    
        } catch (IllegalArgumentException e){
            throw new MipamsException("Bad Media Type", e);
        } catch (NullPointerException e){
            throw new MipamsException("Media type not specified", e);
        }  
    }

    public String discoverFileName(){
        if(fileNameExists()){
            return getFileName();
        } else {
            return CoreUtils.randomStringGenerator() + "." + getMediaType().getSubtype();
        }
    }
}