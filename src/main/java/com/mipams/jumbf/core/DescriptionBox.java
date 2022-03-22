package com.mipams.jumbf.core;

import java.util.UUID;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.ContentTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;  
import lombok.NoArgsConstructor;  
import lombok.Setter;  
import lombok.ToString;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor  
@ToString  
public class DescriptionBox extends XTBox{

    private static final Logger logger = LoggerFactory.getLogger(DescriptionBox.class);

    private @Getter @Setter UUID uuid;

    private @Getter @Setter int toggle;

    private @Getter @Setter String label;

    private @Getter @Setter Integer id;

    private @Getter @Setter byte[] signature;

    @Override
    public void populate(ObjectNode input) throws Exception{
        super.populate(input);
        
        String type = input.get("type").asText();
        ContentTypeEnum contentType = ContentTypeEnum.getContentTypeFromString(type);
        setUuid(contentType.getTypeId());

        JsonNode node = input.get("requestable");
        toggle = (node == null) ? 0 : 1;

        node = input.get("label");

        if(node != null){
            setLabel(node.asText());
            toggle = toggle | 2;
        }

        node = input.get("id");

        if(node != null){
            setId(node.asInt());
            toggle = toggle | 4;
        }

        logger.debug("Signature is not supported");
    }

    @Override
    public ByteArrayOutputStream toBytes() throws Exception {   
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        output.write(CoreUtils.convertUUIDToByteArray(uuid));
        output.write(CoreUtils.convertIntToSingleElementByteArray(toggle));

        if(labelExists()){
            output.write(CoreUtils.convertStringToByteArray(label+"\0"));
        }

        if(idExists()){
            output.write(CoreUtils.convertIntToByteArray(id));
        }

        if(signatureExists()){
            logger.debug("Signature is not supported");
        }

        incrementActualSizeBy(output.size());
        setPayload(output);

        return super.toBytes();
    }
   
    public void parse(ByteArrayInputStream input) throws Exception{
        super.parse(input);

        logger.debug("The box is a description box");

        byte[] uuidTemp = new byte[16];

        if(input.read(uuidTemp, 0, 16) == -1){
            throw new Exception("JUMBF Box is corrupted");
        }

        UUID uuidVal = CoreUtils.convertByteArrayToUUID(uuidTemp);
        setUuid(uuidVal);
        incrementActualSizeBy(16);

        int toggleValue = 0;
        if((toggleValue = input.read()) == -1){
            throw new Exception("JUMBF Box is corrupted");
        }
        
        setToggle(toggleValue);
        incrementActualSizeBy(1);

        if(labelExists()){
            char charVal;
            StringBuilder str = new StringBuilder();

            while((charVal = (char) input.read()) != '\0') {
                str.append(charVal);
                incrementActualSizeBy(1);
            }
            //For the null character that we are not included in the variable
            incrementActualSizeBy(1);

            setLabel(str.toString());
        }

        if(idExists()){

            byte[] temp = new byte[4];

            if(input.read(temp, 0, 4) == -1){
                throw new Exception("JUMBF Box is corrupted");
            }

            int idVal = CoreUtils.convertByteArrayToInt(temp);
            setId(idVal);
            incrementActualSizeBy(4);
        }

        if(signatureExists()){
            byte[] signatureVal = new byte[32];

            if(input.read(signatureVal, 0, 32) == -1){
                throw new Exception("JUMBF Box is corrupted");
            }

            setSignature(signatureVal);
            incrementActualSizeBy(32);
        }

        verifyBoxSizeValidity();

        logger.debug("Discovered box: "+this.toString());

        return;
    }

    boolean labelExists(){
        return CoreUtils.isBitAtGivenPositionSet(toggle, 2);
    }

    boolean idExists(){
        return CoreUtils.isBitAtGivenPositionSet(toggle, 3);
    }

    boolean signatureExists(){
        return CoreUtils.isBitAtGivenPositionSet(toggle, 4);
    }

    void verifyBoxSizeValidity() throws Exception{
        if (getNominalBoxSizeInBytes() != getActualSize()){
            throw new Exception("Mismatch in the byte counting(Nominal: "+getNominalBoxSizeInBytes()+", Actual: "+getActualSize()+") of the Box: "+this.toString());
        }
    }

    @Override
    public String getBoxType() {
        return BoxTypeEnum.DescriptionBox.getType();
    }

    @Override
    public int getBoxTypeId() {
        return BoxTypeEnum.DescriptionBox.getTypeId();
    }
}