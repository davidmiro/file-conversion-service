package dev.david.file_conversion_service.converter;

import dev.david.file_conversion_service.detector.FileTypeDetector;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Component
@RequiredArgsConstructor
public class ZipToPdfConverter implements Converter {

    private final FileTypeDetector fileTypeDetector;
    private final ObjectProvider<List<Converter>> convertersProvider;

    @Override
    public boolean supports(FileType fileType) {
        return fileType == FileType.ZIP;
    }

    @Override
    public byte[] convert(InputStream originalFile) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PDFMergerUtility merger = new PDFMergerUtility();

        merger.setDestinationStream(outputStream);

        try (ZipInputStream zis = new ZipInputStream(originalFile)) {
            ZipEntry entry;
            boolean hasFiles = false;
            while ((entry = zis.getNextEntry()) != null) {

                if (entry.isDirectory()) {
                    continue;
                }
                byte[] content = zis.readAllBytes();

                if (content.length == 0) {
                    continue;
                }
                hasFiles = true;

                    FileType fileType = fileTypeDetector.detect(content);
                    Converter converter = convertersProvider.getObject()
                            .stream()
                            .filter(c -> c.supports(fileType))
                            .findFirst()
                            .orElseThrow();

                    byte[] pdfBytes = converter.convert(new ByteArrayInputStream(content));
                    merger.addSource(new RandomAccessReadBuffer(pdfBytes));

            }
            if (!hasFiles) {
                throw new IllegalArgumentException("ZIP archive contains no valid files");
            }
            merger.mergeDocuments(null);

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert ZIP archive to PDF", e);
        }

        return outputStream.toByteArray();
    }
}