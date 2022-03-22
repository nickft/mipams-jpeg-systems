package com.mipams.jumbf.core;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BadRequestException;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public void populateBody(ObjectNode input) throws MipamsException{
        
        String type = input.get("type").asText();

        if( !BoxTypeEnum.JSONBox.getType().equals(type)){
            throw new MipamsException("Box type does not match with description type.");
        }

        ObjectNode payloadNode = (ObjectNode) input.get("payload");
        setJsonContent(payloadNode);
    }

    @Override
    public long calculatePayloadSizeInBytes() throws MipamsException {

        ObjectMapper om = new ObjectMapper();
        final ObjectWriter writer = om.writer();
        
        byte[] bytes;

        try{
            bytes = writer.writeValueAsBytes(jsonContent);
            return bytes.length;
        } catch (JsonProcessingException e){
            logger.error("Coulnd not convert json to byte array", e);
            return 0;
        }
    }

    @Override
    public void toBytes(FileOutputStream fileOutputStream) throws MipamsException {    
        super.toBytes(fileOutputStream);
        
        ObjectMapper om = new ObjectMapper();
        final ObjectWriter writer = om.writer();

        try{
            byte[] bytes = writer.writeValueAsBytes(jsonContent);
            fileOutputStream.write(bytes);
        } catch (JsonProcessingException e){
            logger.error("Coulnd not convert json to byte array", e);
        } catch (IOException e){
            logger.error("Coulnd not write to file", e);
        }        
    }

    @Override
    public void parsePayload(InputStream input) throws MipamsException{

        try{
            if(isXBoxEnabled()){
                throw new MipamsException("JSON content is huge. Do not support it.");
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
        } catch (IOException e){
            logger.error("Coulnd not read Json content", e);
        } 
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