package org.mipams.jumbf.core.integration;

import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.util.MipamsException;

public class MockJumbfBoxCreation {

    public static JumbfBox generateJumbfBoxWithContent(ContentBox contentBox)
            throws MipamsException {

        return generateJumbfBoxWithContent(contentBox, 0);
    }

    public static JumbfBox generateJumbfBoxWithContent(ContentBox contentBox, long paddingSize)
            throws MipamsException {

        DescriptionBox dBox = new DescriptionBox();

        dBox.setUuid(contentBox.getContentTypeUUID());
        dBox.setLabel("This is a test");
        dBox.computeAndSetToggleBasedOnFields();
        dBox.updateBmffHeadersBasedOnBox();

        return generateJumbfBox(dBox, contentBox, paddingSize);
    }

    public static JumbfBox generateJumbfBox(DescriptionBox descriptionBox, ContentBox contentBox)
            throws MipamsException {
        return generateJumbfBox(descriptionBox, contentBox, 0);
    }

    public static JumbfBox generateJumbfBox(DescriptionBox descriptionBox, ContentBox contentBox,
            long paddingSize)
            throws MipamsException {

        JumbfBox jumbfBox = new JumbfBox();

        jumbfBox.setDescriptionBox(descriptionBox);
        jumbfBox.setContentBox(contentBox);

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
