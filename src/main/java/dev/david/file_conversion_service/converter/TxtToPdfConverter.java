package dev.david.file_conversion_service.converter;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.tools.TextToPDF;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Component
public class TxtToPdfConverter implements Converter {

    public boolean supports(FileType fileType) {
        return fileType == FileType.TXT;
    }

    @Override
    public byte[] convert(InputStream originalFile) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                originalFile, StandardCharsets.UTF_8))) {
            TextToPDF converter = new TextToPDF();
            converter.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
            converter.setFontSize(11);

            try (PDDocument document = converter.createPDFFromText(reader);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                document.save(baos);
                return baos.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert TXT to PDF", e);
        }
    }
}
