package org.mipams.jumbf.demo.config;

import org.mipams.fake_media.services.content_types.AssertionStoreContentType;
import org.mipams.fake_media.services.content_types.ClaimContentType;
import org.mipams.fake_media.services.content_types.ClaimSignatureContentType;
import org.mipams.fake_media.services.content_types.CredentialStoreContentType;
import org.mipams.fake_media.services.content_types.ManifestStoreContentType;
import org.mipams.fake_media.services.content_types.StandardManifestContentType;
import org.mipams.fake_media.services.content_types.UpdateManifestContentType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FakeMediaConfig {

    @Bean
    public AssertionStoreContentType assertionStoreContentType() {
        return new AssertionStoreContentType();
    }

    @Bean
    public ClaimContentType claimContentType() {
        return new ClaimContentType();
    }

    @Bean
    public ClaimSignatureContentType claimSignatureContentType() {
        return new ClaimSignatureContentType();
    }

    @Bean
    public CredentialStoreContentType credentialStoreContentType() {
        return new CredentialStoreContentType();
    }

    @Bean
    public ManifestStoreContentType manifestContentType() {
        return new ManifestStoreContentType();
    }

    @Bean
    public StandardManifestContentType standardManifestContentType() {
        return new StandardManifestContentType();
    }

    @Bean
    public UpdateManifestContentType updateManifestContentType() {
        return new UpdateManifestContentType();
    }
}