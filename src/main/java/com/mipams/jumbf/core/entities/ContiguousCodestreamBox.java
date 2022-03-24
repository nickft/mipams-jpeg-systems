package com.mipams.jumbf.core.entities;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;
import com.mipams.jumbf.core.entities.XTBox;

import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor  
@ToString  
public class ContiguousCodestreamBox extends XTBox{
  
    private @Getter @Setter String pathToCodestream;

    @Override
    public String getBoxType() {
        return BoxTypeEnum.ContiguousCodestreamBox.getType();
    }

    @Override
    public int getBoxTypeId() {
        return BoxTypeEnum.ContiguousCodestreamBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {
        return (long) CoreUtils.getFileSizeFromPath(pathToCodestream);
    }
}