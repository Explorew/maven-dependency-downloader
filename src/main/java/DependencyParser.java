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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DependencyParser {
    public static List<Artifact> fetchDependencies(String REMOTE_URL, Artifact artifact) {
        String coordinate = artifact.getGroupId().replace('.', '/') + '/'
                + artifact.getArtifactId() + '/'
                + artifact.getVersion() + '/'
                + artifact.getArtifactId() + '-' + artifact.getVersion() + ".pom";
        List<Artifact> result = new ArrayList<>();
        try {
            SAXBuilder sax = new SAXBuilder();
            URL url = new URL(REMOTE_URL + coordinate);
            //TODO: try another way
            Document doc = null;
            try {
                doc = sax.build(url);

            }
            catch(FileNotFoundException e){
                url = new URL(REMOTE_URL
                        + artifact.getGroupId().replace('.', '/') + '/'
                        + artifact.getVersion() + '/'
                        + artifact.getArtifactId() + '/'
                        + artifact.getVersion() + '-' + artifact.getArtifactId() + ".pom");
                doc = sax.build(url);
            }
            if(doc == null) return result;
            Element rootNode = doc.getRootElement();
            Namespace ns = Namespace.getNamespace("http://maven.apache.org/POM/4.0.0");
            Element dependencies = rootNode.getChild("dependencyManagement", ns);
            if (dependencies == null) {
                dependencies = rootNode.getChild("dependencies", ns);
            } else {
                dependencies = dependencies.getChild("dependencies", ns);
            }
            if (dependencies != null) {
                for (Element target : dependencies.getChildren("dependency", ns)) {
                    String childArtifactId = target.getChild("artifactId", ns).getValue();
                    String childGroupId = target.getChild("groupId", ns).getValue();
                    Element version = target.getChild("version", ns);
                    String childVersion = null; //TODO: modify version.getValue()
//                    <groupId>${pom.groupId}</groupId>
//                    <artifactId>hamcrest-core</artifactId>
//                    <version>${pom.version}</version>
                    if(childArtifactId.equals("${pom.artifactId}") || childArtifactId.equals("${project.artifactId}")|| childArtifactId.equals("${pom/artifactId}") || childArtifactId.equals("${project/artifactId}")){
                        childArtifactId = artifact.getArtifactId();
                    }
                    if(childGroupId.equals("${pom.groupId}") || childGroupId.equals("${project.groupId}") || childGroupId.equals("${project/groupId}")|| childGroupId.equals("${pom/groupId}")){
                        childGroupId = artifact.getGroupId();
                    }
                    if (version == null) {
                        childVersion = searchOnlineVersion(REMOTE_URL, childGroupId, childArtifactId, ns);
                        System.out.println("==============" + childArtifactId + " " + childGroupId +  " " + childVersion);
                        if (childVersion == null){
//                            throw new Error("Error: Version not found!");
                            System.out.println("Error: Version not found!");
                            continue;
                        }
                    }
                    else if(version.getValue().equals("${pom.version}") || version.getValue().equals("${project.version}") || version.getValue().equals("${pom/version}") || version.getValue().equals("${project/version}")){
                        childVersion = artifact.getVersion();
                    }
                    else if (version.getValue().startsWith("${")) {//if version is specified on property element
                        System.out.println(url);
                        childVersion = searchPropertyVersion(rootNode, version.getValue(), ns);
                        System.out.println();
                    }
                    else{
                        childVersion = version.getValue();
                    }
                    Artifact child = new Artifact(childGroupId, childArtifactId, childVersion);
                    result.add(child);
                }
            }

        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
        return result;
    }


    //<version>${junit.version}</version>
    //https://repo1.maven.org/maven2/cn/leancloud/okhttp-parent/2.6.0/okhttp-parent-2.6.0.pom
    private static String searchPropertyVersion(Element rootNode, String childVersion, Namespace ns) {
        Element temp = rootNode.getChild("properties", ns);
        String version = childVersion.substring(childVersion.indexOf("{") + 1, childVersion.indexOf("}"));
        System.out.println(version);
        String res = null;
        try {
            res = temp.getChild(version, ns).getValue();
        }
        catch (Exception e){
            System.out.println(rootNode.getChildren() + " " + e);
            //TODO: add exception handling
        }
        return res;
    }

    /**
     * if the dependency version is not specified int the POM file, it will search on Maven central library to fetch the first available version
     * */
    private static String searchOnlineVersion(String REMOTE_URL, String childGroupId, String childArtifactId, Namespace ns) {
        String version = null;
        try {
            SAXBuilder sax = new SAXBuilder();
            // XML is in a web-based location
            //TODO: dont hardcode url

            String searchURL = "https://search.maven.org/" + "solrsearch/select?q=g:\"" + childGroupId + "\"+AND+a:\"" + childArtifactId + "\"&core=gav&rows=20&wt=pom";
            URL url = new URL(searchURL);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            Document doc = sax.build(String.valueOf(uri));

            Element rootNode = doc.getRootElement();
            Element responses = rootNode.getChild("result");

            if (responses != null) {
                for (Element target : responses.getChildren()) {
                    String childVersion = null;
                    String query = "//*[@name= 'v']";
                    XPathExpression<Element> xpe = XPathFactory.instance().compile(query, Filters.element());
                    for (Element urle : xpe.evaluate(target))
                    {
                        childVersion = urle.getValue();
                    }
                    if (childVersion == null) {
                        continue;
                    }
                    version = childVersion;
                    break;
                }
            }
        } catch (IOException | JDOMException | URISyntaxException e) {
            e.printStackTrace();
        }
        return version;
    }

}
