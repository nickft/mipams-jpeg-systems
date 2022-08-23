package org.mipams.jumbf.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.mipams.jumbf.core.services.CoreParserService;
import org.mipams.jumbf.core.services.JpegCodestreamParser;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.services.CoreGeneratorService;
import org.mipams.jumbf.core.util.JpegCodestreamException;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.demo.entities.UploadRequest;
import org.mipams.jumbf.demo.services.DemoRequestParser;
import org.mipams.jumbf.demo.services.FileUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    CoreParserService parserService;

    @Autowired
    JpegCodestreamParser jpegCodestreamParser;

    @Autowired
    CoreGeneratorService generatorService;

    @Autowired
    DemoRequestParser demoRequestParser;

    @Autowired
    FileUploader fileUploader;

    @RequestMapping(path = "/uploadJumbfFile", method = RequestMethod.POST, consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadJumbf(@ModelAttribute UploadRequest request) throws MipamsException {
        String fileName = fileUploader.saveFileToDiskAndGetFileName(request, true);
        String fileUrl = fileUploader.getFileUrl(fileName);

        try {

            List<JumbfBox> boxList;

            logger.info(request.getFile().getOriginalFilename());

            if (request.getFile().getOriginalFilename().endsWith(".jumbf")) {
                boxList = parserService.parseMetadataFromFile(fileUrl);
            } else {
                boxList = jpegCodestreamParser.parseMetadataFromFile(fileUrl);
            }

            return ResponseEntity.ok().body(prepareResponse(boxList));
        } catch (JpegCodestreamException e) {
            return ResponseEntity.badRequest()
                    .body("JPEG XT Codestream parsing error.");
        } catch (MipamsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(path = "/uploadMetadataFile", method = RequestMethod.POST, consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMetadataFile(@ModelAttribute UploadRequest request) throws MipamsException {
        fileUploader.saveFileToDiskAndGetFileName(request, false);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/parseMetadata")
    public ResponseEntity<?> parseJumbfMetadataFromPath(@RequestParam String fileName) {
        String fileUrl = fileUploader.getFileUrl(fileName);
        try {
            List<JumbfBox> boxList = parserService.parseMetadataFromFile(fileUrl);
            return ResponseEntity.ok().body(prepareResponse(boxList));
        } catch (MipamsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String prepareResponse(List<JumbfBox> boxList) throws MipamsException {

        ObjectMapper mapper = new ObjectMapper();
        String result;
        try {
            result = mapper.writeValueAsString(boxList);
        } catch (JsonProcessingException e) {
            throw new MipamsException(e.getMessage());
        }

        return result;
    }

    @GetMapping(path = "/download")
    public ResponseEntity<?> downloadFile(@RequestParam(required = false) String targetFile) {
        String outputFileName = targetFile == null ? "test.jumbf" : targetFile;

        try {
            return fileUploader.createOctetResponse(outputFileName);
        } catch (MipamsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/generateBox", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateJumbfBytes(@RequestParam(required = false) String targetFile,
            @RequestBody JsonNode requestBody) {
        String outputFileName = targetFile == null ? "test.jumbf" : targetFile;
        String fileUrl = fileUploader.getFileUrl(outputFileName);

        try {
            List<JumbfBox> boxList = demoRequestParser.generateBoxFromRequest(requestBody);

            generatorService.generateJumbfMetadataToFile(boxList, fileUrl);

            String result = generateResultMessage(boxList, fileUrl);
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
