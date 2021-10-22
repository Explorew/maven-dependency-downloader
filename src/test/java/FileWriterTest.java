import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import java.io.*;

import org.junit.Test;

public class FileWriterTest {
    @Test
    public void testWrite() throws IOException {
        FileWriter fileWriter = new FileWriter();
        assertThrows(Error.class,
                () -> fileWriter.write(new ByteArrayInputStream("AAAAAAAAAAAAAAAAAAAAAAAA".getBytes("UTF-8")), 10.0));
    }

    @Test
    //TODO: delete test file after testing
    public void testWrite2() throws IOException {
        FileWriter fileWriter = new FileWriter();
        File file = new File("./test.txt");
        fileWriter.setOutputStream(new FileOutputStream("./test.txt"));
        fileWriter.write(new ByteArrayInputStream("AAAAAAAAAAAAAAAAAAAAAAAA".getBytes("UTF-8")), 10.0);
        boolean res = file.exists();
        file.delete();
        assertTrue(res);
    }



    @Test
    //TODO: figure out how to test close
    public void testClose() throws IOException {
        FileWriter fileWriter = new FileWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1);
        fileWriter.setOutputStream(byteArrayOutputStream);

        fileWriter.close();

//        verify(byteArrayOutputStream).close();
    }

    @Test
    public void testConstructor() {
        FileWriter actualFileWriter = new FileWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1);
        actualFileWriter.setOutputStream(byteArrayOutputStream);
        assertSame(byteArrayOutputStream, actualFileWriter.getOutputStream());
    }
}

