package org.mipams.privsec.integration;

import java.util.List;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.DescriptionBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.util.MipamsException;

public class MockJumbfBox {

    public static JumbfBox generateJumbfBoxWithContent(String uuid, List<BmffBox> contentBoxes)
            throws MipamsException {

        DescriptionBox dBox = new DescriptionBox();
        dBox.setUuid(uuid);
        dBox.setLabel("This is a test");
        dBox.computeAndSetToggleBasedOnFields();
        dBox.updateBmffHeadersBasedOnBox();

        return generateJumbfBox(dBox, contentBoxes);
    }

    public static JumbfBox generateJumbfBox(DescriptionBox descriptionBox, List<BmffBox> contentBoxes)
            throws MipamsException {

        JumbfBox jumbfBox = new JumbfBox();
        jumbfBox.setDescriptionBox(descriptionBox);
        jumbfBox.setContentBoxList(contentBoxes);
        jumbfBox.updateBmffHeadersBasedOnBox();

        return jumbfBox;
    }
}
