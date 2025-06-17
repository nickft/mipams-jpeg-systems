package org.mipams.privsec.services.boxes.replacement;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.util.MipamsException;

public interface DataBoxHandler {
    void writeDataBoxToJumbfFile(List<BmffBox> replacementDataBoxList, OutputStream outputStream)
            throws MipamsException;

    List<BmffBox> parseDataBoxFromJumbfFile(InputStream inputStream, ParseMetadata parseMetadata)
            throws MipamsException;

}
