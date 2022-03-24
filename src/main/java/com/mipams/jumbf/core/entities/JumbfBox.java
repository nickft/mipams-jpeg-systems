package com.mipams.jumbf.core.entities;

import java.util.List;
import java.util.ArrayList;

import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BoxTypeEnum;

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
   
    private @Getter @Setter DescriptionBox descriptionBox;
    private @Getter @Setter List<XTBox> contentList;

    @Override
    public String getBoxType() {
        return BoxTypeEnum.JumbfBox.getType();
    }

    @Override
    public int getBoxTypeId() {
        return BoxTypeEnum.JumbfBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = descriptionBox.getBoxSizeFromXTBoxHeaders();

        for (XTBox content: contentList){
            sum += content.getBoxSizeFromXTBoxHeaders();
        }

        return sum;
    }

    public void addContentBoxToList(XTBox xtBox){

        if(getContentList() == null){
            setContentList(new ArrayList<XTBox>());
        }

        getContentList().add(xtBox);
    }

}