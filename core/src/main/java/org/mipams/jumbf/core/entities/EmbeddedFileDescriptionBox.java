package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class EmbeddedFileDescriptionBox extends XtBox {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedFileDescriptionBox.class);

    private @Getter @Setter int toggle;

    private @Getter @Setter MediaType mediaType;

    private @Getter @Setter String fileName;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.EmbeddedFileDescriptionBox.getTypeId();
    }

    @Override
    protected long calculatePayloadSize() throws MipamsException {

        long sum = getToggleSize();

        sum += getMediaTypeSize();

        if (fileNameExists())
            sum += getFileNameSize();

        return sum;
    }

    public int getToggleSize() {
        return 1;
    }

    public int getMediaTypeSize() {
        return getMediaTypeWithEscapeCharacter().length();
    }

    public int getFileNameSize() {
        return getFileNameWithEscapeCharacter().length();
    }

    public boolean fileNameExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 0);
    }

    public boolean isContentReferencedExternally() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 1);
    }

    public void markFileAsExternallyReferenced() {
        int value = 1;
        int updatedToggle = CoreUtils.setBitValueAtGivenPosition(toggle, 1, value);
        setToggle(updatedToggle);
    }

    public void markFileAsInternallyReferenced() {
        int value = 0;
        int updatedToggle = CoreUtils.setBitValueAtGivenPosition(toggle, 1, value);
        setToggle(updatedToggle);
    }

    public String getMediaTypeWithEscapeCharacter() {
        return CoreUtils.addEscapeCharacterToText(getMediaType().toString());
    }

    public String getFileNameWithEscapeCharacter() {
        return CoreUtils.addEscapeCharacterToText(getFileName());
    }

    public void setMediaTypeFromString(String type) throws MipamsException {
        try {
            MediaType mediaType = CoreUtils.getMediaTypeFromString(type);
            setMediaType(mediaType);
        } catch (IllegalArgumentException e) {
            throw new MipamsException("Bad Media Type", e);
        } catch (NullPointerException e) {
            throw new MipamsException("Media type not specified", e);
        }
    }

    public String discoverFileName() {
        if (fileNameExists()) {
            return getFileName();
        } else {
            logger.info(getMediaType().toString());
            return CoreUtils.randomStringGenerator() + "." + getMediaType().getSubtype();
        }
    }
}