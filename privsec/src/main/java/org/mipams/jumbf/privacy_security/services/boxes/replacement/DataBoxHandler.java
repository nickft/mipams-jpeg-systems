package org.mipams.jumbf.privacy_security.services.boxes.replacement;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface DataBoxHandler {
        void writeDataBoxToJumbfFile(List<BmffBox> replacementDataBoxList, FileOutputStream fileOutputStream)
                        throws MipamsException;

        List<BmffBox> parseDataBoxFromJumbfFile(InputStream inputStream, long availableBytesForBox)
                        throws MipamsException;

}
