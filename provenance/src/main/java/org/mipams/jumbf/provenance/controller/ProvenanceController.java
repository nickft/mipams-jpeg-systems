package org.mipams.jumbf.provenance.controller;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.provenance.services.GeneratorService;
import org.mipams.jumbf.provenance.services.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/provenance/v1")
public class ProvenanceController {

    @Autowired
    ParserService parserService;

    @Autowired
    GeneratorService generatorService;

    @GetMapping("/test")
    public String test() throws MipamsException {
        return generatorService.generateJumbfFileFromBox(null);
    }

}