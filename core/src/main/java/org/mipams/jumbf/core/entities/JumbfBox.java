package org.mipams.jumbf.core.entities;

import java.util.List;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class JumbfBox extends XTBox {

    protected @Getter @Setter DescriptionBox descriptionBox;
    protected @Getter @Setter List<XTBox> contentList;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.JumbfBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = descriptionBox.getBoxSizeFromXTBoxHeaders();

        for (XTBox content : getContentList()) {
            sum += content.getBoxSizeFromXTBoxHeaders();
        }

        return sum;
    }
}