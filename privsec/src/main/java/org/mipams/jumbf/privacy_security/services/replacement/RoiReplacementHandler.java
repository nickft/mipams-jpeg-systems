package org.mipams.jumbf.privacy_security.services.replacement;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.entities.XtBox;
import org.mipams.jumbf.core.services.ContiguousCodestreamBoxService;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoiReplacementHandler implements DataBoxHandler {

    @Autowired
    ContiguousCodestreamBoxService contiguousCodestreamBoxService;

    @Override
    public List<XtBox> discoverDataBoxFromRequest(ObjectNode inputNode) throws MipamsException {

        ObjectNode contentNode = (ObjectNode) inputNode.get("content");

        ContiguousCodestreamBox contiguousCodestreamBox = contiguousCodestreamBoxService
                .discoverBoxFromRequest(contentNode);

        return List.of(contiguousCodestreamBox);
    }

    @Override
    public void writeDataBoxToJumbfFile(List<XtBox> replacementDataBoxList, FileOutputStream fileOutputStream)
            throws MipamsException {

        ContiguousCodestreamBox contiguousCodestreamBox = (ContiguousCodestreamBox) replacementDataBoxList.get(0);
        contiguousCodestreamBoxService.writeToJumbfFile(contiguousCodestreamBox, fileOutputStream);
    }

    @Override
    public List<XtBox> parseDataBoxFromJumbfFile(InputStream inputStream, long availableBytesForBox)
            throws MipamsException {

        ContiguousCodestreamBox contiguousCodestreamBox = contiguousCodestreamBoxService.parseFromJumbfFile(inputStream,
                availableBytesForBox);
        return List.of(contiguousCodestreamBox);
    }

}
