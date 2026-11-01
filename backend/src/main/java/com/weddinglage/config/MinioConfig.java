package com.weddinglage.config;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class MinioConfig {

    @Bean
    public S3Client s3Client(WeddinglageProperties properties) {
        var minio = properties.getMinio();
        return S3Client.builder()
                .endpointOverride(URI.create(minio.getEndpoint()))
                .region(Region.of(minio.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(minio.getAccessKey(), minio.getSecretKey())))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(WeddinglageProperties properties) {
        var minio = properties.getMinio();
        return S3Presigner.builder()
                .endpointOverride(URI.create(minio.getEndpoint()))
                .region(Region.of(minio.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(minio.getAccessKey(), minio.getSecretKey())))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}
