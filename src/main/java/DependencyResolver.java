import okhttp3.OkHttpClient;
import org.apache.maven.model.Repository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class DependencyResolver {
    public static void main(String[] args) throws FileNotFoundException {
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Please type groupId: ");
//        String groupId = scanner.next();
//        System.out.print("Please type artifactId: ");
//        String artifactId = scanner.next();
//        System.out.print("Please type version: ");
//        String version = scanner.next();

//        String groupId = "com.jolira";
//        String artifactId = "guice";
//        String version = "3.0.0";

        String groupId = "org.httpmock";
        String artifactId = "httpmock";
        String version = "1.4";

        Artifact target = new Artifact(groupId, artifactId, version);
        Set<Artifact> downloaded = new HashSet<>();
        Set<Artifact> falied = new HashSet<>();
        Queue<Artifact> queue = new LinkedList<>();
        queue.add(target);
        while(!queue.isEmpty()){
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Artifact curr = queue.poll();
                if(downloaded.contains(curr) || falied.contains(curr)) continue;
                downloaded.add(curr);
                try{
                    download(curr);
                }
                catch (Exception e){
                    System.out.println(e);
                    System.out.println("Error: failed to download " + curr.toString());
                }
                try{
                    List<Artifact> dependencies = DependencyParser.fetchDependencies("https://search.maven.org/remotecontent?filepath=", curr);
                    curr.setDependencies(dependencies);
                    queue.addAll(dependencies);
                }
                catch(Error error){
                    System.out.println(error);
                }

            }
        }
        System.out.println("Successfully downloaded: ");
        for (Artifact artifact: downloaded) {
            System.out.println("\t" + artifact.getArtifactId() + " " + artifact.getVersion() + " " + artifact.getGroupId());
        }
    }

    public static void download(Artifact artifact) throws IOException {
        Repository repo = new Repository();
        repo.setUrl("https://search.maven.org/remotecontent?filepath=");
        OutputStream outputStream = new FileOutputStream(artifact + ".jar");
        FileWriter fileWriter = new FileWriter();
        fileWriter.setOutputStream(outputStream);
        OkHttpClient client = new OkHttpClient();
        ArtifactDownloader downloader = new ArtifactDownloader(client, fileWriter);
        String path = artifact.getGroupId().replace(".", "/") + "/" + artifact.getArtifactId() + "/" + artifact.getVersion() + "/" + artifact.getArtifactId() + "-" +  artifact.getVersion() + ".jar";
        downloader.download(repo, path);
    }
}
