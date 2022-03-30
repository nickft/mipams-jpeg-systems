package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.BoxTypeEnum;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class XmlBox extends ContiguousCodestreamBox {
    @Override
    public int getTypeId() {
        return BoxTypeEnum.XmlBox.getTypeId();
    }
}