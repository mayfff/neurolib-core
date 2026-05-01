package kpi.zakrevskyi.neurolib.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class AwsS3Config {

    @Bean
    public S3Client s3Client(
        @Value("${app.aws.s3.path}") String path,
        @Value("${app.aws.s3.region}") String region,
        @Value("${app.aws.s3.access-key}") String accessKey,
        @Value("${app.aws.s3.secret-key}") String secretKey
    ) {
        S3ClientBuilder builder = S3Client.builder()
            .region(Region.of(region));

        if (StringUtils.hasText(path)) {
            builder.endpointOverride(URI.create(path));
            builder.serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            );
        }

        if (StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey)) {
            builder.credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
            );
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        return builder.build();
    }
}
