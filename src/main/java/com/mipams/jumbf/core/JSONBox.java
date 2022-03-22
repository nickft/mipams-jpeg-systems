package com.mipams.jumbf.core;

import java.util.List;

import org.springframework.stereotype.Component;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.mipams.jumbf.core.util.BoxTypeEnum;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor  
@ToString  
@Component
public class JSONBox extends XTBox{

    private static final Logger logger = LoggerFactory.getLogger(JSONBox.class);
   
    private @Getter @Setter ObjectNode  jsonContent;

    @Override
    public void populateBody(ObjectNode input) throws Exception{
        
        String type = input.get("type").asText();

        if( !BoxTypeEnum.JSONBox.getType().equals(type)){
            throw new Exception("Bad request");
        }

        ObjectNode payloadNode = (ObjectNode) input.get("payload");
        setJsonContent(payloadNode);
    }

    @Override
    public long calculatePayloadSizeInBytes() throws Exception {

        ObjectMapper om = new ObjectMapper();
        final ObjectWriter writer = om.writer();

        byte[] bytes = writer.writeValueAsBytes(jsonContent);

        return bytes.length;
    }

    @Override
    public void toBytes(FileOutputStream fileOutputStream) throws Exception {    
        super.toBytes(fileOutputStream);
        
        ObjectMapper om = new ObjectMapper();
        final ObjectWriter writer = om.writer();

        final byte[] bytes = writer.writeValueAsBytes(jsonContent);

        fileOutputStream.write(bytes);
    }

    @Override
    public void parsePayload(ByteArrayInputStream input) throws Exception{
        if(isXBoxEnabled()){
            throw new Exception("JSON content is huge. Do not support it.");
        }

        logger.debug("The box is a JSON box");
        
        int jsonSize = (int) getNominalPayloadSizeInBytes();

        ObjectMapper om = new ObjectMapper();
        final ObjectReader reader = om.reader();

        byte[] temp = new byte[jsonSize];

        input.read(temp, 0, jsonSize);

        ObjectNode jsonContent = (ObjectNode) reader.readTree(new ByteArrayInputStream(temp));
        logger.info(Integer.toString(input.available()));
        setJsonContent(jsonContent);

        logger.debug("Discovered box: "+this.toString());
    }

    @Override
    public String getBoxType() {
        return BoxTypeEnum.JSONBox.getType();
    }

    @Override
    public int getBoxTypeId() {
        return BoxTypeEnum.JSONBox.getTypeId();
    }

}