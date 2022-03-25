package com.mipams.jumbf.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.mipams.jumbf.core.services.ParserService;
import com.mipams.jumbf.core.services.GeneratorService;
import com.mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;

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
        return parserService.parseMetadataFromJumbfFile(path);
    }

    @PostMapping("/generateBox")
    public String generateJumbfBytes(@RequestBody JsonNode requestBody) throws MipamsException {
        return generatorService.generateJumbfFileFromRequest((ObjectNode) requestBody);
    }
}