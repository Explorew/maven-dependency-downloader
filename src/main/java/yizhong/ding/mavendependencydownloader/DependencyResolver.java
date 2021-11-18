package yizhong.ding.mavendependencydownloader;

import okhttp3.OkHttpClient;

import java.io.*;
import java.util.*;

/**
 * This plays the role of resolving the dependencies of an input artifact
 *      and downloads them on a given directory.
 * @author Yizhong Ding
 */
public class DependencyResolver {

    public static void main(String[] args) throws ArtifactResolveException {
        resolveArtifact("org.springframework.boot", "spring-boot-starter-logging", "2.2.6.RELEASE", "./temp");
    }

    /**
     * This method resolve the dependencies of a given artifact coordinate and store the Jar files on the given path.
     * @param groupId groupId of the target artifact
     * @param artifactId artifactId of the target artifact
     * @param version version of the target artifact
     * @param downloadPath directory to store the dependencies
     * @return returns a list of dependency Artifacts
     * @throws ArtifactResolveException throw ArtifactResolveException if the inputs are not valid
     */
    public static List<Artifact> resolveArtifact(String groupId, String artifactId, String version, String downloadPath) throws ArtifactResolveException {

       // Default download path is tmp directory, will only override if provided path argument is not null or empty.
        if(downloadPath == null || downloadPath.isEmpty())
            downloadPath = System.getProperty("java.io.tmpdir");

        // Verify download target path exists (create it if not)
        ensureTargetDirectoryExists(downloadPath);

        Artifact artifact = handleInputArtifact(groupId, artifactId, version);
        return resolveDependencies(artifact, downloadPath);
    }

    /**
     * Overloaded method of previous. See javadoc above.
     * @param artifact as the target artifact to collect with all transitive dependencies.
     * @param downloadPath directory to store the dependencies
     * @return list of transitive dependency Artifacts (location on disk)
     * @throws ArtifactResolveException
     */
    public static List<Artifact> resolveArtifact(Artifact artifact, String downloadPath) throws ArtifactResolveException {
        return resolveArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), downloadPath);
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
        return new ArrayList<>(downloaded);
    }

    /**
     * Helper method that visits dependency Graph nodes.
     * @param downloaded a set of successfully downloaded Artifacts
     * @param queue queue used for BFS traverse the dependency graph
     * @param downloadPath path to store Jar files
     */
    public static void traverseDependencyNode(Set<Artifact> downloaded, Queue<Artifact> queue, String downloadPath) {
        Artifact curr = queue.poll();
        if(downloaded.contains(curr)){
            //TODO: use Log4J if you need to log output.
//            System.out.println("!!!! Visited: " + curr);
            return;
        }
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

//TODO: Remove after meeting
//            System.out.println("===========");
//            System.out.println(curr);
//            System.out.println(dependencies);

            queue.addAll(dependencies);
        }
        catch(Error error){
            System.out.println(error);
        }
    }

    /**
     * This method validates the user entered coordinate of an artifact as well as the directory of output jar files.
     * @return An Artifact object created by the given artifact coordinate.
     * @throws ArtifactResolveException Throw an exception if the input is not valid.
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
     * @param downloadPath The path to store Jar files.
     * @throws ArtifactResolveException Throw an exception if it fails to fetch the Jar file online.
     * @throws IOException Throw an exception if it fails to write the file.
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
