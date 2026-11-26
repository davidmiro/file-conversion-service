package dev.david.file_conversion_service.service;

import dev.david.file_conversion_service.converter.Converter;
import dev.david.file_conversion_service.converter.FileType;
import dev.david.file_conversion_service.detector.FileTypeDetector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileConversionService {

    private final List<Converter> converters;
    private final MinioStorageService minioStorageService;
    private final FileTypeDetector fileTypeDetector;

    private Converter findConverter(FileType fileType) {
        return converters.stream()
                .filter(converter -> converter.supports(fileType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No converter found for type: " + fileType));
    }

    public String convert(String key, String originalFileName) throws Exception {

        try (InputStream originalFile = minioStorageService.downloadOriginalFile(key)) {
            byte[] originalBytes = originalFile.readAllBytes();
            FileType fileType = fileTypeDetector.detect(originalBytes);
            Converter converter = findConverter(fileType);
            byte[] pdfBytes = converter.convert(new ByteArrayInputStream(originalBytes));
            String convertedKey = key + ".pdf";
            minioStorageService.uploadConvertedFile(pdfBytes.length, convertedKey, new ByteArrayInputStream(pdfBytes));
            return convertedKey;
        }
    }
}