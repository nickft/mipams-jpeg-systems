package mipams.jumbf.core.entities;

import java.util.List;
import java.util.ArrayList;

import mipams.jumbf.core.util.MipamsException;
import mipams.jumbf.core.util.BoxTypeEnum;
import mipams.jumbf.core.util.BoxTypeEnum;

import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor  
@ToString  
public class JumbfBox extends XTBox{

    private static final Logger logger = LoggerFactory.getLogger(JumbfBox.class);
   
    protected @Getter @Setter DescriptionBox descriptionBox;
    protected @Getter @Setter List<XTBox> contentList;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.JumbfBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = descriptionBox.getBoxSizeFromXTBoxHeaders();

        for (XTBox content: getContentList()){
            sum += content.getBoxSizeFromXTBoxHeaders();
        }

        return sum;
    }
}