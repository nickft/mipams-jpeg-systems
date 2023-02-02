package org.mipams.privsec.services.boxes.replacement;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.boxes.ContiguousCodestreamBoxService;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileReplacementHandler implements DataBoxHandler {

    @Autowired
    ContiguousCodestreamBoxService contiguousCodestreamBoxService;

    @Override
    public void writeDataBoxToJumbfFile(List<BmffBox> replacementDataBoxList, OutputStream outputStream)
            throws MipamsException {

        ContiguousCodestreamBox contiguousCodestreamBox = (ContiguousCodestreamBox) replacementDataBoxList.get(0);
        contiguousCodestreamBoxService.writeToJumbfFile(contiguousCodestreamBox, outputStream);
    }

    @Override
    public List<BmffBox> parseDataBoxFromJumbfFile(InputStream inputStream, ParseMetadata parseMetadata)
            throws MipamsException {

        ContiguousCodestreamBox contiguousCodestreamBox = contiguousCodestreamBoxService.parseFromJumbfFile(inputStream,
                parseMetadata);
        return List.of(contiguousCodestreamBox);
    }
}
