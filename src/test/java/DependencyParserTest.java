import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Element;
import org.junit.Test;

public class DependencyParserTest {
    @Test
    public void testCheckArtifactId() {
        assertFalse(DependencyParser.checkArtifactId("42"));
        assertTrue(DependencyParser.checkArtifactId((String) "${pom.artifactId}"));
        assertTrue(DependencyParser.checkArtifactId((String) "${pom/artifactId}"));
        assertTrue(DependencyParser.checkArtifactId((String) "${project/artifactId}"));
        assertTrue(DependencyParser.checkArtifactId((String) "${project.artifactId}"));
    }

    @Test
    public void testCheckGroupId() {
        assertFalse(DependencyParser.checkGroupId("42"));
        assertTrue(DependencyParser.checkGroupId((String) "${project/groupId}"));
        assertTrue(DependencyParser.checkGroupId((String) "${pom/groupId}"));
        assertTrue(DependencyParser.checkGroupId((String) "${project.groupId}"));
        assertTrue(DependencyParser.checkGroupId((String) "${pom.groupId}"));
    }

    @Test
    public void testCheckVersion() {
        assertFalse(DependencyParser.checkVersion(new Element("Name")));
    }

    @Test
    public void testCheckVersion2() {
        Element element = new Element("Name");
        element.addContent(new CDATA("String"));
        assertFalse(DependencyParser.checkVersion(element));
    }

    @Test
    public void testCheckVersion3() {
        Element element = new Element("Name");
        element.addContent("${pom.version}");
        assertTrue(DependencyParser.checkVersion(element));
    }

    @Test
    public void testCheckVersion4() {
        Element element = new Element("Name");
        element.addContent(new CDATA("${project.version}"));
        assertTrue(DependencyParser.checkVersion(element));
    }

    @Test
    public void testCheckVersion5() {
        Element element = new Element("Name");
        element.addContent(new CDATA("${pom/version}"));
        assertTrue(DependencyParser.checkVersion(element));
    }

    @Test
    public void testCheckVersion6() {
        Element element = new Element("Name");
        element.addContent(new CDATA("${project/version}"));
        assertTrue(DependencyParser.checkVersion(element));
    }

    @Test
    public void testCheckVersion7() {
        Element element = new Element("Name");
        element.addContent(new Comment("Text"));
        assertFalse(DependencyParser.checkVersion(element));
    }

    @Test
    public void testCheckVersion8() {
        Element element = new Element("Name");
        element.addContent(new Element("Name"));
        assertFalse(DependencyParser.checkVersion(element));
    }

    @Test
    public void testFetchDependencies() {
//        assertTrue(DependencyParser.fetchDependencies("foo", new Artifact("42", "42", "1.0.2")).isEmpty());
//        assertTrue(DependencyParser.fetchDependencies("${pom.groupId}", new Artifact("42", "42", "1.0.2")).isEmpty());
    }
}

