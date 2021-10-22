/**
 * Util class providing the common methods
 *  and URL/Path configurations for the project.
 */
public class Util {
    // It generates the path of POM file for a given Artifact
    public static String createPomPath(Artifact artifact){
        String res = artifact.getGroupId().replace('.', '/') + '/'
                + artifact.getArtifactId() + '/'
                + artifact.getVersion() + '/'
                + artifact.getArtifactId() + '-' + artifact.getVersion() + ".pom";
        return res;
    }

    public static String createReversedPomPath(Artifact artifact){
        String res = artifact.getGroupId().replace('.', '/') + '/'
                + artifact.getVersion() + '/'
                + artifact.getArtifactId() + '/'
                + artifact.getVersion() + '-' + artifact.getArtifactId() + ".pom";
        return res;
    }

    public static String namespace(){
        return "http://maven.apache.org/POM/4.0.0";
    }

    public static String getPomURL(){
        return "https://search.maven.org/remotecontent?filepath=";
    }

    public static String getJarURL(){
        return "https://search.maven.org/remotecontent?filepath=";
    }

    public static String getSearchURL(){
        return "https://search.maven.org/";
    }

    public static String getSearchPath(String childArtifactId, String childGroupId){
        return "solrsearch/select?q=g:\"" + childGroupId + "\"+AND+a:\"" + childArtifactId + "\"&core=gav&rows=20&wt=pom";
    }

    public static String getJarPath(Artifact artifact){
        return artifact.getGroupId().replace(".", "/")
                + "/" + artifact.getArtifactId() + "/"
                + artifact.getVersion() + "/"
                + artifact.getArtifactId() + "-"
                + artifact.getVersion() + ".jar";
    }

}