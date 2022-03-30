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
public class ContiguousCodestreamBox extends XTBox {

    protected @Getter @Setter String pathToCodestream;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.ContiguousCodestreamBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {
        return (long) CoreUtils.getFileSizeFromPath(pathToCodestream);
    }
}