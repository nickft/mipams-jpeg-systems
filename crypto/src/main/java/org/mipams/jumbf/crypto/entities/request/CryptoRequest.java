package org.mipams.jumbf.crypto.entities.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class CryptoRequest {

    private @Getter @Setter String cryptoMethod;

    private @Getter @Setter String contentFileUrl;

    private @Getter @Setter String iv;

    private @Getter @Setter String signatureHexEncoded;
}
