package dev.david.file_conversion_service.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String accessKey;
    private String secretKey;
    private String uploadsBucket;
    private String convertedBucket;
    private String minioUrl;
}
