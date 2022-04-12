package org.mipams.jumbf.core.entities;

import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.util.BoxTypeEnum;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class JsonBox extends SingleFormatBox implements ContentBox {

    @Override
    public int getTypeId() {
        return BoxTypeEnum.JsonBox.getTypeId();
    }

    @Override
    public List<XtBox> getXtBoxes() {
        return List.of(this);
    }

    @Override
    public UUID getContentTypeUUID() {
        return BoxTypeEnum.JsonBox.getContentUuid();
    }

}