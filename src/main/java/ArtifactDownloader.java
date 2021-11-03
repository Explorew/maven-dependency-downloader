import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Yizhong Ding Plays the role of downloading JAR files by using a http based fetcher.
 */
public class ArtifactDownloader implements AutoCloseable {
    private final OkHttpClient client;
    private final FileWriter writer;
    private final String ERROR_PREAMBLE = "Unable to resolve maven artifact: ";


    /**
     * Constructor of ArtifactDownloader taking an OkHttpClient and a FileWriter as parameters.
     *
     * @param client input client
     * @param writer input writer
     */
    public ArtifactDownloader(OkHttpClient client, FileWriter writer) {
        this.client = client;
        this.writer = writer;
    }

    /**
     * Performs the downloading responsibility to fetch the jar file located on a given maven library.
     *
     * @param url  The URL of a maven library.
     * @param path The path of the target jar file in the maven library.
     * @return Returns the length written on the disk.
     */
    public long download(String url, String path) throws ArtifactResolveException {
        Request request = new Request.Builder().url(url + path).build();
        try {
            Call call = client.newCall(request);
            return handleResponse(call.execute());
        } catch (IOException e) {
            throw new ArtifactResolveException(e.getMessage());
        }
    }

    //TODO: write javadoc
    private long handleResponse(Response response)  throws ArtifactResolveException {
        // If the HTTP request is not successful
        if (!response.isSuccessful()) {
            if (response.code() == 404) {
                throw new ArtifactResolveException(ERROR_PREAMBLE + "Page not found! Error code: " + response.code());
            }

            // TODO: Change this, we have twice the same check for 404
            else if (response.code() == 404) {
                throw new ArtifactResolveException(ERROR_PREAMBLE + "Bad request! Error code: " + response.code());
            } else {
                throw new ArtifactResolveException(ERROR_PREAMBLE + "Unknown error! Error code: " + response.code());
            }
        }
        // If the HTTP request is successful
        else {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new ArtifactResolveException(ERROR_PREAMBLE + "Response doesn't contain a file!");
            }
            double length = Double.parseDouble(Objects.requireNonNull(response.header("Content-Length", "1")));
            return writer.write(responseBody.byteStream(), length);
        }
    }

    /**
     * Getter for OkHttpClient
     *
     * @return returns the OkHttpClient instance
     */
    public OkHttpClient getClient() {
        return client;
    }

    /**
     * Getter for FileWriter
     *
     * @return returns the FileWriter instance
     */
    public FileWriter getWriter() {
        return writer;
    }

    /**
     * Automatically close the writer at the end of the life cycle of this class.
     */
    @Override
    public void close() throws Exception {
        writer.close();
    }
}
