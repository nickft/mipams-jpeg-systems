package org.mipams.jumbf.demo.services.core;

import org.mipams.jumbf.core.entities.BinaryDataBox;

import org.springframework.stereotype.Service;

@Service
public class BinaryDataBoxParser extends SingleFormatParser<BinaryDataBox> {

    @Override
    protected BinaryDataBox initializeBox() {
        return new BinaryDataBox();
    }
}
