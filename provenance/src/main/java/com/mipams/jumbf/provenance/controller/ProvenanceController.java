package mipams.jumbf.provenance.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mipams.jumbf.provenance.services.ParserService;
import mipams.jumbf.provenance.services.GeneratorService;
import mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/provenance/v1")
public class ProvenanceController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProvenanceController.class);
    
    @Autowired
    ParserService parserService;

    @Autowired
    GeneratorService generatorService;

    @GetMapping("/test")
    public String test() throws MipamsException {
        return generatorService.generateJumbfFileFromRequest(null);
    }

}