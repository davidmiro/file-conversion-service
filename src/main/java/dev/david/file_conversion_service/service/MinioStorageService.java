package dev.david.file_conversion_service.service;

import dev.david.file_conversion_service.config.MinioProperties;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public InputStream downloadOriginalFile(String key) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getUploadsBucket())
                        .object(key)
                        .build());
    }

    public InputStream downloadConvertedFile(String key) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getConvertedBucket())
                        .object(key)
                        .build()
        );
    }

    public void uploadConvertedFile(long fileSize, String key, InputStream inputStream) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.getConvertedBucket())
                        .object(key)
                        .stream(inputStream, fileSize, -1)
                        .build());
    }

    public void deleteConvertedFile(String key) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.getConvertedBucket())
                        .object(key)
                        .build()
        );
    }

    public void deleteOriginalFile(String key) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.getUploadsBucket())
                        .object(key)
                        .build()
        );
    }
}
