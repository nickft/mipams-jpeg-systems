package org.mipams.jumbf.provenance.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.BoxServiceManager;
import org.mipams.jumbf.core.services.XTBoxService;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.provenance.entities.AssertionBox;
import org.mipams.jumbf.provenance.util.BoxTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssertionBoxService extends XTBoxService<AssertionBox> {

    @Autowired
    BoxServiceManager boxServiceManager;

    @Override
    protected AssertionBox initializeBox() throws MipamsException {
        return new AssertionBox();
    }

    @Override
    protected void populateBox(AssertionBox assertionBox, ObjectNode input) throws MipamsException {

    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(AssertionBox assertionBox, FileOutputStream fileOutputStream)
            throws MipamsException {

    }

    @Override
    protected void populatePayloadFromJumbfFile(AssertionBox assertionBox, InputStream input) throws MipamsException {

    }

    @Override
    public int serviceIsResponsibleForBoxTypeId() {
        return BoxTypeEnum.AssertionBox.getTypeId();
    }

}