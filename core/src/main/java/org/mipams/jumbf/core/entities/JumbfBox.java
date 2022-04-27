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
public class JumbfBox extends BmffBox {

    protected @Getter @Setter DescriptionBox descriptionBox;
    protected @Getter @Setter List<BmffBox> contentBoxList;
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
    protected long calculatePayloadSize() throws MipamsException {
        long sum = getDescriptionBox().getBoxSizeFromBmffHeaders();

        for (BmffBox contentBox : getContentBoxList()) {
            sum += contentBox.getBoxSize();
        }

        sum += (getPaddingBox() != null) ? getPaddingBox().getBoxSize() : 0;
        return sum;
    }

    public long calculateContentBoxListSize(List<BmffBox> contentBoxList) {
        long sum = 0;

        for (BmffBox contentBox : contentBoxList) {
            sum += contentBox.getBoxSize();
        }

        return sum;
    }
}