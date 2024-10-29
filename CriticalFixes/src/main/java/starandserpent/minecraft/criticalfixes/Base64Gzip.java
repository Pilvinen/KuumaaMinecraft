package starandserpent.minecraft.criticalfixes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64OutputStream;


public class Base64Gzip {

    // Encode text to base64 and compress it with gzip.
    public static String encodeToGzippedBase64(String text) {
        try {
            Base64OutputStream b64os = new Base64OutputStream(System.out);
            GZIPOutputStream gzip = new GZIPOutputStream(b64os);
            gzip.write(text.getBytes("UTF-8"));
            gzip.close();
            b64os.close();
            return b64os.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "";
    }

    // Decompress and decode and return plain old string with no encoding.
    public static String decodeFromGzippedBase64(String gzippedBase64) throws IOException {
        try {

            byte[] decodedBytes = Base64.getDecoder().decode(gzippedBase64);
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(decodedBytes));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            return byteArrayOutputStream.toString("UTF-8");
        } catch (Throwable t) {
            return "";
        }
    }

    // Decompress, do not decode. Returns Base64 String.
    public static String decompressGzippedBase64(String gzippedBase64) throws IOException {
        if (gzippedBase64 == null || gzippedBase64.trim().isEmpty()) {
            throw new IllegalArgumentException("Input Base64 string cannot be null or empty.");
        }

        try {
            // Decode the Base64 gzipped string to bytes
            byte[] decodedBytes = Base64.getDecoder().decode(gzippedBase64);

            // Decompress the gzipped bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(decodedBytes))) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzipInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
            }

            // Get the decompressed byte array
            byte[] decompressedBytes = byteArrayOutputStream.toByteArray();

            // Re-encode the decompressed bytes to Base64
            return Base64.getEncoder().encodeToString(decompressedBytes);

        } catch (IOException e) {
            throw new RuntimeException("Failed to decompress gzipped data: " + e.getMessage(), e);
        }
    }

}