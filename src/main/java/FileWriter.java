import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Plays the role of writing JAR files at the given directory.
 */
public class FileWriter implements AutoCloseable {

    // OutputStream specifies the output file.
    private OutputStream outputStream;
    private static final int CHUNK_SIZE = 1024;

    // Write the InputStream as a file.
    public long write(InputStream inputStream, double length) throws IOException {
        if(outputStream == null) throw new Error("Error: Please specify output stream!");
        try (BufferedInputStream input = new BufferedInputStream(inputStream)) {
            byte[] dataBuffer = new byte[CHUNK_SIZE];
            int readBytes;
            long totalBytes = 0;
            while ((readBytes = input.read(dataBuffer)) != -1) {
                totalBytes += readBytes;
                outputStream.write(dataBuffer, 0, readBytes);
            }
            return totalBytes;
        }
        catch (Exception e) {
            throw new Error("Error: Failed to write files!"); //TODO: To be moved into a helper class
        }
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    // Automatically close the outputStream after saving the JAR file.
    public void close() throws IOException {
        outputStream.close();
    }
}