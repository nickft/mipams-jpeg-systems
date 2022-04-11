package org.mipams.jumbf.core.entities;

import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.util.BoxTypeEnum;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class ContiguousCodestreamBox extends SingleFormatBox implements ContentBox {

    @Override
    public int getTypeId() {
        return BoxTypeEnum.ContiguousCodestreamBox.getTypeId();
    }

    @Override
    public List<XtBox> getXtBoxes() {
        return List.of(this);
    }

    @Override
    public UUID getContentTypeUUID() {
        return BoxTypeEnum.ContiguousCodestreamBox.getContentUuid();
    }
}