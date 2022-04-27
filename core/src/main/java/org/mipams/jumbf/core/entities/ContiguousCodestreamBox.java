package org.mipams.jumbf.core.entities;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class ContiguousCodestreamBox extends SingleFormatBox {

    @Override
    public int getTypeId() {
        return 0x6A703263;
    }

    @Override
    public String getType() {
        return "jp2c";
    }
}