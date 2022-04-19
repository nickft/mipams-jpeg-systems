package org.mipams.jumbf.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import org.mipams.jumbf.core.services.CoreParserService;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.services.CoreGeneratorService;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.demo.services.DemoRequestParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    CoreParserService parserService;

    @Autowired
    CoreGeneratorService generatorService;

    @Autowired
    DemoRequestParser demoRequestParser;

    @GetMapping("/parseMetadata")
    public ResponseEntity<?> parseJumbfMetadataFromPath(@RequestParam String fileName) {
        try {
            List<JumbfBox> boxList = parserService.parseMetadataFromJumbfFile(fileName);
            return ResponseEntity.ok().body(prepareResponse(boxList));
        } catch (MipamsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String prepareResponse(List<JumbfBox> boxList) {
        StringBuilder result = new StringBuilder("This .jumbf file contains the following boxes:\n");
        for (JumbfBox jumbfBox : boxList) {
            result.append(jumbfBox.toString());
        }
        return result.toString();
    }

    @PostMapping("/generateBox")
    public ResponseEntity<?> generateJumbfBytes(@RequestParam(required = false) String targetFile,
            @RequestBody JsonNode requestBody) {
        String outputFileName = targetFile == null ? "test.jumbf" : targetFile;

        try {
            List<JumbfBox> boxList = demoRequestParser.generateBoxFromRequest(requestBody);

            String filePath = generatorService.generateJumbfFileFromBox(boxList, outputFileName);

            String result = generateResultMessage(boxList, filePath);
            return ResponseEntity.ok().body(result);
        } catch (MipamsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String generateResultMessage(List<JumbfBox> jumbfBoxList, String path) {

        StringBuilder result = new StringBuilder("Jumbf file is stored in location ");

        result.append(path).append("\n");

        result.append("The JUMBF content is the following: \n");

        for (JumbfBox jumbfBox : jumbfBoxList) {
            result.append(jumbfBox.toString());
        }

        return result.toString();
    }
}
