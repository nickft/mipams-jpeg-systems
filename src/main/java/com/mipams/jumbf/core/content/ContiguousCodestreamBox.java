package com.mipams.jumbf.core.content;

import java.util.List;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.XTBox;
import com.mipams.jumbf.core.util.BadRequestException;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class ContiguousCodestreamBox extends XTBox{
    private static final Logger logger = LoggerFactory.getLogger(ContiguousCodestreamBox.class);
   
    private @Getter @Setter String pathToCodestream;


    @Override
    public void populateBody(ObjectNode input) throws MipamsException{
        
        String type = input.get("type").asText();

        if( !getBoxType().equals(type)){
            throw new MipamsException("Box type does not match with description type.");
        }

        String path = input.get("path").asText();
        setPathToCodestream(path);
    }

    @Override
    public long calculatePayloadSizeInBytes() throws MipamsException {

        double fileSize = CoreUtils.getFileSizeFromPath(pathToCodestream);

        if(CoreUtils.doesFileSizeExceedApplicationLimits(fileSize)){
            throw new BadRequestException("File is too large for the application. Check the available limits.");
        }

        return (long) fileSize;
    }

    @Override
    public void toBytes(FileOutputStream fileOutputStream) throws MipamsException {    
        super.toBytes(fileOutputStream);

        try (FileInputStream inputStream = new FileInputStream(pathToCodestream)){

            int n;
            while ((n = inputStream.read()) != -1) {
                fileOutputStream.write(n);
            }  
        } catch(FileNotFoundException e){
            logger.error("Coulnd not locate file", e);
        } catch (IOException e){
            logger.error("Coulnd not write to file", e);
        }
    }

    @Override
    public void parsePayload(InputStream input) throws MipamsException{

        pathToCodestream = CoreUtils.randomStringGenerator() + ".png";

        String fullPath = CoreUtils.getFullPath(pathToCodestream);

        try (FileOutputStream fileOutputStream = new FileOutputStream(fullPath)){            
            
            long nominalTotalSizeInBytes = getNominalPayloadSizeInBytes();

            int actualBytes = 0, n;

            while ((actualBytes < nominalTotalSizeInBytes) && ((n = input.read()) != -1)){
                fileOutputStream.write(n);
                actualBytes++;
            }

            fileOutputStream.close();

            logger.debug("Finished writing file to: "+fullPath);

        } catch (IOException e){
            logger.error("Coulnd not read Json content", e);
        } 
    }

    @Override
    public String getBoxType() {
        return BoxTypeEnum.ContiguousCodestreamBox.getType();
    }

    @Override
    public int getBoxTypeId() {
        return BoxTypeEnum.ContiguousCodestreamBox.getTypeId();
    }
}