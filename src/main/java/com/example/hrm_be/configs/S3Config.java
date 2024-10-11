package com.example.hrm_be.configs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
  @Value("${hrm-config.s3.key:default}")
  private String doSpaceKey;

  @Value("${hrm-config.s3.secret:default}")
  private String doSpaceSecret;

  @Value("${hrm-config.s3.endpoint:default}")
  private String doSpaceEndpoint;

  @Value("${hrm-config.s3.region:default}")
  private String doSpaceRegion;

  @Bean
  public AmazonS3 getS3() {
    BasicAWSCredentials creds = new BasicAWSCredentials(doSpaceKey, doSpaceSecret);
    return AmazonS3ClientBuilder.standard()
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(doSpaceEndpoint, doSpaceRegion))
        .withCredentials(new AWSStaticCredentialsProvider(creds))
        .build();
  }
}
