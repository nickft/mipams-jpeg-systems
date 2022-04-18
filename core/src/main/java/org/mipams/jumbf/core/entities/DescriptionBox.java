package org.mipams.jumbf.core.entities;

import java.util.UUID;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class DescriptionBox extends BmffBox {

    protected @Getter @Setter UUID uuid;

    protected @Getter @Setter int toggle;

    protected @Getter @Setter String label;

    protected @Getter @Setter Integer id;

    protected @Getter @Setter byte[] sha256Hash;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.DescriptionBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {
        long sum = getUuidSize();

        sum += getToggleSize();

        if (labelExists()) {
            sum += getLabelSize();
        }

        if (idExists()) {
            sum += getIdSize();
        }

        if (sha256HashExists()) {
            sum += getSignatureSize();
        }

        return sum;
    }

    int getUuidSize() {
        return CoreUtils.UUID_BYTE_SIZE;
    }

    int getToggleSize() {
        return 1;
    }

    int getIdSize() {
        return CoreUtils.INT_BYTE_SIZE;
    }

    int getSignatureSize() {
        return 32;
    }

    long getLabelSize() {
        return CoreUtils.addEscapeCharacterToText(getLabel()).length();
    }

    public void computeAndSetToggleBasedOnFields() {

        int toggle = 0;

        if (isRequestable()) {
            toggle = 3;
        }

        if (labelExists()) {
            toggle = toggle | 2;
        }

        if (idExists()) {
            toggle = toggle | 4;
        }

        if (sha256HashExists()) {
            toggle = toggle | 8;
        }

        setToggle(toggle);
    }

    public boolean isRequestable() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 0);
    }

    public boolean labelExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 1);
    }

    public boolean idExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 2);
    }

    public boolean sha256HashExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 3);
    }

    public String getLabelWithEscapeCharacter() {
        return CoreUtils.addEscapeCharacterToText(getLabel());
    }
}