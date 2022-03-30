package org.mipams.jumbf.provenance.services;

import java.util.List;

import org.mipams.jumbf.core.BoxServiceManager;
import org.mipams.jumbf.core.entities.XTBox;
import org.mipams.jumbf.core.services.ParserInterface;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParserService implements ParserInterface {

    @Autowired
    BoxServiceManager boxServiceManager;

    @Override
    public List<XTBox> parseMetadataFromJumbfFile(String path) throws MipamsException {
        return null;
    }
}