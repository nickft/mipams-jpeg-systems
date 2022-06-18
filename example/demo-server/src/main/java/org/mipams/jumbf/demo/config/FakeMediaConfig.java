package org.mipams.jumbf.demo.config;

import org.mipams.fake_media.services.AssertionFactory;
import org.mipams.fake_media.services.ManifestDiscovery;
import org.mipams.fake_media.services.RedactionService;
import org.mipams.fake_media.services.UriReferenceService;
import org.mipams.fake_media.services.consumer.AssertionStoreConsumer;
import org.mipams.fake_media.services.consumer.ClaimConsumer;
import org.mipams.fake_media.services.consumer.ClaimSignatureConsumer;
import org.mipams.fake_media.services.consumer.ManifestConsumer;
import org.mipams.fake_media.services.consumer.ManifestStoreConsumer;
import org.mipams.fake_media.services.content_types.AssertionStoreContentType;
import org.mipams.fake_media.services.content_types.ClaimContentType;
import org.mipams.fake_media.services.content_types.ClaimSignatureContentType;
import org.mipams.fake_media.services.content_types.CredentialStoreContentType;
import org.mipams.fake_media.services.content_types.ManifestStoreContentType;
import org.mipams.fake_media.services.content_types.StandardManifestContentType;
import org.mipams.fake_media.services.content_types.UpdateManifestContentType;
import org.mipams.fake_media.services.producer.AssertionRefProducer;
import org.mipams.fake_media.services.producer.AssertionStoreProducer;
import org.mipams.fake_media.services.producer.ClaimProducer;
import org.mipams.fake_media.services.producer.ClaimSignatureProducer;
import org.mipams.fake_media.services.producer.ManifestProducer;
import org.mipams.fake_media.services.producer.ManifestStoreProducer;
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