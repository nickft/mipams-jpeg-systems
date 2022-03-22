package com.mipams.jumbf.core;

import java.util.List;
import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;

import com.mipams.jumbf.core.util.BoxTypeEnum;

import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@NoArgsConstructor  
@ToString  
public class JUMBFBox extends XTBox{

    private static final Logger logger = LoggerFactory.getLogger(JUMBFBox.class);
   
    private @Getter @Setter DescriptionBox descriptionBox;
    private @Getter @Setter List<XTBox> contentList;

    @Override
    public void populate(ObjectNode input) throws Exception{
        super.populate(input);

        String type = input.get("type").asText();

        if(!BoxTypeEnum.JUMBFBox.getType().equals(type)){
            throw new Exception("Bad request");
        }

        ObjectNode descriptionNode = (ObjectNode) input.get("description");
        descriptionBox = new DescriptionBox();
        descriptionBox.populate(descriptionNode);

        JsonNode contentNodeList = input.get("contentList");

        Iterator<JsonNode> contentIterator = contentNodeList.elements();
        contentList = new ArrayList<>();

        BoxFactory boxFactory = new BoxFactory();
      
        while (contentIterator.hasNext()){
            XTBox contentBox = boxFactory.generateBoxBasedOnUUID(descriptionBox.getUuid());
            contentBox.populate((ObjectNode) contentIterator.next());
            contentList.add(contentBox);
        }
    }

    @Override
    public ByteArrayOutputStream toBytes() throws Exception {   
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        output.write(descriptionBox.toBytes().toByteArray());

        for (XTBox content: contentList){
            output.write(content.toBytes().toByteArray());
        }

        incrementActualSizeBy(output.size());
        setPayload(output);

        return super.toBytes();
    }

    @Override
    public void parse(ByteArrayInputStream input) throws Exception{
        super.parse(input);

        logger.debug("The box is a JUMBF box");

        descriptionBox = new DescriptionBox();
        descriptionBox.parse(input);

        incrementActualSizeBy(descriptionBox.getNominalBoxSizeInBytes());

        contentList = new ArrayList<>();

        logger.debug("Start parsing the contents of the box");

        BoxFactory boxFactory = new BoxFactory();

        do{
            XTBox contentBox = boxFactory.generateBoxBasedOnUUID(descriptionBox.getUuid());
            contentBox.parse(input);
            contentList.add(contentBox);

            incrementActualSizeBy(contentBox.getNominalBoxSizeInBytes());
        } while(getActualSize() < getNominalBoxSizeInBytes());

        logger.debug("Finish parsing the contents of the box");

        verifyBoxSizeValidity();
    }

    void verifyBoxSizeValidity() throws Exception{
        if (getNominalBoxSizeInBytes() != getActualSize()){
            throw new Exception("Mismatch in the byte counting(Nominal: "+getNominalBoxSizeInBytes()+", Actual: "+getActualSize()+") of the Box: "+this.toString());
        }
    }

    @Override
    public String getBoxType() {
        return BoxTypeEnum.JUMBFBox.getType();
    }

    @Override
    public int getBoxTypeId() {
        return BoxTypeEnum.JUMBFBox.getTypeId();
    }

}