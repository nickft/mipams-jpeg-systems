package org.mipams.jumbf.core.integration;

import java.util.UUID;

import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.util.MipamsException;

public class MockJumbfBoxCreation {

    public static JumbfBox generateJumbfBoxWithContent(ContentBox contentBox, UUID contentTypeUuid)
            throws MipamsException {

        return generateJumbfBoxWithContent(contentBox, contentTypeUuid, 0);
    }

    public static JumbfBox generateJumbfBoxWithContent(ContentBox contentBox, UUID contentTypeUuid, long paddingSize)
            throws MipamsException {

        DescriptionBox dBox = new DescriptionBox();

        dBox.setUuid(contentTypeUuid);
        dBox.setLabel("This is a test");
        dBox.setToggle(3);
        dBox.updateBmffHeadersBasedOnBox();

        PaddingBox paddingBox = null;

        JumbfBox jumbfBox = new JumbfBox();

        jumbfBox.setDescriptionBox(dBox);
        jumbfBox.setContentBox(contentBox);

        if (paddingSize > 0) {
            paddingBox = new PaddingBox();
            paddingBox.setPaddingSize(paddingSize);
            paddingBox.updateBmffHeadersBasedOnBox();

            jumbfBox.setPaddingBox(paddingBox);
        }

        jumbfBox.updateBmffHeadersBasedOnBox();

        return jumbfBox;
    }
}
