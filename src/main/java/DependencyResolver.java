import okhttp3.OkHttpClient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * The DependencyResolver class plays the role of traversing the dependency graph of the given artifact.
 *  It utilizes a BFS algorithm.
 */
public class DependencyResolver {
    public static void main(String[] args) throws Exception {
        Artifact target = handleInput();
        Set<Artifact> downloaded = new HashSet<>();
        Queue<Artifact> queue = new LinkedList<>();
        queue.add(target);
        while(!queue.isEmpty()){
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Artifact curr = queue.poll();
                if(downloaded.contains(curr)) continue;
                downloaded.add(curr);
                try{
                    download(curr);
                }
                catch (Exception e){
                    System.out.println("Error: failed to download " + curr.toString());
                }
                try{
                    List<Artifact> dependencies = DependencyParser.fetchDependencies(curr);
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

    static Artifact handleInput() throws Exception {
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Please type groupId: ");
//        String groupId = scanner.next();
//        System.out.print("Please type artifactId: ");
//        String artifactId = scanner.next();
//        System.out.print("Please type version: ");
//        String version = scanner.next();
        //sample test case
        String groupId = "com.jolira";
        String artifactId = "guice";
        String version = "3.0.0";
        if(groupId.length() == 0 || artifactId.length() == 0 || version.length() == 0) throw new Exception("Error: Input artifact is not valid");
        return new Artifact(groupId, artifactId, version);
    }

    // The method is for downloading Artifact
    public static void download(Artifact artifact) throws IOException {
        OutputStream outputStream = new FileOutputStream("./Downloaded/" + artifact + ".jar");
        FileWriter fileWriter = new FileWriter();
        fileWriter.setOutputStream(outputStream);
        OkHttpClient client = new OkHttpClient();
        ArtifactDownloader downloader = new ArtifactDownloader(client, fileWriter);
        String path = Util.getJarPath(artifact);
        downloader.download(Util.getJarURL(), path);
    }
}
