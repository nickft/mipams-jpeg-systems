package org.mipams.jumbf.core.services.content_types;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.util.MipamsException;

public interface ContentTypeService {

        public String getContentTypeUuid();

        public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, ParseMetadata parseMetadata)
                        throws MipamsException;

        public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, OutputStream outputStream)
                        throws MipamsException;

}
