package mipams.jumbf.provenance.services;

import mipams.jumbf.provenance.util.BoxTypeEnum;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.node.ObjectNode;

import mipams.jumbf.core.util.MipamsException;
import mipams.jumbf.core.BoxServiceManager;
import mipams.jumbf.core.entities.XTBox;
import mipams.jumbf.core.util.BadRequestException;
import mipams.jumbf.core.util.CoreUtils;
import mipams.jumbf.core.services.GeneratorInterface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GeneratorService implements GeneratorInterface{

    private static final Logger logger = LoggerFactory.getLogger(GeneratorService.class); 

    @Autowired
    BoxServiceManager boxServiceManager;

    @Override
    public String generateJumbfFileFromRequest(ObjectNode inputNode) throws MipamsException{             
        String result = boxServiceManager.generateServiceBasedOnBoxWithId(BoxTypeEnum.AssertionBox.getTypeId());

        if(result == null){
            return "failed to connect the two packages";
        } else {
            return "I connected one package with another. The box with id: "+Integer.toString(result.serviceIsResponsibleForBoxType())+" is discovered properly.";
        }               
    }
}