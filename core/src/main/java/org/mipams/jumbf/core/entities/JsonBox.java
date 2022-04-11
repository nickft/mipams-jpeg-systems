package org.mipams.jumbf.core.entities;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.BoxTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class JsonBox extends SingleFormatBox implements ContentBox {

    private @Getter @Setter ObjectNode jsonContent;

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