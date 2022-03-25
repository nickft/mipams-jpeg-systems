package mipams.jumbf.provenance.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.UUID;

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
public class AssertionBoxService extends XTBoxService<AssertionBox>{

    private static final Logger logger = LoggerFactory.getLogger(AssertionBoxService.class); 

    @Autowired
    BoxServiceManager boxServiceManager;

    @Override
    protected AssertionBox initializeBox() throws MipamsException{
        return new AssertionBox();
    }

    @Override
    protected void populateBox(AssertionBox assertionBox, ObjectNode input) throws MipamsException{

    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(AssertionBox assertionBox, FileOutputStream fileOutputStream) throws MipamsException{

    }

    @Override
    protected void populatePayloadFromJumbfFile(AssertionBox assertionBox, InputStream input) throws MipamsException{
        
    }

    @Override
    public BoxTypeEnum serviceIsResponsibleForBoxType(){
        return BoxTypeEnum.AssertionBox;
    }

}