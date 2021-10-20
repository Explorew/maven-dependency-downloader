import okhttp3.*;
import org.apache.maven.model.Repository;

import java.io.IOException;
import java.util.Objects;

/**
 *
 *
 * */
public class ArtifactDownloader implements AutoCloseable {
    private final OkHttpClient client;
    private final FileWriter writer;

    public OkHttpClient getClient() {
        return client;
    }

    public FileWriter getWriter() {
        return writer;
    }

    public ArtifactDownloader(OkHttpClient client, FileWriter writer) {
        this.client = client;
        this.writer = writer;
    }

    public long download(Repository repository, String path) throws IOException {
        String url = repository.getUrl() + path;
        Request request = new Request.Builder()
                .url(url)
                .build();
        try{
            Call call = client.newCall(request);
            Response response = call.execute();
            if (!response.isSuccessful()) {
                if(response.code() == 404){
                    throw new Error("Error: Page not found!"); //TODO: To be moved into a helper class
                }
                else{
                    throw new Error("Error: Unknown error!"); //TODO: To be moved into a helper class
                }
            }
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

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
