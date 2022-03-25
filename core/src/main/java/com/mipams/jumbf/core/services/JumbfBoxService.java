package mipams.jumbf.core.services;

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

import mipams.jumbf.core.entities.JumbfBox;
import mipams.jumbf.core.entities.XTBox;
import mipams.jumbf.core.BoxServiceManager;
import mipams.jumbf.core.entities.DescriptionBox;
import mipams.jumbf.core.util.MipamsException;
import mipams.jumbf.core.util.BadRequestException;
import mipams.jumbf.core.util.BoxTypeEnum;
import mipams.jumbf.core.util.CoreUtils;

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

        if(!BoxTypeEnum.JumbfBox.getType().equals(type)){
            throw new BadRequestException("Box type does not match with description type.");
        }

        ObjectNode descriptionNode = (ObjectNode) input.get("description");
        
        XTBoxService descriptionBoxService = boxServiceManager.generateServiceBasedOnBoxWithId(BoxTypeEnum.DescriptionBox.getTypeId());

        jumbfBox.setDescriptionBox((DescriptionBox) descriptionBoxService.discoverXTBox(descriptionNode));

        JsonNode contentNodeList = input.get("contentList");

        Iterator<JsonNode> contentIterator = contentNodeList.elements();
        List<XTBox> contentList = new ArrayList<>();
      
        while (contentIterator.hasNext()){

            UUID contentTypeUuid = jumbfBox.getDescriptionBox().getUuid();

            XTBoxService xtBoxService = boxServiceManager.getServiceBasedOnContentUUID(contentTypeUuid);
            XTBox xtBoxContent = xtBoxService.discoverXTBox((ObjectNode) contentIterator.next());

            contentList.add(xtBoxContent);
        }

        jumbfBox.setContentList(contentList);
    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(JumbfBox jumbfBox, FileOutputStream fileOutputStream) throws MipamsException{

        XTBoxService descriptionBoxService = boxServiceManager.generateServiceBasedOnBoxWithId(BoxTypeEnum.DescriptionBox.getTypeId());
        descriptionBoxService.writeBoxToJumbfFile(jumbfBox.getDescriptionBox(), fileOutputStream);

        for (XTBox contentBox: jumbfBox.getContentList()){

            XTBoxService xtBoxService = boxServiceManager.generateServiceBasedOnBoxWithId(contentBox.getTypeId());
            xtBoxService.writeBoxToJumbfFile(contentBox, fileOutputStream);
        }
    }

    @Override
    protected void populatePayloadFromJumbfFile(JumbfBox jumbfBox, InputStream input) throws MipamsException{
        
        logger.debug("Jumbf box");

        long actualSize = 0;

        XTBoxService descriptionBoxService = boxServiceManager.generateServiceBasedOnBoxWithId(BoxTypeEnum.DescriptionBox.getTypeId());
        jumbfBox.setDescriptionBox((DescriptionBox) descriptionBoxService.parseFromJumbfFile(input));

        actualSize += jumbfBox.getDescriptionBox().getBoxSizeFromXTBoxHeaders();

        List<XTBox> contentList = new ArrayList<>();

        do{
            XTBoxService xtBoxService = boxServiceManager.getServiceBasedOnContentUUID(jumbfBox.getDescriptionBox().getUuid());
            XTBox contentBox = xtBoxService.parseFromJumbfFile(input);

            actualSize += contentBox.getBoxSizeFromXTBoxHeaders();
            
            contentList.add(contentBox);
        } while(actualSize < jumbfBox.getPayloadSizeFromXTBoxHeaders());

        jumbfBox.setContentList(contentList);

        verifyBoxSize(jumbfBox, actualSize);

        logger.debug("Discovered box: " + jumbfBox.toString());
    }

    @Override
    public int serviceIsResponsibleForBoxTypeId(){
        return BoxTypeEnum.JumbfBox.getTypeId();
    }

}