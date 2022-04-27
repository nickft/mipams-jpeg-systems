package org.mipams.jumbf.core.integration;

import java.util.List;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.util.MipamsException;

public class MockJumbfBoxCreation {

    public static JumbfBox generateJumbfBoxWithContent(List<BmffBox> contentBoxes, String contentUuidAsString)
            throws MipamsException {

        return generateJumbfBoxWithContent(contentBoxes, contentUuidAsString, 0);
    }

    public static JumbfBox generateJumbfBoxWithContent(List<BmffBox> contentBoxes, String contentUuidAsString,
            long paddingSize)
            throws MipamsException {

        DescriptionBox dBox = new DescriptionBox();

        dBox.setUuid(contentUuidAsString);
        dBox.setLabel("This is a test");
        dBox.computeAndSetToggleBasedOnFields();
        dBox.updateBmffHeadersBasedOnBox();

        return generateJumbfBox(dBox, contentBoxes, paddingSize);
    }

    public static JumbfBox generateJumbfBox(DescriptionBox descriptionBox, List<BmffBox> contentBoxes)
            throws MipamsException {
        return generateJumbfBox(descriptionBox, contentBoxes, 0);
    }

    public static JumbfBox generateJumbfBox(DescriptionBox descriptionBox, List<BmffBox> contentBoxes,
            long paddingSize)
            throws MipamsException {

        JumbfBox jumbfBox = new JumbfBox();

        jumbfBox.setDescriptionBox(descriptionBox);
        jumbfBox.setContentBoxList(contentBoxes);

        if (paddingSize > 0) {
            PaddingBox paddingBox = null;

            paddingBox = new PaddingBox();
            paddingBox.setPaddingSize(paddingSize);
            paddingBox.updateBmffHeadersBasedOnBox();

            jumbfBox.setPaddingBox(paddingBox);
        }

        jumbfBox.updateBmffHeadersBasedOnBox();

        return jumbfBox;
    }
}
