import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Element;
import org.junit.Test;

import static org.junit.Assert.*;

public class DependencyParserTest {
    @Test
    public void testFetchVersion() {
        assertNull(DependencyParser.fetchVersion(new Element("name")));
    }

    @Test
    public void testFetchVersion2() {
        Element element = new Element("Name");
        element.addContent(new CDATA("String"));
        assertNull(DependencyParser.fetchVersion(element));
    }

    @Test
    public void testSearchOnlineVersion() {
        assertNull(DependencyParser.searchOnlineVersion("Name", "Name"));
    }

    @Test
    public void testSearchOnlineVersion2() {
        assertEquals(DependencyParser.searchOnlineVersion("org.codehaus.mojo", "animal-sniffer-parent"), "1.20");
    }

    @Test
    public void testFetchVersion3() {
        Element element = new Element("Name");
        element.addContent(new Element("Name"));
        assertNull(DependencyParser.fetchVersion(element));
    }

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



}

