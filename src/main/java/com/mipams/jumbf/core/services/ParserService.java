package com.mipams.jumbf.core;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mipams.jumbf.core.util.MipamsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ParserService implements ParserInterface{

    private static final Logger logger = LoggerFactory.getLogger(ParserService.class); 

    @Autowired
    BoxFactory boxFactory;

    @Override
    public void parse(String path) throws MipamsException{

        try (InputStream input = new FileInputStream(path)){

            JUMBFBox superbox = new JUMBFBox();
            superbox.parse(input);  

            logger.debug("Finish parsing. The complete JUMBF box is: {}",superbox.toString());
        } catch(FileNotFoundException e){
            logger.error("File {} does not exist",path , e);
        }  catch(IOException e){
            logger.error("Could not open file",path , e);
        }

    }
}