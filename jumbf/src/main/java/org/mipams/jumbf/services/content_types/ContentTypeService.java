package org.mipams.jumbf.services.content_types;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.util.MipamsException;

public interface ContentTypeService {

        public String getContentTypeUuid();

        public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, ParseMetadata parseMetadata)
                        throws MipamsException;

        public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, OutputStream outputStream)
                        throws MipamsException;

}
