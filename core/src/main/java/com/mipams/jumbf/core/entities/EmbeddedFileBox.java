package mipams.jumbf.core.entities;

import java.util.List;

import org.springframework.stereotype.Component;

import mipams.jumbf.core.util.MipamsException;
import mipams.jumbf.core.util.BoxTypeEnum;
import mipams.jumbf.core.util.CoreUtils;
import mipams.jumbf.core.entities.XTBox;

import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor  
@ToString  
public class EmbeddedFileBox extends XTBox{
  
    private @Getter @Setter EmbeddedFileDescriptionBox descriptionBox;

    private @Getter @Setter String fileUrl;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.EmbeddedFileBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = getDescriptionBox().getBoxSizeFromXTBoxHeaders();

        if(getDescriptionBox().isContentReferencedExternally()){
            sum += getUrlSize();
        } else {
            sum += CoreUtils.getFileSizeFromPath(getFileUrl());
        }

        return sum;
    }

    private long getUrlSize(){
        return CoreUtils.addEscapeCharacterToText(getFileUrl()).length();
    }
}