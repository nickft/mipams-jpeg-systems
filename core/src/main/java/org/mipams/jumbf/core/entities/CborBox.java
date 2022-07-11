package org.mipams.jumbf.core.entities;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class CborBox extends MemoryBox {

    @Override
    public int getTypeId() {
        return 0x63626F72;
    }

    @Override
    public String getType() {
        return "cbor";
    }
}