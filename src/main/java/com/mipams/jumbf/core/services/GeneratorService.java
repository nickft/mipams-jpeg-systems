package com.mipams.jumbf.core;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mipams.jumbf.core.util.MipamsException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GeneratorService implements GeneratorInterface{

    private static final Logger logger = LoggerFactory.getLogger(GeneratorService.class); 

    @Override
    public void generate(ObjectNode input) throws MipamsException{

        String path = "/home/nikos/Desktop/test.jumb";

        try(FileOutputStream fileOutputStream = new FileOutputStream(path);){
            
            JUMBFBox superbox = new JUMBFBox();
            superbox.populate(input);
            
            superbox.toBytes(fileOutputStream);

            logger.debug("Created a new Superbox in file: {}",path);
            logger.debug(superbox.toString());                  

        } catch(FileNotFoundException e){
            logger.error("File {} does not exist",path , e);
        }  catch(IOException e){
            logger.error("Could not open file",path , e);
        }
    }
}