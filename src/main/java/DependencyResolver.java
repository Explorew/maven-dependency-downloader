import okhttp3.OkHttpClient;

import java.io.*;
import java.util.*;

/**
 * This plays the role of resolving the dependencies of an input artifact
 *      and downloads them on a given directory.
 * @author Yizhong Ding
 */
public class DependencyResolver {

    /**
     * This method resolve the dependencies of a given artifact coordinate and store the Jar files on the given path.
     * @param groupId groupId of the target artifact
     * @param artifactId artifactId of the target artifact
     * @param version version of the target artifact
     * @param downloadPath directory to store the dependencies
     * @throws ArtifactResolveException throw ArtifactResolveException if the inputs are not valid
     */
    public static void resolveArtifact(String groupId, String artifactId, String version, String downloadPath) throws ArtifactResolveException {

       // Default download path is tmp directory, will only ne override if provided path argument is not null or empty.
        if(downloadPath == null || downloadPath.isEmpty())
            downloadPath = System.getProperty("java.io.tmpdir");

        // Verify download target path exists (create it if not)
        ensureTargetDirectoryExists(downloadPath);

        Artifact artifact = handleInputArtifact(groupId, artifactId, version);
        resolveDependencies(artifact, downloadPath);
    }

    /**
     * This method checks if the download directory exists. If not, it will create the directory.
     * @param downloadPath String of download path
     */
    private static void ensureTargetDirectoryExists(String downloadPath) {
        // Check if the directory exists.
        File directory = new File(downloadPath);
        if (!directory.exists()){
            // Create it if it does not yet exist.
            directory.mkdirs();
        }
    }

    /**
     * The methods plays the roles of traversing the dependency graph of the given artifact
     *      and downloading the artifacts as jar files. It utilizes a BFS algorithm to traverse
     *      the dependency graph.
     * @param target the target Artifact
     * @param downloadPath as the location where tha resolved JARs should be stored on disk.
     * @return Returns a list of successfully downloaded artifacts.
     */
    public static List<Artifact> resolveDependencies(Artifact target, String downloadPath){
        ensureTargetDirectoryExists(downloadPath);
        Set<Artifact> downloaded = new HashSet<>();
        Queue<Artifact> queue = new LinkedList<>();
        queue.add(target);
        while(!queue.isEmpty()){
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                traverseDependencyNode(downloaded, queue, downloadPath);
            }
        }
        System.out.println("Successfully downloaded: ");
        for (Artifact artifact: downloaded) {
            System.out.println("\t" + artifact.getArtifactId() + " " + artifact.getVersion() + " " + artifact.getGroupId());
        }
        for ( Artifact artifact: downloaded) {
            System.out.println(artifact);
        }
        return new ArrayList<>(downloaded);
    }

    /**
     * Helper method that visits dependency Graph nodes.
     * @param downloaded a set of successfully downloaded Artifacts
     * @param queue queue used for BFS traverse the dependency graph
     */
    public static void traverseDependencyNode(Set<Artifact> downloaded, Queue<Artifact> queue, String downloadPath) {
        Artifact curr = queue.poll();
        if(downloaded.contains(curr)) return;
        try{
            // Download the visited artifact and add it in the set.
            download(curr, downloadPath);
            downloaded.add(curr);
        }
        catch (Exception e){
            System.out.println("Error: failed to download " + curr.toString());
            System.out.println(e);
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
    static Artifact handleInputArtifact(String groupId, String artifactId, String version) throws ArtifactResolveException {

        if(groupId.length() == 0 || artifactId.length() == 0 || version.length() == 0){
            throw new ArtifactResolveException("Error: Input artifact is not valid");
        }
        return new Artifact(groupId, artifactId, version);
    }

    /**
     * The method plays the role of downloading Artifact.
     * @param artifact The maven artifact to be downloaded.
     * @throws IOException Throw an exception if it fails to write the file, or it fails to fetch the Jar file online.
     */
    public static void download(Artifact artifact, String downloadPath) throws ArtifactResolveException, IOException {
        OutputStream outputStream = new FileOutputStream(downloadPath + '/' + artifact + ".jar");
        FileWriter fileWriter = new FileWriter();
        fileWriter.setOutputStream(outputStream);
        OkHttpClient client = new OkHttpClient();
        ArtifactDownloader downloader = new ArtifactDownloader(client, fileWriter);
        String path = Util.getJarPath(artifact);
        downloader.download(Util.getJarURL(), path);
    }
}
