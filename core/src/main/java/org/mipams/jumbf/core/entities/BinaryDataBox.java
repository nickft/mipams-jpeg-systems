package org.mipams.jumbf.core.entities;

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
public class BinaryDataBox extends SingleFormatBox {

    private @Getter @Setter boolean referencedExternally = false;

    @Override
    public int getTypeId() {
        return 0x62696462;
    }

    @Override
    public String getType() {
        return "bidb";
    }

    @Override
    protected long calculatePayloadSize() throws MipamsException {

        long sum;

        if (isReferencedExternally()) {
            sum = getUrlSize();
        } else {
            sum = getFileSize();
        }

        return sum;
    }

    private long getFileSize() throws MipamsException {
        return CoreUtils.getFileSizeFromPath(getFileUrl());
    }

    private long getUrlSize() {
        return CoreUtils.addEscapeCharacterToText(getFileUrl()).length();
    }
}
