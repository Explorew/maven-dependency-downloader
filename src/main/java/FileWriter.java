import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileWriter implements AutoCloseable {

    private static final int CHUNK_SIZE = 1024;
    private OutputStream outputStream;

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

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

    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}