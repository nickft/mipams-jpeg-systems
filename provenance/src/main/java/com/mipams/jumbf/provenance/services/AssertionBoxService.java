package org.mipams.jumbf.provenance.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.XTBox;
import org.mipams.jumbf.core.BoxServiceManager;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.services.XTBoxService;

import org.mipams.jumbf.provenance.entities.AssertionBox;
import org.mipams.jumbf.provenance.util.BoxTypeEnum;

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
    public int serviceIsResponsibleForBoxTypeId(){
        return BoxTypeEnum.AssertionBox.getTypeId();
    }

}