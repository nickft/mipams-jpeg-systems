package org.mipams.jumbf.core.entities;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class CborBox extends SingleFormatBox implements ContentBox {

    @Override
    public int getTypeId() {
        return 0x63626F72;
    }

    @Override
    public String getType() {
        return "cbor";
    }

    @Override
    public String getContentTypeUUID() {
        return "63626F72-0011-0010-8000-00AA00389B71";
    }

    @Override
    public List<BmffBox> getBmffBoxes() {
        return List.of(this);
    }
}