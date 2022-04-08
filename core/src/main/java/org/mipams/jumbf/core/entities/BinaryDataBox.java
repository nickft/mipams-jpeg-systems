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
public class BinaryDataBox extends XTBox {

    private @Getter @Setter String fileUrl;

    private @Getter @Setter boolean isReferencedExternally = false;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.BinaryDataBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

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
