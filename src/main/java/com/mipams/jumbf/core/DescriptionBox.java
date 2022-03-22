package com.mipams.jumbf.core;

import java.util.UUID;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.ContentTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BadRequestException;

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
    public void populateBody(ObjectNode input) throws MipamsException{
        
        String type = input.get("type").asText();
        
        ContentTypeEnum contentType = ContentTypeEnum.getContentTypeFromString(type);

        if(contentType == null){
            throw new BadRequestException("Content Type: "+type+" is not supported");
        }

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
    public long calculatePayloadSizeInBytes() throws MipamsException {
        long sum = 16; // UUID length

        sum ++; // toggle

        if(labelExists()) sum += label.length() + 1;

        if(idExists()) sum += 4;

        if(signatureExists()) sum += 32;

        return sum;
    }

    @Override
    public void toBytes(FileOutputStream fileOutputStream) throws MipamsException {   
        super.toBytes(fileOutputStream);

        try{        
            fileOutputStream.write(CoreUtils.convertUUIDToByteArray(uuid));
            fileOutputStream.write(CoreUtils.convertIntToSingleElementByteArray(toggle));

            if(labelExists()){
                fileOutputStream.write(CoreUtils.convertStringToByteArray(label+"\0"));
            }

            if(idExists()){
                fileOutputStream.write(CoreUtils.convertIntToByteArray(id));
            }

            if(signatureExists()){
                fileOutputStream.write(signature);
            }
        } catch (IOException e){
            logger.error("Could not write to file.", e);
        }
    }
   
    @Override
    public void parsePayload(InputStream input) throws MipamsException{
        logger.debug("Description box");

        long actualSize = 0;

        try{
            byte[] uuidTemp = new byte[16];

            if(input.read(uuidTemp, 0, 16) == -1){
                throw new MipamsException();
            }

            UUID uuidVal = CoreUtils.convertByteArrayToUUID(uuidTemp);
            setUuid(uuidVal);
            actualSize +=16;

            int toggleValue = 0;
            if((toggleValue = input.read()) == -1){
                throw new MipamsException();
            }
            actualSize ++;
            setToggle(toggleValue);

            if(labelExists()){
                char charVal;
                StringBuilder str = new StringBuilder();

                while((charVal = (char) input.read()) != '\0') {
                    str.append(charVal);
                    actualSize ++;
                }
                //For the null character that we are not included in the variable
                actualSize ++;

                setLabel(str.toString());
            }

            if(idExists()){

                byte[] temp = new byte[4];

                if(input.read(temp, 0, 4) == -1){
                    throw new MipamsException();
                }

                int idVal = CoreUtils.convertByteArrayToInt(temp);
                setId(idVal);
                actualSize +=4;
            }

            if(signatureExists()){
                byte[] signatureVal = new byte[32];

                if(input.read(signatureVal, 0, 32) == -1){
                    throw new MipamsException();
                }

                setSignature(signatureVal);
                actualSize +=32;
            }
        } catch (IOException e){
            logger.error("Failed to read description box after {} bytes.", Long.toString(actualSize), e);
        }

        verifyBoxSizeValidity(actualSize);

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

    void verifyBoxSizeValidity(long actualSize) throws MipamsException{
        if (getNominalPayloadSizeInBytes() != actualSize){
            throw new MipamsException("Mismatch in the byte counting(Nominal: "+getNominalPayloadSizeInBytes()+", Actual: "+Long.toString(actualSize)+") of the Box: "+this.toString());
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