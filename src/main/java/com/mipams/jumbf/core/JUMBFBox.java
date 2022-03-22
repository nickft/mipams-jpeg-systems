package com.mipams.jumbf.core;

import java.util.List;
import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;

import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BoxTypeEnum;

import java.util.ArrayList;
import java.io.InputStream;
import java.io.FileOutputStream;

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
    public void populateBody(ObjectNode input) throws MipamsException{
        String type = input.get("type").asText();

        if(!BoxTypeEnum.JUMBFBox.getType().equals(type)){
            throw new MipamsException();
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
    public long calculatePayloadSizeInBytes() throws MipamsException {

        long sum = descriptionBox.calculatePayloadSizeInBytes();

        for (XTBox content: contentList){
            sum += content.calculatePayloadSizeInBytes();
        }

        return sum;
    }

    @Override
    public void toBytes(FileOutputStream fileOutputStream) throws MipamsException{
        super.toBytes(fileOutputStream);
           
        descriptionBox.toBytes(fileOutputStream);

        for (XTBox content: contentList){
            content.toBytes(fileOutputStream);
        }
    }

    @Override
    public void parsePayload(InputStream input) throws MipamsException{
        long actualSize = 0;

        descriptionBox = new DescriptionBox();
        descriptionBox.parse(input);

        actualSize += descriptionBox.getNominalBoxSizeInBytes();

        contentList = new ArrayList<>();

        BoxFactory boxFactory = new BoxFactory();

        do{
            XTBox contentBox = boxFactory.generateBoxBasedOnUUID(descriptionBox.getUuid());
            contentBox.parse(input);
            contentList.add(contentBox);

            actualSize += contentBox.getNominalBoxSizeInBytes();
        } while(actualSize < getNominalPayloadSizeInBytes());

        verifyBoxSizeValidity(actualSize);
    }

    void verifyBoxSizeValidity(long actualSize) throws MipamsException{
        if (getNominalPayloadSizeInBytes() != actualSize){
            throw new MipamsException("Mismatch in the byte counting(Nominal: "+getNominalBoxSizeInBytes()+", Actual: "+Long.toString(actualSize)+") of the Box: "+this.toString());
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