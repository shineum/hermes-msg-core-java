package hermesmsg.entity;

import jakarta.activation.MimetypesFileTypeMap;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class ByteArrayAttachment {
    String filename;
    String contentType;
    byte[] data;

    public ByteArrayAttachment(String filename, String contentType, byte[] data) {
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
    }

    public ByteArrayAttachment(File f, String contentType) throws Exception {
        this.filename = f.getName();
        this.contentType = contentType;
        this.data = Files.readAllBytes(f.toPath());
    }

    public String getFilename() {
        return this.filename;
    }

    public String getContentType() {
        if (this.contentType == null || this.contentType.isEmpty()) {
            return MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(this.filename);
        } else {
            return this.contentType;
        }
    }

    public String getBase64Data() {
        return Base64.getEncoder().encodeToString(this.data);
    }
}
