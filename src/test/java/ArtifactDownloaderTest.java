import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import java.io.IOException;

import okhttp3.OkHttpClient;
import org.junit.Test;

public class ArtifactDownloaderTest {
    @Test
    public void testConstructor() {
        OkHttpClient okHttpClient = new OkHttpClient();
        FileWriter fileWriter = new FileWriter();
        ArtifactDownloader actualArtifactDownloader = new ArtifactDownloader(okHttpClient, fileWriter);

        assertSame(okHttpClient, actualArtifactDownloader.getClient());
        assertSame(fileWriter, actualArtifactDownloader.getWriter());
    }

    @Test
    public void testDownload() throws IOException {
        OkHttpClient client = new OkHttpClient();
        assertThrows(Error.class,
                () -> (new ArtifactDownloader(client, new FileWriter())).download("https://example.org/example", "Path"));
    }

    @Test
    public void testDownload2() throws IOException {
        OkHttpClient client = new OkHttpClient(new OkHttpClient.Builder());
        assertThrows(Error.class,
                () -> (new ArtifactDownloader(client, new FileWriter())).download("https://example.org/example", "Path"));
    }

    @Test
    public void testDownload3() throws IOException {
        assertThrows(Error.class,
                () -> (new ArtifactDownloader(null, new FileWriter())).download("https://example.org/example", "Path"));
    }

    @Test
    public void testDownload4() throws IOException {
        OkHttpClient client = new OkHttpClient();
        assertThrows(Error.class,
                () -> (new ArtifactDownloader(client, new FileWriter())).download("https://example.org/example", "42"));
    }
}

