package org.mipams.jumbf.core.services.content_types;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.services.boxes.ContiguousCodestreamBoxService;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContiguousCodestreamContentType implements ContentTypeService {
    @Autowired
    ContiguousCodestreamBoxService contiguousCodestreamBoxService;

    @Override
    public String getContentTypeUuid() {
        return "6579D6FB-DBA2-446B-B2AC-1B82FEEB89D1";
    }

    @Override
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, ParseMetadata parseMetadata)
            throws MipamsException {
        ContiguousCodestreamBox contiguousCodestreamBox = contiguousCodestreamBoxService.parseFromJumbfFile(input,
                parseMetadata);
        return List.of(contiguousCodestreamBox);
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, OutputStream outputStream)
            throws MipamsException {
        ContiguousCodestreamBox jsonBox = (ContiguousCodestreamBox) inputBox.get(0);
        contiguousCodestreamBoxService.writeToJumbfFile(jsonBox, outputStream);
    }
}
