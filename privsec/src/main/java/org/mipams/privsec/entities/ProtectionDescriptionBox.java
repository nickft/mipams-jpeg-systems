package org.mipams.privsec.entities;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

public class ProtectionDescriptionBox extends BmffBox {

    private int methodToggle;

    private String encLabel;

    private String arLabel;

    private byte[] iv;

    @Override
    public int getTypeId() {
        return 0x70737064;
    }

    @Override
    public String getType() {
        return "pspd";
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
        return CoreUtils.addEscapeCharacterToText(getEncLabel()).length();
    }

    long getArLabelSize() {
        return CoreUtils.addEscapeCharacterToText(getArLabel()).length();
    }

    @Override
    public String toString() {

        final int methodToggle = getMethodToggle();
        final String encLabel = this.encLabel != null ? getEncLabel().toString() : "null";
        final String arLabel = this.arLabel != null ? getArLabel().toString() : "null";
        final String ivAsString = this.iv != null ? getIv().toString() : "null";

        StringBuilder builder = new StringBuilder("RoiParamHandler=(");

        builder.append("methodToggle=").append(methodToggle).append(", ").append("encLabel=").append(encLabel)
                .append(", ").append("arLabel=").append(arLabel).append(", ").append("iv=").append(ivAsString)
                .append(")");

        return builder.toString();
    }

    public int getMethodToggle() {
        return this.methodToggle;
    }

    public void setMethodToggle(int methodToggle) {
        this.methodToggle = methodToggle;
    }

    public String getEncLabel() {
        return this.encLabel;
    }

    public void setEncLabel(String encLabel) {
        this.encLabel = encLabel;
    }

    public String getArLabel() {
        return this.arLabel;
    }

    public void setArLabel(String arLabel) {
        this.arLabel = arLabel;
    }

    public byte[] getIv() {
        return this.iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public enum MethodType {

        EXTERNAL("external", 0),
        AES_256_CBC("aes-256-cbc", 1),
        AES_256_CBC_WITH_IV("aes-256-cbc-iv", 2);

        private String methodType;
        private int methodId;

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

        public String getMethodType() {
            return this.methodType;
        }

        public void setMethodType(String methodType) {
            this.methodType = methodType;
        }

        public int getMethodId() {
            return this.methodId;
        }

        public void setMethodId(int methodId) {
            this.methodId = methodId;
        }
    }

}