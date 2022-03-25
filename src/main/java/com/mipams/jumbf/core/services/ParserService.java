package com.mipams.jumbf.core.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mipams.jumbf.core.BoxServiceManager;
import com.mipams.jumbf.core.entities.JumbfBox;
import com.mipams.jumbf.core.entities.XTBox;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.CorruptedJumbfFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ParserService implements ParserInterface{

    private static final Logger logger = LoggerFactory.getLogger(ParserService.class); 

    @Autowired
    BoxServiceManager boxServiceManager;

    @Override
    public String parseMetadataFromJumbfFile(String path) throws MipamsException{

        try (InputStream input = new FileInputStream(path)){

            XTBox superbox = boxServiceManager.getSuperBoxService().parseFromJumbfFile(input);  

            logger.debug("Finish parsing. The complete JUMBF box is: {}",superbox.toString());

            return superbox.toString();
        } catch(FileNotFoundException e){
            throw new CorruptedJumbfFileException( "File {"+path+"} does not exist", e);
        }  catch(IOException e){
            throw new CorruptedJumbfFileException("Could not open file: "+path , e);
        }

    }
}