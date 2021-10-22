import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Plays the role of parsing a list of (direct) dependency of a given Artifact.
 *  It uses HTTP based fetcher to retrive a POM file and return the Dependencies lists on the POM file.
 */
public class DependencyParser {

    public static List<Artifact> fetchDependencies(Artifact artifact) {
        List<Artifact> result = new ArrayList<>();
        String REMOTE_URL = Util.getPomURL();
            Document doc = parseDoc(REMOTE_URL, artifact);
            // Try another way to construct the URL
            if(doc == null) doc = parseDocReversely(REMOTE_URL, artifact);
            // If the dependencies are not found, return the empty list
            if(doc == null){
                System.out.println("Failed to find dependencies for: " + artifact.toString());
                return result;
            }
            Element rootNode = doc.getRootElement();
            Namespace ns = Namespace.getNamespace(Util.namespace());
            // Check if the POM file contains "dependencyManagement" element
            Element dependencies = rootNode.getChild("dependencyManagement", ns);
            // If not, check for "dependencies" element
            if (dependencies == null) {
                dependencies = rootNode.getChild("dependencies", ns);
            } else {
                dependencies = dependencies.getChild("dependencies", ns);
            }
            //If the POM file does contain "dependencyManagement" or "dependencies"  element
            if (dependencies != null) {
                getDependencyList(result, dependencies, ns, rootNode, artifact);
            }
        return result;
    }

    // TODO: Check [] and () conditions
    // Return a list of dependency parsed from the given POM element
    private static void getDependencyList(List<Artifact> result, Element dependencies, Namespace ns, Element rootNode, Artifact artifact) {
        for (Element target : dependencies.getChildren("dependency", ns)) {
            String childArtifactId = target.getChild("artifactId", ns).getValue();
            String childGroupId = target.getChild("groupId", ns).getValue();
            Element version = target.getChild("version", ns);
            String childVersion = null;
            // Check if artifactId and groupId are not variables. eg: ${pom.groupId}
            if(checkArtifactId(childArtifactId)){
                childArtifactId = artifact.getArtifactId();
            }
            if(checkGroupId(childGroupId)){
                childGroupId = artifact.getGroupId();
            }
            // If version is not specified in the POM file, search on maven library
            if (version == null) {
                childVersion = searchOnlineVersion(childGroupId, childArtifactId);
                if (childVersion == null){
                    System.out.println("Error: Version not found for: " + childArtifactId + " " + childVersion);
                    continue;
                }
            }
            // Check if version is not variable. eg: ${pom.version}
            else if(checkVersion(version)){
                childVersion = artifact.getVersion();
            }
            // If version is specified in property element
            else if (version.getValue().startsWith("${")) {
                childVersion = searchPropertyVersion(rootNode, version.getValue(), ns);
            }
            // Normal version format
            else{
                childVersion = version.getValue();
            }
            if(childVersion == null) continue;
            Artifact child = new Artifact(childGroupId, childArtifactId, childVersion);
            result.add(child);
        }
    }

    // HELPER METHODS:
    // It returns a Document object fetched by SAXBuilder.
    private static Document parseDoc(String REMOTE_URL, Artifact artifact){
        try{
            SAXBuilder sax = new SAXBuilder();
            URL url = new URL(REMOTE_URL + Util.createPomPath(artifact));
            Document doc = sax.build(url);
            return doc;
        } catch (JDOMException | IOException e) {
            return null;
        }
    }

    // It returns a Document object fetched by SAXBuilder. The URL is constructed in a reversed order.
    private static Document parseDocReversely(String REMOTE_URL, Artifact artifact) {
        try{
            SAXBuilder sax = new SAXBuilder();
            URL url = new URL(REMOTE_URL + Util.createReversedPomPath(artifact));
            Document doc = sax.build(url);
            return doc;
        }
        catch (JDOMException | IOException e){
            return null;
        }
    }

    //  It searches version of a dependency which is specified as a variable
    //      <version>${junit.version}</version>
    //      https://repo1.maven.org/maven2/cn/leancloud/okhttp-parent/2.6.0/okhttp-parent-2.6.0.pom
    private static String searchPropertyVersion(Element rootNode, String childVersion, Namespace ns) {
        Element temp = rootNode.getChild("properties", ns);
        String version = childVersion.substring(childVersion.indexOf("{") + 1, childVersion.indexOf("}"));
        String res = null;
        try {
            res = temp.getChild(version, ns).getValue();
        }
        catch (Exception e){
            System.out.println("Failed to find version.");
        }
        return res;
    }

    // If the dependency version is not specified int the POM file,
    //  it will search the coordinate on Maven central library to fetch the first available version.
    private static String searchOnlineVersion(String childGroupId, String childArtifactId) {
        String version = null;
        try {
            SAXBuilder sax = new SAXBuilder();
            String searchURL = Util.getSearchURL() + Util.getSearchPath(childArtifactId, childGroupId);
            URL url = new URL(searchURL);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            Document doc = sax.build(String.valueOf(uri));
            Element rootNode = doc.getRootElement();
            Element responses = rootNode.getChild("result");
            if (responses != null) {
                // Find the first version in <str name='v'> element
                for (Element target : responses.getChildren()) {
                    String childVersion = null;
                    String query = "//*[@name= 'v']";
                    XPathExpression<Element> xpe = XPathFactory.instance().compile(query, Filters.element());
                    for (Element urle : xpe.evaluate(target))
                    {
                        childVersion = urle.getValue();
                    }
                    if (childVersion == null) continue;
                    version = childVersion;
                    break;
                }
            }
        } catch (IOException | JDOMException | URISyntaxException e) {
            System.out.println(e);
        }
        return version;
    }

    //helper methods for checking if ArtifactId is a variable
    public static boolean checkArtifactId(String childArtifactId){
        return childArtifactId.equals("${pom.artifactId}")
                || childArtifactId.equals("${project.artifactId}")
                || childArtifactId.equals("${pom/artifactId}")
                || childArtifactId.equals("${project/artifactId}");
    }

    public static boolean checkGroupId(String childGroupId){
        return childGroupId.equals("${pom.groupId}")
                || childGroupId.equals("${project.groupId}")
                || childGroupId.equals("${project/groupId}")
                || childGroupId.equals("${pom/groupId}");
    }

    public static boolean checkVersion(Element version){
        return version.getValue().equals("${pom.version}")
                || version.getValue().equals("${project.version}")
                || version.getValue().equals("${pom/version}")
                || version.getValue().equals("${project/version}");
    }
}
