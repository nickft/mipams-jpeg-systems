package org.mipams.jumbf.core.entities;

import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class JumbfBox extends BmffBox implements ContentBox {

    protected @Getter @Setter DescriptionBox descriptionBox;
    protected @Getter @Setter ContentBox contentBox;
    protected @Getter @Setter PaddingBox paddingBox;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.JumbfBox.getTypeId();
    }

    @Override
    protected long calculatePayloadSize() throws MipamsException {
        long sum = getDescriptionBox().getBoxSizeFromBmffHeaders();
        sum += getContentBox().getBoxSize();
        sum += (getPaddingBox() != null) ? getPaddingBox().getBoxSize() : 0;
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
}