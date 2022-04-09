package org.mipams.jumbf.privacy_security.util;

import java.util.UUID;

import org.mipams.jumbf.core.entities.ServiceMetadata;

import lombok.Getter;
import lombok.Setter;

public enum BoxTypeEnum {

    ProtectionBox("prtc", 0x74B11BBF, "74B11BBF-F21D-4EEA-98C1-0BEBF23AEFD3"),
    ProtectionDescriptionBox("pspd", 0x70737064, "70737064-0011-0010-8000-00AA00389B71"),
    ReplacementBox("rplc", 0xDC28B95F, "DC28B95F-B68A-498E-8064-0FCA845D6B0E"),
    ReplacementDescriptionBox("psrd", 0x70737264, "70737264-0011-0010-8000-00AA00389B71");

    private @Getter @Setter String type;
    private @Getter @Setter int typeId;
    private @Getter @Setter UUID contentUuid;

    BoxTypeEnum(String type, int typeId, String uuidAsString) {
        setTypeId(typeId);
        setType(type);
        if (uuidAsString != null)
            setContentUuid(UUID.fromString(uuidAsString));
    }

    public static String getBoxTypeAsStringFromId(int boxId) {
        BoxTypeEnum result = getBoxTypeFromIdOrNull(boxId);

        return (result != null) ? result.getType() : "";
    }

    public static BoxTypeEnum getBoxTypeFromIdOrNull(int boxId) {
        BoxTypeEnum resultType = null;

        for (BoxTypeEnum boxType : values()) {
            if (boxType.getTypeId() == boxId) {
                resultType = boxType;
                break;
            }
        }

        return resultType;
    }

    public static BoxTypeEnum getBoxTypeFromContentUuidOrNull(UUID uuid) {
        BoxTypeEnum resultType = null;

        for (BoxTypeEnum boxType : values()) {
            if (boxType.isContentBox()) {
                if (boxType.getContentUuid().equals(uuid)) {
                    resultType = boxType;
                    break;
                }
            }
        }

        return resultType;
    }

    public boolean isContentBox() {
        return getContentUuid() != null;
    }

    public static BoxTypeEnum getBoxTypeFromString(String type) {
        BoxTypeEnum resultType = null;

        for (BoxTypeEnum boxType : values()) {
            if (boxType.getType().equals(type)) {
                resultType = boxType;
                break;
            }
        }

        return resultType;
    }

    public ServiceMetadata getServiceMetadata() {
        return new ServiceMetadata(getTypeId(), getType(), getContentUuid());
    }
}