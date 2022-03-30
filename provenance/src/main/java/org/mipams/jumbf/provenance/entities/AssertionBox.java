package org.mipams.jumbf.provenance.entities;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.provenance.util.BoxTypeEnum;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class AssertionBox extends JumbfBox {

    @Override
    public int getTypeId() {
        return BoxTypeEnum.AssertionBox.getTypeId();
    }

}