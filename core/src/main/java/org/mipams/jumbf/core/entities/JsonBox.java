package org.mipams.jumbf.core.entities;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class JsonBox extends MemoryBox {

    @Override
    public int getTypeId() {
        return 0x6A736F6E;
    }

    @Override
    public String getType() {
        return "json";
    }
}