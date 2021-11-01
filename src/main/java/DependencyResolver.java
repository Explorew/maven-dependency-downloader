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
        if(args.length < 3)
            throw new ArtifactResolveException("Please specify the coordinate of the target Artifact");
        int argsIndex = 0;
        if(args.length > 3) // override default download path if provided as runtime argument
            DOWNLOAD_PATH = args[argsIndex++];
        //sample test case
        String groupId = args[argsIndex++];//"junit";
        String artifactId = args[argsIndex++];//"junit";
        String version = args[argsIndex++]; //"4.13.2";
        Artifact artifact = handleInputArtifact(groupId, artifactId, version);
        resolveDependencies(artifact);
    }

    /**
     * The methods plays the roles of traversing the dependency graph of the given artifact
     *      and downloading the artifacts as jar files. It utilizes a BFS algorithm to traverse
     *      the dependency graph.
     * @param target the target Artifact
     * @return Returns a list of successfully downloaded artifacts.
     */
    public static List<Artifact> resolveDependencies(Artifact target){
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
     * This method validates the user entered coordinate of an artifact as well as the directory of output jar files.
     * @return An Artifact object created by the given artifact coordinate.
     * @throws Exception Throw an exception if the input is not valid.
     * @param groupId input groupId
     * @param artifactId input artifactId
     * @param version input version
     */
    static Artifact handleInputArtifact(String groupId, String artifactId, String version) throws Exception {
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
     * @throws IOException Throw an exception if it fails to write the file, or it fails to fetch the Jar file online.
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
