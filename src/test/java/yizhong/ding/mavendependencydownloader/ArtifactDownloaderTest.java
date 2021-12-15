package yizhong.ding.mavendependencydownloader;

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

}

