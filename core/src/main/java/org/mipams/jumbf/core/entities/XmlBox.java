package org.mipams.jumbf.core.entities;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class XmlBox extends SingleFormatBox {

    @Override
    public int getTypeId() {
        return 0x786D6C20;
    }

    @Override
    public String getType() {
        return "xml";
    }
}