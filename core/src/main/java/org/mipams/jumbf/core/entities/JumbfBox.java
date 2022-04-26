package org.mipams.jumbf.core.entities;

import java.util.List;

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
    protected @Getter @Setter @ToString.Exclude PaddingBox paddingBox;

    @Override
    public int getTypeId() {
        return 0x6A756D62;
    }

    @Override
    public String getType() {
        return "jumb";
    }

    @Override
    public String getContentTypeUUID() {
        return "6A756D62-0011-0010-8000-00AA00389B71";
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
}