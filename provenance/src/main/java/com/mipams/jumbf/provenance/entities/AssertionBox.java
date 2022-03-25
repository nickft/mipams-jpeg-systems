package mipams.jumbf.provenance.entities;

import java.util.List;
import java.util.ArrayList;

import mipams.jumbf.provenance.util.BoxTypeEnum;


import mipams.jumbf.core.util.MipamsException;
import mipams.jumbf.core.entities.JumbfBox;
import mipams.jumbf.core.entities.DescriptionBox;

import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor  
@ToString  
public class AssertionBox extends JumbfBox{

    private static final Logger logger = LoggerFactory.getLogger(AssertionBox.class); 

    @Override
    public int getBoxTypeId() {
        return BoxTypeEnum.AssertionBox.getTypeId();
    }

}