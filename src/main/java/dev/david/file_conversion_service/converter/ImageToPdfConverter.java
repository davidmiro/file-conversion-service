package dev.david.file_conversion_service.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Set;

@Component
public class ImageToPdfConverter implements Converter {

    public boolean supports(FileType fileType) {
        return Set.of(FileType.PNG, FileType.JPG).contains(fileType);
    }

    @Override
    public byte[] convert(InputStream originalFile) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            byte[] imageBytes = originalFile.readAllBytes();

            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "image");
            PDPage page = new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight()));
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                contentStream.drawImage(pdImage, 0, 0);
            }
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert Image to PDF", e);
        }
    }
}
