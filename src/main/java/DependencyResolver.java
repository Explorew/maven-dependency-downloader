import okhttp3.OkHttpClient;

import java.io.*;
import java.util.*;

/**
 * @author Yizhong Ding
 * This plays the role of resolving the dependencies of an input artifact
 *      and downloads them on a given directory.
 */
public class DependencyResolver {

    private static String DOWNLOAD_PATH = System.getProperty("java.io.tmpdir");

    // TODO: remove this main method => libraries do not have main methods.
    public static void main(String[] args) throws Exception {

        // override default download path if provided as runtime argument
        if(args.length > 0)
            DOWNLOAD_PATH = args[0];

        Artifact artifact = handleInputArtifact();
        List<Artifact> res = resolveDependencies(artifact, null);
        for (Artifact a: res
             ) {
            System.out.println(a);
        }
    }

    /**
     * The methods plays the roles of traversing the dependency graph of the given artifact
     *      and downloading the artifacts as jar files. It utilizes a BFS algorithm to traverse
     *      the dependency graph.
     * @param path the path of Jar files
     * @param target the target Artifact
     * @return Returns a list of successfully downloaded artifacts.
     */
    public static List<Artifact> resolveDependencies(Artifact target, String path){
        if(path != null) DOWNLOAD_PATH = path;
        Set<Artifact> downloaded = new HashSet<>();
        Queue<Artifact> queue = new LinkedList<>();
        queue.add(target);
        while(!queue.isEmpty()){
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                traverseDependencyNode(downloaded, queue);
            }
        }
        System.out.println("Successfully downloaded: ");
        for (Artifact artifact: downloaded) {
            System.out.println("\t" + artifact.getArtifactId() + " " + artifact.getVersion() + " " + artifact.getGroupId());
        }
        return new ArrayList<>(downloaded);
    }

    /**
     * Helper method that visits dependency Graph nodes.
     * @param downloaded a set of successfully downloaded Artifacts
     * @param queue queue used for BFS traverse the dependency graph
     */
    public static void traverseDependencyNode(Set<Artifact> downloaded, Queue<Artifact> queue) {
        Artifact curr = queue.poll();
        if(downloaded.contains(curr)) return;
        try{
            // Download the visited artifact and add it in the set.
            download(curr);
            downloaded.add(curr);
        }
        catch (Exception e){
            System.out.println("Error: failed to download " + curr.toString());
        }
        try{
            // Fetch dependencies list of curr artifact and add them in the help queue.
            List<Artifact> dependencies = DependencyParser.fetchDependencies(curr);
            curr.setDependencies(dependencies);
            queue.addAll(dependencies);
        }
        catch(Error error){
            System.out.println(error);
        }
    }

    /**
     * This method requires the user enter the coordinate of an artifact as well as the directory of output jar files.
     * @return An Artifact object created by the given artifact coordinate.
     * @throws Exception Throw an except if the input is not valid.
     */
    static Artifact handleInputArtifact() throws Exception {

        // TODO: place this in a unit test.
        //sample test case
        String groupId = "junit";
        String artifactId = "junit";
        String version = "4.13.2";
        DOWNLOAD_PATH = "./temp";

        // Check if the directory exists.
        File directory = new File(DOWNLOAD_PATH);
        if (!directory.exists()){
            directory.mkdirs();
        }
        if(groupId.length() == 0 || artifactId.length() == 0 || version.length() == 0){
            throw new Exception("Error: Input artifact is not valid");
        }
        return new Artifact(groupId, artifactId, version);
    }

    /**
     * The method plays the role of downloading Artifact.
     * @param artifact The maven artifact to be downloaded.
     * @throws IOException Throw an except if it fails to write the file or it fails to fetch the Jar file online.
     */
    public static void download(Artifact artifact) throws IOException {
        OutputStream outputStream = new FileOutputStream(DOWNLOAD_PATH + '/' + artifact + ".jar");
        FileWriter fileWriter = new FileWriter();
        fileWriter.setOutputStream(outputStream);
        OkHttpClient client = new OkHttpClient();
        ArtifactDownloader downloader = new ArtifactDownloader(client, fileWriter);
        String path = Util.getJarPath(artifact);
        downloader.download(Util.getJarURL(), path);
    }
}
