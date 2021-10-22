import okhttp3.*;
import org.apache.maven.model.Repository;
import java.io.IOException;
import java.util.Objects;

/**
 * Plays the role of downloading JAR files by using a http based fetcher.
 */
public class ArtifactDownloader implements AutoCloseable {
    private final OkHttpClient client;
    private final FileWriter writer;

    // Constructor of ArtifactDownloader taking an OkHttpClient and a FileWriter as parameters.
    public ArtifactDownloader(OkHttpClient client, FileWriter writer) {
        this.client = client;
        this.writer = writer;
    }

    // Performs the downloading responsibility. It requires the URL of a maven library
    //  and the path of the target file as parameters.
    public long download(String url, String path) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try{
            Call call = client.newCall(request);
            Response response = call.execute();
            // If the HTTP request is not successful
            if (!response.isSuccessful()) {
                if(response.code() == 404){
                    throw new Error("Error: Page not found! Error code: " + response.code()); //TODO: To be moved into a helper class
                }
                else if(response.code() == 404){
                    throw new Error("Error: Bad request! Error code: " + response.code()); //TODO: To be moved into a helper class
                }
                else{
                    throw new Error("Error: Unknown error! Error code: " + response.code()); //TODO: To be moved into a helper class
                }
            }
            // If the HTTP request is successful
            else{
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new IllegalStateException("Error: Response doesn't contain a file!");
                }
                double length = Double.parseDouble(Objects.requireNonNull(response.header("Content-Length", "1")));
                return writer.write(responseBody.byteStream(), length);
            }
        } catch (Exception e) {
            throw new Error("Error: Failed to establish connections!"); //TODO: To be moved into a helper class
        }
    }

    public OkHttpClient getClient() {
        return client;
    }

    public FileWriter getWriter() {
        return writer;
    }

    @Override
    // Automatically close the writer at the end of the life cycle of this class.
    public void close() throws Exception {
        writer.close();
    }
}
