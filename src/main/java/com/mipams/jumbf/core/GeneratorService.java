package com.mipams.jumbf.core;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.FileOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GeneratorService implements GeneratorInterface{

    private static final Logger logger = LoggerFactory.getLogger(GeneratorService.class); 

    @Override
    public void generate(ObjectNode input) throws Exception{

        
        FileOutputStream fileOutputStream = new FileOutputStream("/home/nikos/Desktop/test.jumb");
        JUMBFBox superbox = new JUMBFBox();

        superbox.populate(input);
        logger.debug("Created a new Superbox");
        logger.debug(superbox.toString());

        superbox.toBytes(fileOutputStream);    
            
        logger.debug("Finish parsing. JUMBF Box is stored in file: ");
        logger.debug(superbox.toString());
    }
}