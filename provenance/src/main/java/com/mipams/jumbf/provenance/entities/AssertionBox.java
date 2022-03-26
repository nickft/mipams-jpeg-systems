package org.mipams.jumbf.provenance.entities;

import java.util.List;
import java.util.ArrayList;

import org.mipams.jumbf.provenance.util.BoxTypeEnum;


import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.DescriptionBox;

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
    public int getTypeId() {
        return BoxTypeEnum.AssertionBox.getTypeId();
    }

}