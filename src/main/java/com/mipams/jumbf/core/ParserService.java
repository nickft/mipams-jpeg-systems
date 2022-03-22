package com.mipams.jumbf.core;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ParserService implements ParserInterface{

    private static final Logger logger = LoggerFactory.getLogger(ParserService.class); 

    @Autowired
    BoxFactory boxFactory;

    @Override
    public void parse(ByteArrayInputStream inputStream) throws Exception{

        JUMBFBox superbox = new JUMBFBox();

        superbox.parse(inputStream);    
        
        logger.debug("Finish parsing. The complete JUMBF box is: ");
        logger.debug(superbox.toString());
    }
}