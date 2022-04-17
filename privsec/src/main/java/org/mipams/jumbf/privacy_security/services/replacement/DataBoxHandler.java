package org.mipams.jumbf.privacy_security.services.replacement;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface DataBoxHandler {

        List<BmffBox> discoverDataBoxFromRequest(ObjectNode inputNode) throws MipamsException;

        void writeDataBoxToJumbfFile(List<BmffBox> replacementDataBoxList, FileOutputStream fileOutputStream)
                        throws MipamsException;

        List<BmffBox> parseDataBoxFromJumbfFile(InputStream inputStream, long availableBytesForBox)
                        throws MipamsException;

}
