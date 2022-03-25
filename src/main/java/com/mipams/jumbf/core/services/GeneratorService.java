package com.mipams.jumbf.core.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.BoxServiceManager;
import com.mipams.jumbf.core.entities.XTBox;
import com.mipams.jumbf.core.util.BadRequestException;
import com.mipams.jumbf.core.util.CoreUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GeneratorService implements GeneratorInterface{

    private static final Logger logger = LoggerFactory.getLogger(GeneratorService.class); 

    @Autowired
    BoxServiceManager boxServiceManager;

    @Value("${mipams.core.image_folder}")
    private String IMAGE_FOLDER;

    @Override
    public String generateJumbfFileFromRequest(ObjectNode inputNode) throws MipamsException{

        String path = CoreUtils.getFullPath(IMAGE_FOLDER, "test.jumbf");

        try(FileOutputStream fileOutputStream = new FileOutputStream(path)){
            
            XTBox superbox = boxServiceManager.getSuperBoxService().writeToJumbfFileFromRequest(inputNode, fileOutputStream);

            logger.debug("Created a new Superbox in file: "+path);
            return "JUMBF file is stored in the following location: "+path;                  
        } catch(FileNotFoundException e){
            throw new BadRequestException("File {"+path+"} does not exist", e);
        }  catch(IOException e){
            throw new BadRequestException("Could not open file: "+path , e);
        }
    }
}