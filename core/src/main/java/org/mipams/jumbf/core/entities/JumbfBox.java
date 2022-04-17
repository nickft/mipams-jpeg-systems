package org.mipams.jumbf.core.entities;

import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class JumbfBox extends BmffBox implements ContentBox {

    protected @Getter @Setter DescriptionBox descriptionBox;
    protected @Getter @Setter ContentBox contentBox;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.JumbfBox.getTypeId();
    }

    @Override
    protected long calculatePayloadSize() throws MipamsException {

        long sum = descriptionBox.getBoxSizeFromBmffHeaders();
        sum += contentBox.getBoxSize();

        return sum;
    }

    @Override
    public List<BmffBox> getBmffBoxes() {
        return contentBox.getBmffBoxes();
    }

    @Override
    public UUID getContentTypeUUID() {
        return BoxTypeEnum.JumbfBox.getContentUuid();
    }

    @Override
    public String toString() {
        return String.format("--> %s Content Type JUMBF box \n", getContentBox().getClass().getSimpleName());
    }

}