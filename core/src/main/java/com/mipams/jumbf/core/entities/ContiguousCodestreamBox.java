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
public class ContiguousCodestreamBox extends XTBox{
  
    private @Getter @Setter String pathToCodestream;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.ContiguousCodestreamBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {
        return (long) CoreUtils.getFileSizeFromPath(pathToCodestream);
    }
}