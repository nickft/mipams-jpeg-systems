package org.mipams.jumbf.privacy_security.services.replacement;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.XtBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface DataBoxHandler {

        List<XtBox> discoverDataBoxFromRequest(ObjectNode inputNode) throws MipamsException;

        void writeDataBoxToJumbfFile(List<XtBox> replacementDataBoxList, FileOutputStream fileOutputStream)
                        throws MipamsException;

        List<XtBox> parseDataBoxFromJumbfFile(InputStream inputStream, long availableBytesForBox)
                        throws MipamsException;

}
