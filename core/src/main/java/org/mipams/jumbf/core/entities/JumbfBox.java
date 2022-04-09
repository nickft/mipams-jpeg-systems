package org.mipams.jumbf.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString(callSuper = true)
public class JumbfBox extends XtBox implements ContentBox {

    protected @Getter @Setter DescriptionBox descriptionBox;
    protected @Getter @Setter List<ContentBox> contentList;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.JumbfBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = descriptionBox.getBoxSizeFromXTBoxHeaders();

        for (ContentBox content : getContentList()) {
            sum += content.calculateSizeFromBox();
        }

        return sum;
    }

    @Override
    public List<XtBox> getXtBoxes() {

        List<XtBox> xtBoxes = new ArrayList<>();

        for (ContentBox contentBox : contentList) {
            xtBoxes.addAll(contentBox.getXtBoxes());
        }

        return xtBoxes;
    }

    @Override
    public UUID getContentTypeUUID() {
        return BoxTypeEnum.JumbfBox.getContentUuid();
    }

}