//import static org.junit.Assert.assertSame;
//import static org.junit.Assert.assertThrows;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.io.IOException;
//
//import okhttp3.OkHttpClient;
//import org.apache.maven.model.Repository;
//import org.junit.Test;
//
//public class ArtifactDownloaderTest {
//    @Test
//    public void testConstructor() {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        FileWriter fileWriter = new FileWriter();
//        ArtifactDownloader actualArtifactDownloader = new ArtifactDownloader(okHttpClient, fileWriter);
//
//        assertSame(okHttpClient, actualArtifactDownloader.getClient());
//        assertSame(fileWriter, actualArtifactDownloader.getWriter());
//    }
//
//    @Test
//    public void testDownload() throws IOException {
//        OkHttpClient client = new OkHttpClient();
//        ArtifactDownloader artifactDownloader = new ArtifactDownloader(client, new FileWriter());
//
//        Repository repository = new Repository();
//        repository.setUrl("https://example.org/example");
//        assertThrows(Error.class, () -> artifactDownloader.download(repository, "Path"));
//    }
//
//    @Test
//    public void testDownload2() throws IOException {
//        ArtifactDownloader artifactDownloader = new ArtifactDownloader(null, new FileWriter());
//
//        Repository repository = new Repository();
//        repository.setUrl("https://example.org/example");
//        assertThrows(Error.class, () -> artifactDownloader.download(repository, "Path"));
//    }
//
//    @Test
//    public void testDownload3() throws IOException {
//        OkHttpClient okHttpClient = mock(OkHttpClient.class);
//        when(okHttpClient.newCall((okhttp3.Request) any())).thenReturn(null);
//        ArtifactDownloader artifactDownloader = new ArtifactDownloader(okHttpClient, new FileWriter());
//
//        Repository repository = new Repository();
//        repository.setUrl("https://example.org/example");
//        assertThrows(Error.class, () -> artifactDownloader.download(repository, "Path"));
//        verify(okHttpClient).newCall((okhttp3.Request) any());
//    }
//}
//
