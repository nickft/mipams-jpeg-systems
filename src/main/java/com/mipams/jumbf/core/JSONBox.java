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
import java.io.ByteArrayOutputStream;

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
    public void populate(ObjectNode input) throws Exception{
        super.populate(input);
        
        String type = input.get("type").asText();

        if( !BoxTypeEnum.JSONBox.getType().equals(type)){
            throw new Exception("Bad request");
        }

        ObjectNode payloadNode = (ObjectNode) input.get("payload");
        setJsonContent(payloadNode);
    }

    public ByteArrayOutputStream toBytes() throws Exception {   
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ObjectMapper om = new ObjectMapper();
        final ObjectWriter writer = om.writer();

        final byte[] bytes = writer.writeValueAsBytes(jsonContent);

        output.write(bytes);

        incrementActualSizeBy(output.size());
        setPayload(output);

        return super.toBytes();
    }

    @Override
    public void parse(ByteArrayInputStream input) throws Exception{
        super.parse(input);

        logger.info(Integer.toString(input.available()));

        if(isXBoxEnabled()){
            throw new Exception("JSON content is huge. Do not support it.");
        }

        logger.debug("The box is a JSON box");
        
        int jsonSize = (int) getPayloadSizeInBytes();

        logger.info(Long.toString(getPayloadSizeInBytes()));

        ObjectMapper om = new ObjectMapper();
        final ObjectReader reader = om.reader();

        byte[] temp = new byte[getLBox()];

        input.read(temp, 0, getLBox());

        ObjectNode jsonContent = (ObjectNode) reader.readTree(new ByteArrayInputStream(temp));
        logger.info(Integer.toString(input.available()));
        setJsonContent(jsonContent);
        incrementActualSizeBy(jsonSize);

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