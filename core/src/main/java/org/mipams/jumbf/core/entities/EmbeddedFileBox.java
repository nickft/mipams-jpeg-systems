package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class EmbeddedFileBox extends XTBox {

    private @Getter @Setter EmbeddedFileDescriptionBox descriptionBox;

    private @Getter @Setter String fileUrl;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.EmbeddedFileBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = getDescriptionBox().getBoxSizeFromXTBoxHeaders();

        if (getDescriptionBox().isContentReferencedExternally()) {
            sum += getUrlSize();
        } else {
            sum += CoreUtils.getFileSizeFromPath(getFileUrl());
        }

        return sum;
    }

    private long getUrlSize() {
        return CoreUtils.addEscapeCharacterToText(getFileUrl()).length();
    }
}