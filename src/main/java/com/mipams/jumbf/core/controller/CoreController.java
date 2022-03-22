package com.mipams.jumbf.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.mipams.jumbf.core.JUMBFBox;
import com.mipams.jumbf.core.ParserService;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.GeneratorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/core/v1")
public class CoreController {
    
    private static final Logger logger = LoggerFactory.getLogger(CoreController.class);
    
    @Autowired
    ParserService parserService;

    @Autowired
    GeneratorService generatorService;

    @GetMapping("/parseMetadata")
    public String parseJumbfMetadataFromPath(@RequestParam String path) throws MipamsException {
        parserService.parse(path);
        return "Parsing is on the way.";
    }

    @PostMapping("/generateBox")
    public String generateJumbfBytes(@RequestBody JsonNode requestBody) throws MipamsException {

        logger.debug(requestBody.toString());

        generatorService.generate((ObjectNode) requestBody);
        
        return "Generating is on the way.";
    }



}