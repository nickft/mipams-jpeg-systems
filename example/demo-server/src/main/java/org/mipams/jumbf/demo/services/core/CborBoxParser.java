package org.mipams.jumbf.demo.services.core;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.CborBox;
import org.mipams.jumbf.core.services.content_types.CborContentType;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.services.ContentTypeParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CborBoxParser extends MemoryBoxParser<CborBox> implements ContentTypeParser {

    @Autowired
    CborContentType cborContentType;

    @Override
    protected CborBox initializeBox() {
        return new CborBox();
    }

    @Override
    public String getContentTypeUuid() {
        return cborContentType.getContentTypeUuid();
    }

    @Override
    public List<BmffBox> discoverContentBoxesFromRequest(ObjectNode input) throws MipamsException {
        return List.of(discoverBoxFromRequest(input));
    }
}
