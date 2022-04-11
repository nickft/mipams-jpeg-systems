package org.mipams.jumbf.core.controller;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/core/v1")
public class CoreController {

    @Autowired
    CoreParserService parserService;

    @Autowired
    CoreGeneratorService generatorService;

    @GetMapping("/parseMetadata")
    public ResponseEntity<?> parseJumbfMetadataFromPath(@RequestParam String path) {
        try {
            List<JumbfBox> boxList = parserService.parseMetadataFromJumbfFile(path);
            return ResponseEntity.ok().body(boxList.toString());
        } catch (MipamsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generateBox")
    public ResponseEntity<?> generateJumbfBytes(@RequestParam(required = false) String targetFile,
            @RequestBody JsonNode requestBody) {
        try {
            List<JumbfBox> boxList = generatorService.generateBoxFromRequest(requestBody);

            String outputFileName = targetFile == null ? "test.jumbf" : targetFile;

            String result = generatorService.generateJumbfFileFromBox(boxList, outputFileName);
            return ResponseEntity.ok().body(result);
        } catch (MipamsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
