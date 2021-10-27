import okhttp3.*;
import java.util.Objects;

/**
 * @author Alex
 * Plays the role of downloading JAR files by using a http based fetcher.
 */
public class ArtifactDownloader implements AutoCloseable {
    private final OkHttpClient client;
    private final FileWriter writer;

    private final String ERROR_PREAMBLE = "Unable to resolve maven artifact: ";


    // TODO: javadoc
    // Constructor of ArtifactDownloader taking an OkHttpClient and a FileWriter as parameters.
    public ArtifactDownloader(OkHttpClient client, FileWriter writer) {
        this.client = client;
        this.writer = writer;
    }

    /**
     * Performs the downloading responsibility to fetch the jar file located on a given maven library.
     * @param url The URL of a maven library.
     * @param path The path of the target jar file in the maven library.
     * @return Returns the length written on the disk.
     */
    public long download(String url, String path){
        Request request = new Request.Builder().url(url + path).build();
        try{
            Call call = client.newCall(request);
            Response response = call.execute();
            // If the HTTP request is not successful
            if (!response.isSuccessful()) {
                if(response.code() == 404){
                    throw new Error(ERROR_PREAMBLE+ "Page not found! Error code: " + response.code());
                }
                else if(response.code() == 404){
                    throw new Error(ERROR_PREAMBLE+ "Bad request! Error code: " + response.code());
                }
                else{
                    throw new Error(ERROR_PREAMBLE+ "Unknown error! Error code: " + response.code());
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
            throw new ArtifactResolveException(e.getMessage());
        }
    }

    // TODO: write javadoc

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
