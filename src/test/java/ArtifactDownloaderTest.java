import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import java.io.IOException;

import okhttp3.OkHttpClient;
import org.junit.Test;

public class ArtifactDownloaderTest {
    @Test
    /**
     * Test for the constructor of ArtifactDownloader
     */
    public void testConstructor() {
        OkHttpClient okHttpClient = new OkHttpClient();
        FileWriter fileWriter = new FileWriter();
        ArtifactDownloader actualArtifactDownloader = new ArtifactDownloader(okHttpClient, fileWriter);

        assertSame(okHttpClient, actualArtifactDownloader.getClient());
        assertSame(fileWriter, actualArtifactDownloader.getWriter());
    }

    @Test
    /**
     * Test for invalid URL download
     */
    public void testDownload() throws IOException {
        OkHttpClient client = new OkHttpClient();
        assertThrows(ArtifactResolveException.class,
                () -> (new ArtifactDownloader(client, new FileWriter())).download("https://example.org/example", "Path"));
    }

    @Test
    /**
     * Test for invalid URL download
     */
    public void testDownload2() throws IOException {
        OkHttpClient client = new OkHttpClient(new OkHttpClient.Builder());
        assertThrows(ArtifactResolveException.class,
                () -> (new ArtifactDownloader(client, new FileWriter())).download("https://example.org/example", "Path"));
    }

    @Test
    /**
     * Test for invalid URL download
     */
    public void testDownload3() throws IOException {
        assertThrows(ArtifactResolveException.class,
                () -> (new ArtifactDownloader(null, new FileWriter())).download("https://example.org/example", "Path"));
    }

    @Test
    /**
     * Test for invalid URL download
     */
    public void testDownload4() throws IOException {
        OkHttpClient client = new OkHttpClient();
        assertThrows(ArtifactResolveException.class,
                () -> (new ArtifactDownloader(client, new FileWriter())).download("https://example.org/example", "42"));
    }
}

