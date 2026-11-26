package dev.david.file_conversion_service.detector;

import dev.david.file_conversion_service.converter.FileType;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

@Component
public class FileTypeDetector {

    public FileType detect(byte[] content) {

        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Empty file");
        }

        FileType byMagic = detectByMagic(content);

        if (byMagic != null) {
            return byMagic;
        }

        if (isValidUtf8Text(content)) {
            return FileType.TXT;
        }
        throw new IllegalArgumentException("Unsupported file type");
    }

    private FileType detectByMagic(byte[] content) {

        if (content == null || content.length < 4) {
            return null;
        }

        // PNG: 89 50 4E 47
        if (content[0] == (byte) 0x89 && content[1] == 0x50
                && content[2] == 0x4E && content[3] == 0x47) {
            return FileType.PNG;
        }

        // JPG: FF D8 FF
        if (content[0] == (byte) 0xFF && content[1] == (byte) 0xD8
                && content[2] == (byte) 0xFF) {
            return FileType.JPG;
        }

        // ZIP: 50 4B 03 04
        if (content[0] == 0x50 && content[1] == 0x4B
                && (content[2] == 0x03 || content[2] == 0x05 || content[2] == 0x07)) {
            return FileType.ZIP;
        }


        return null;
    }

    private boolean isValidUtf8Text(byte[] content) {

        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()

                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);

        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(content);
            CharBuffer charBuffer = decoder.decode(byteBuffer);

            for (int i = 0; i < charBuffer.length(); i++) {
                char c = charBuffer.charAt(i);
                if (c == '\t' || c == '\n' || c == '\r') {
                    continue;
                }
                if (c < 32 || c == 127) {
                    return false;
                }
            }

        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }
}
