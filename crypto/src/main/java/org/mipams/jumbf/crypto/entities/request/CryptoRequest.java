package org.mipams.jumbf.crypto.entities.request;

public class CryptoRequest {

    private String cryptoMethod;

    private String contentFileUrl;

    private String iv;

    private String signatureHexEncoded;

    public String getCryptoMethod() {
        return this.cryptoMethod;
    }

    public void setCryptoMethod(String cryptoMethod) {
        this.cryptoMethod = cryptoMethod;
    }

    public String getContentFileUrl() {
        return this.cryptoMethod;
    }

    public void setContentFileUrl(String contentFileUrl) {
        this.contentFileUrl = contentFileUrl;
    }

    public String getIv() {
        return this.iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getSignatureHexEncoded() {
        return this.signatureHexEncoded;
    }

    public void setSignatureHexEncoded(String signatureHexEncoded) {
        this.signatureHexEncoded = signatureHexEncoded;
    }

    @Override
    public String toString() {
        final String cryptoMethod = this.cryptoMethod != null ? getCryptoMethod() : "null";
        final String contentFileUrl = this.contentFileUrl != null ? getContentFileUrl() : "null";
        final String iv = this.iv != null ? getIv() : "null";
        final String signatureHexEncoded = this.signatureHexEncoded != null ? getSignatureHexEncoded() : "null";

        StringBuilder builder = new StringBuilder("CryptoRequest(");

        builder.append("cryptoMethod=").append(cryptoMethod).append(", ").append("contentFileUrl=")
                .append(contentFileUrl).append(", ").append("iv").append(iv).append(", ").append("signatureHexEncoded=")
                .append(signatureHexEncoded).append(")");

        return builder.toString();
    }
}
