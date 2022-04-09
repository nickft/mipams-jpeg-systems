package org.mipams.jumbf.privacy_security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.services.CoreGeneratorService;
import org.mipams.jumbf.core.services.CoreParserService;
import org.mipams.jumbf.core.util.MipamsException;

@RestController
@RequestMapping("/privsec/v1")
public class PrivacySecurityController {

    @Autowired
    CoreParserService parserService;

    @Autowired
    CoreGeneratorService generatorService;

    @GetMapping("/parseMetadata")
    public String parseJumbfMetadataFromPath(@RequestParam String path) throws MipamsException {
        List<JumbfBox> boxList = parserService.parseMetadataFromJumbfFile(path);
        return boxList.toString();
    }

    @PostMapping("/generateBox")
    public String generateJumbfBytes(@RequestBody JsonNode requestBody) throws MipamsException {
        List<JumbfBox> boxList = generatorService.generateBoxFromRequest(requestBody);
        return generatorService.generateJumbfFileFromBox(boxList);
    }
}
