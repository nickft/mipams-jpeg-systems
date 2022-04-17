package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public abstract class SingleFormatBox extends BmffBox {
    protected @Getter @Setter String fileUrl;

    @Override
    protected long calculatePayloadSize() throws MipamsException {
        return CoreUtils.getFileSizeFromPath(fileUrl);
    }
}
