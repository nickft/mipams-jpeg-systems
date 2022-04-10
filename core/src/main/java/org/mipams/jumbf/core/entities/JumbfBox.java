package org.mipams.jumbf.core.entities;

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
    protected @Getter @Setter ContentBox contentBox;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.JumbfBox.getTypeId();
    }

    @Override
    protected long calculatePayloadSize() throws MipamsException {

        long sum = descriptionBox.getBoxSizeFromXTBoxHeaders();
        sum += contentBox.getBoxSize();

        return sum;
    }

    @Override
    public List<XtBox> getXtBoxes() {
        return contentBox.getXtBoxes();
    }

    @Override
    public UUID getContentTypeUUID() {
        return BoxTypeEnum.JumbfBox.getContentUuid();
    }

}