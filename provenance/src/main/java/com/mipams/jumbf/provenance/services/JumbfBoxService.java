package mipams.jumbf.provenance.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.mipams.jumbf.core.entities.JumbfBox;
import com.mipams.jumbf.core.entities.XTBox;
import com.mipams.jumbf.core.BoxServiceManager;
import com.mipams.jumbf.core.entities.DescriptionBox;
import com.mipams.jumbf.core.util.MipamsException;
import com.mipams.jumbf.core.util.BadRequestException;
import com.mipams.jumbf.core.util.BoxTypeEnum;
import com.mipams.jumbf.core.util.CoreUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JumbfBoxService extends XTBoxService<JumbfBox>{

    private static final Logger logger = LoggerFactory.getLogger(JumbfBoxService.class); 

    @Autowired
    BoxServiceManager boxServiceManager;

    @Override
    protected JumbfBox initializeBox() throws MipamsException{
        return new JumbfBox();
    }

    @Override
    protected void populateBox(JumbfBox jumbfBox, ObjectNode input) throws MipamsException{

        String type = input.get("type").asText();

        if(!jumbfBox.getBoxType().equals(type)){
            throw new BadRequestException("Box type does not match with description type.");
        }

        ObjectNode descriptionNode = (ObjectNode) input.get("description");
        
        XTBoxService descriptionBoxService = boxServiceManager.generateServiceBasedOnBoxType(BoxTypeEnum.DescriptionBox);

        jumbfBox.setDescriptionBox((DescriptionBox) descriptionBoxService.discoverXTBox(descriptionNode));

        JsonNode contentNodeList = input.get("contentList");

        Iterator<JsonNode> contentIterator = contentNodeList.elements();
        List<XTBox> contentList = new ArrayList<>();
      
        while (contentIterator.hasNext()){

            UUID contentTypeUuid = jumbfBox.getDescriptionBox().getUuid();

            XTBoxService xtBoxService = boxServiceManager.getServiceBasedOnContentUUID(contentTypeUuid);
            XTBox xtBoxContent = xtBoxService.discoverXTBox((ObjectNode) contentIterator.next());

            jumbfBox.addContentBoxToList(xtBoxContent);
        }
    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(JumbfBox jumbfBox, FileOutputStream fileOutputStream) throws MipamsException{

        XTBoxService descriptionBoxService = boxServiceManager.generateServiceBasedOnBoxType(BoxTypeEnum.DescriptionBox);
        descriptionBoxService.writeBoxToJumbfFile(jumbfBox.getDescriptionBox(), fileOutputStream);

        for (XTBox contentBox: jumbfBox.getContentList()){

            XTBoxService xtBoxService = boxServiceManager.generateServiceBasedOnString(contentBox.getBoxType());
            xtBoxService.writeBoxToJumbfFile(contentBox, fileOutputStream);
        }
    }

    @Override
    protected void populatePayloadFromJumbfFile(JumbfBox jumbfBox, InputStream input) throws MipamsException{
        
        logger.debug("Jumbf box");

        long actualSize = 0;

        XTBoxService descriptionBoxService = boxServiceManager.generateServiceBasedOnBoxType(BoxTypeEnum.DescriptionBox);
        jumbfBox.setDescriptionBox((DescriptionBox) descriptionBoxService.parseFromJumbfFile(input));

        actualSize += jumbfBox.getDescriptionBox().getBoxSizeFromXTBoxHeaders();
        logger.info(Long.toString(actualSize));

        do{
            XTBoxService xtBoxService = boxServiceManager.getServiceBasedOnContentUUID(jumbfBox.getDescriptionBox().getUuid());
            XTBox contentBox = xtBoxService.parseFromJumbfFile(input);

            actualSize += contentBox.getBoxSizeFromXTBoxHeaders();
            
            jumbfBox.addContentBoxToList(contentBox);

        } while(actualSize < jumbfBox.getPayloadSizeFromXTBoxHeaders());

        verifyBoxSize(jumbfBox, actualSize);

        logger.debug("Discovered box: " + jumbfBox.toString());
    }

    @Override
    public BoxTypeEnum serviceIsResponsibleForBoxType(){
        return BoxTypeEnum.JumbfBox;
    }

}