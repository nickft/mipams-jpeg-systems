package com.mipams.jumbf.core.entities;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.entities.XTBox;

import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor  
@ToString  
public class JsonBox extends XTBox{

    private static final Logger logger = LoggerFactory.getLogger(JsonBox.class);
   
    private @Getter @Setter ObjectNode  jsonContent;

    @Override
    public String getBoxType() {
        return BoxTypeEnum.JsonBox.getType();
    }

    @Override
    public int getBoxTypeId() {
        return BoxTypeEnum.JsonBox.getTypeId();
    }


    @Override
    public long calculatePayloadSize() throws MipamsException {

        ObjectMapper om = new ObjectMapper();
        final ObjectWriter writer = om.writer();
        
        byte[] bytes;

        try{
            bytes = writer.writeValueAsBytes(jsonContent);
            return bytes.length;
        } catch (JsonProcessingException e){
            throw new MipamsException("Coulnd not convert json to byte array", e);
        }
    }
}