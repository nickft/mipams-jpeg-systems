package org.mipams.jumbf.privacy_security.integration;

import java.util.UUID;

import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.util.MipamsException;

public class MockJumbfBox {

    public static JumbfBox generateJumbfBoxWithContent(ContentBox contentBox, UUID contentTypeUuid)
            throws MipamsException {

        DescriptionBox dBox = new DescriptionBox();
        dBox.setUuid(contentTypeUuid);
        dBox.setLabel("This is a test");
        dBox.computeAndSetToggleBasedOnFields();
        dBox.updateBmffHeadersBasedOnBox();

        return generateJumbfBox(dBox, contentBox);
    }

    public static JumbfBox generateJumbfBox(DescriptionBox descriptionBox, ContentBox contentBox)
            throws MipamsException {

        JumbfBox jumbfBox = new JumbfBox();
        jumbfBox.setDescriptionBox(descriptionBox);
        jumbfBox.setContentBox(contentBox);
        jumbfBox.updateBmffHeadersBasedOnBox();

        return jumbfBox;
    }
}
