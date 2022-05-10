package org.mipams.jumbf.privacy_security.services.boxes.replacement;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.services.boxes.ContiguousCodestreamBoxService;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoiReplacementHandler implements DataBoxHandler {

    @Autowired
    ContiguousCodestreamBoxService contiguousCodestreamBoxService;

    @Override
    public void writeDataBoxToJumbfFile(List<BmffBox> replacementDataBoxList, OutputStream outputStream)
            throws MipamsException {

        ContiguousCodestreamBox contiguousCodestreamBox = (ContiguousCodestreamBox) replacementDataBoxList.get(0);
        contiguousCodestreamBoxService.writeToJumbfFile(contiguousCodestreamBox, outputStream);
    }

    @Override
    public List<BmffBox> parseDataBoxFromJumbfFile(InputStream inputStream, long availableBytesForBox)
            throws MipamsException {

        ContiguousCodestreamBox contiguousCodestreamBox = contiguousCodestreamBoxService.parseFromJumbfFile(inputStream,
                availableBytesForBox);
        return List.of(contiguousCodestreamBox);
    }

}
