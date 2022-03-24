package com.mipams.jumbf.core.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import com.mipams.jumbf.core.entities.EmbeddedFileBox;
import com.mipams.jumbf.core.entities.XTBox;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BadRequestException;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmbeddedFileBoxService extends XTBoxService<EmbeddedFileBox>{

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedFileBoxService.class); 

    @Value("${mipams.core.image_folder}")
    private String IMAGE_FOLDER;

    @Value("${mipams.core.max_file_size_in_bytes}")
    private long MAX_FILE_SIZE;

    @Autowired
    EmbeddedFileDescriptionBoxService embeddedFileDescriptionBoxService;

    @Override
    protected EmbeddedFileBox initializeBox() throws MipamsException{
        return new EmbeddedFileBox();
    }

    @Override
    protected void populateBox(EmbeddedFileBox embeddedFileBox, ObjectNode input) throws MipamsException{
              
        String type = input.get("type").asText();

        if( !embeddedFileBox.getBoxType().equals(type)){
            throw new BadRequestException("Box type does not match with description type.");
        }

        ObjectNode descriptionNode = (ObjectNode) input.get("description");

        embeddedFileBox.setDescriptionBox(embeddedFileDescriptionBoxService.discoverXTBox(descriptionNode));

        String path = input.get("path").asText();

        if(path == null) {
            throw new BadRequestException("Path is not specified");
        }

        embeddedFileBox.setFileUrl(path);

        if(!embeddedFileBox.getDescriptionBox().isContentReferencedExternally()){
            if(doesFileSizeExceedApplicationLimits(path)){
                throw new BadRequestException("File is too large for the application. Check the available limits.");
            }
        }
    }

    protected boolean doesFileSizeExceedApplicationLimits(String filePath) throws MipamsException{
        double size = CoreUtils.getFileSizeFromPath(filePath);
        return size > MAX_FILE_SIZE || size > Long.MAX_VALUE;        
    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(EmbeddedFileBox embeddedFileBox, FileOutputStream fileOutputStream) throws MipamsException{

        embeddedFileDescriptionBoxService.writeBoxToJumbfFile(embeddedFileBox.getDescriptionBox(), fileOutputStream);

        if(embeddedFileBox.getDescriptionBox().isContentReferencedExternally()){
            writeUrlToJumbfBox(embeddedFileBox, fileOutputStream);
        } else {
            writeFileToJumbfBox(embeddedFileBox, fileOutputStream);
        }

    }

    private void writeUrlToJumbfBox(EmbeddedFileBox embeddedFileBox, FileOutputStream fileOutputStream) throws MipamsException{
        try{
            fileOutputStream.write(CoreUtils.convertStringToByteArray(embeddedFileBox.getFileUrl()));
        } catch (IOException e){
            throw new MipamsException(e);
        }
    }

    private void writeFileToJumbfBox(EmbeddedFileBox embeddedFileBox, FileOutputStream fileOutputStream) throws MipamsException{
        CoreUtils.writeFileContentToOutput(embeddedFileBox.getFileUrl(), fileOutputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(EmbeddedFileBox embeddedFileBox, InputStream input) throws MipamsException{
        logger.debug("Embedded File box");

        String fileName = embeddedFileBox.getDescriptionBox().discoverFileName();
        
        String fullPath = CoreUtils.getFullPath(IMAGE_FOLDER, fileName);

        embeddedFileBox.setFileUrl(fullPath);

        if(embeddedFileBox.getDescriptionBox().isContentReferencedExternally()) {
            logger.debug("File is externally stored. No need to fetch it.");
        } else {
            try (FileOutputStream fileOutputStream = new FileOutputStream(fullPath)){            
                
                long nominalTotalSizeInBytes = embeddedFileBox.getPayloadSizeFromXTBoxHeaders();

                int actualBytes = 0, n;

                while ((actualBytes < nominalTotalSizeInBytes) && ((n = input.read()) != -1)){
                    fileOutputStream.write(n);
                    actualBytes++;
                }

                logger.debug("File is internally stored. The file is extracted and stored in " + fullPath);
            } catch (IOException e){
                throw new MipamsException("Coulnd not read Json content", e);
            }
        }
    }

    @Override
    public BoxTypeEnum serviceIsResponsibleForBoxType(){
        return BoxTypeEnum.ContiguousCodestreamBox;
    }
}