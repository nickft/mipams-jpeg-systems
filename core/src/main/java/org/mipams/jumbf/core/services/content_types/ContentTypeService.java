package org.mipams.jumbf.core.services.content_types;

import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.List;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface ContentTypeService {

        public String getContentTypeUuid();

        public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, long availableBytesForBox)
                        throws MipamsException;

        public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, FileOutputStream fileOutputStream)
                        throws MipamsException;

}
