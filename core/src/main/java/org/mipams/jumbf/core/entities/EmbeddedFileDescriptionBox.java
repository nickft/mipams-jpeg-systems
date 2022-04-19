package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.http.MediaType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmbeddedFileDescriptionBox extends BmffBox {

    private @Getter @Setter int toggle;

    private @Getter @Setter MediaType mediaType;

    @EqualsAndHashCode.Exclude
    private @Getter @Setter String fileName;

    @Override
    public int getTypeId() {
        return 0x62666462;
    }

    @Override
    public String getType() {
        return "bfdb";
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

    public void computeAndSetToggleBasedOnFields() {
        int toggle = getToggle();

        if (getFileName() != null) {
            toggle = toggle | 1;
        }

        setToggle(toggle);
    }

    public void markFileAsExternallyReferenced() {
        int value = 1;
        int updatedToggle = CoreUtils.setBitValueAtGivenPosition(getToggle(), 1, value);
        setToggle(updatedToggle);
    }

    public void markFileAsInternallyReferenced() {
        int value = 0;
        int updatedToggle = CoreUtils.setBitValueAtGivenPosition(getToggle(), 1, value);
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
        }
    }

    public String discoverFileName() {
        if (fileNameExists()) {
            return getFileName();
        } else {
            return getRandomFileName();

        }
    }

    public String getRandomFileName() {
        return CoreUtils.randomStringGenerator() + "." + getMediaType().getSubtype();
    }
}