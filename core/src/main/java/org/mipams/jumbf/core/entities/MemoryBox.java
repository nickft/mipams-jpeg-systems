package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public abstract class MemoryBox extends BmffBox {
    protected @Getter @Setter byte[] content;

    @Override
    protected long calculatePayloadSize() throws MipamsException {
        return content != null ? content.length : 0;
    }
}
