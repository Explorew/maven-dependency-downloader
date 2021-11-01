import static org.junit.Assert.*;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;

import java.io.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class FileWriterTest {
    @Test
    public void testWrite() throws IOException {
        FileWriter fileWriter = new FileWriter();
        assertThrows(ArtifactResolveException.class,
                () -> fileWriter.write(new ByteArrayInputStream("AAAAAAAAAAAAAAAAAAAAAAAA".getBytes("UTF-8")), 10.0));
    }

    @Test
    public void testClose() throws IOException {
        FileWriter fileWriter = new FileWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1);
        fileWriter.setOutputStream(byteArrayOutputStream);
        fileWriter.close();
        assertSame(byteArrayOutputStream, fileWriter.getOutputStream());
    }


    @Test
    public void testConstructor() {
        FileWriter actualFileWriter = new FileWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1);
        actualFileWriter.setOutputStream(byteArrayOutputStream);
        assertSame(byteArrayOutputStream, actualFileWriter.getOutputStream());
    }
}

