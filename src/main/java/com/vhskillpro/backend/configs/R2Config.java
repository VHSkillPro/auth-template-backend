package com.vhskillpro.backend.configs;

import com.vhskillpro.backend.utils.r2.R2Properties;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class R2Config {
  private final R2Properties r2Properties;

  public R2Config(R2Properties r2Properties) {
    this.r2Properties = r2Properties;
  }

  @Bean
  public S3Client r2S3Client() {
    return S3Client.builder()
        .region(Region.of("auto"))
        .endpointOverride(
            URI.create("https://" + r2Properties.getAccountId() + ".r2.cloudflarestorage.com"))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    r2Properties.getAccessKeyId(), r2Properties.getSecretAccessKey())))
        .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
        .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
        .build();
  }

  @Bean
  public S3Presigner r2S3Presigner() {
    return S3Presigner.builder()
        .region(Region.of("auto"))
        .endpointOverride(
            URI.create("https://" + r2Properties.getAccountId() + ".r2.cloudflarestorage.com"))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    r2Properties.getAccessKeyId(), r2Properties.getSecretAccessKey())))
        .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
        .build();
  }
}
