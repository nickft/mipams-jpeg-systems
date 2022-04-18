package org.mipams.jumbf.privacy_security.entities;

import org.mipams.jumbf.privacy_security.util.BoxTypeEnum;
import org.mipams.jumbf.core.entities.BmffBox;
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
public class ProtectionDescriptionBox extends BmffBox {

    private @Getter @Setter int methodToggle;

    private @Getter @Setter String encLabel;

    private @Getter @Setter String arLabel;

    private @Getter @Setter byte[] iv;

    @Override
    public int getTypeId() {
        return BoxTypeEnum.ProtectionDescriptionBox.getTypeId();
    }

    @Override
    public long calculatePayloadSize() throws MipamsException {

        long sum = getMethodToggleSize();

        if (isProtectionExternallyReferenced()) {
            sum += getEncLabelSize();
        }

        if (isAes256CbcWithIvProtection()) {
            sum += 16;
        }

        if (accessRulesExist()) {
            sum += getArLabelSize();
        }

        return sum;
    }

    public int getMethodToggleSize() {
        return 1;
    }

    public boolean isProtectionExternallyReferenced() {
        return getMethodToggle() % 16 == 0;
    }

    public boolean isAes256CbcProtection() {
        return getMethodToggle() % 16 == 1;
    }

    public boolean isAes256CbcWithIvProtection() {
        return getMethodToggle() % 16 == 2;
    }

    public boolean accessRulesExist() {
        return getMethodToggle() >= 16;
    }

    public void setProtectionMethodAsExternallyReferenced() {
        int upperPart = getMethodToggle() / 16;

        setMethodToggle(upperPart + MethodType.EXTERNAL.getMethodId());
    }

    public void setAes256CbcProtection() {
        int upperPart = getMethodToggle() / 16;

        setMethodToggle(upperPart + MethodType.AES_256_CBC.getMethodId());
    }

    public void setAes256CbcWithIvProtection() {
        int upperPart = getMethodToggle() / 16;

        setMethodToggle(upperPart + MethodType.AES_256_CBC_WITH_IV.getMethodId());
    }

    public void includeAccessRulesInToggle() {
        int lowerPart = getMethodToggle() % 16;
        setMethodToggle(16 + lowerPart);
    }

    long getEncLabelSize() {
        return getEncLabelWithEscapeCharacter().length();
    }

    public String getEncLabelWithEscapeCharacter() {
        return CoreUtils.addEscapeCharacterToText(getEncLabel().toString());
    }

    long getArLabelSize() {
        return getArLabelWithEscapeCharacter().length();
    }

    public String getArLabelWithEscapeCharacter() {
        return CoreUtils.addEscapeCharacterToText(getArLabel().toString());
    }

    public enum MethodType {

        EXTERNAL("external", 0),
        AES_256_CBC("aes-256-cbc", 1),
        AES_256_CBC_WITH_IV("aes-256-cbc-iv", 2);

        private @Getter @Setter String methodType;
        private @Getter @Setter int methodId;

        MethodType(String methodType, int methodId) {
            setMethodType(methodType);
            setMethodId(methodId);
        }

        public static MethodType getMethodTypeFromString(String type) throws MipamsException {
            for (MethodType val : values()) {
                if (val.getMethodType().equals(type)) {
                    return val;
                }
            }
            throw new MipamsException(getErrorMessage());
        }

        static String getErrorMessage() {
            return String.format("Method is not supported. Supported methods are: %s, %s, %s",
                    EXTERNAL.getMethodType(),
                    AES_256_CBC.getMethodType(), AES_256_CBC_WITH_IV.getMethodType());
        }
    }
}