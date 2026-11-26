package dev.david.file_conversion_service.converter;

import java.io.InputStream;

public interface Converter {
    boolean supports(FileType fileType);
    byte[] convert(InputStream originalFile);
}
