package org.mipams.jumbf.core.entities;

import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.util.BoxTypeEnum;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class XmlBox extends SingleFormatBox implements ContentBox {

    @Override
    public int getTypeId() {
        return BoxTypeEnum.XmlBox.getTypeId();
    }

    @Override
    public List<BmffBox> getBmffBoxes() {
        return List.of(this);
    }

    @Override
    public UUID getContentTypeUUID() {
        return BoxTypeEnum.XmlBox.getContentUuid();
    }
}