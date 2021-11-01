import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Element;
import org.junit.Test;

import static org.junit.Assert.*;

public class DependencyParserTest {
    @Test
    /**
     * Test for fetch version method of DependencyParser
     */
    public void testFetchVersion() {
        assertNull(DependencyParser.fetchVersion(new Element("name")));
    }

    @Test
    /**
     * Test for fetchVersion method of DependencyParser
     */
    public void testFetchVersion2() {
        Element element = new Element("Name");
        element.addContent(new CDATA("String"));
        assertNull(DependencyParser.fetchVersion(element));
    }

    @Test
    /**
     * Test for searchOnlineVersion method of DependencyParser with invalid target
     */
    public void testSearchOnlineVersion() {
        assertNull(DependencyParser.searchOnlineVersion("Name", "Name"));
    }

    @Test
    /**
     * Test for searchOnlineVersion method of DependencyParser with valid target
     */
    public void testSearchOnlineVersion2() {
        assertEquals(DependencyParser.searchOnlineVersion("org.codehaus.mojo", "animal-sniffer-parent"), "1.20");
    }


    @Test
    /**
     * Test for checkArtifactId method of DependencyParser
     */
    public void testCheckArtifactId() {
        assertFalse(DependencyParser.checkArtifactId("42"));
        assertTrue(DependencyParser.checkArtifactId((String) "${pom.artifactId}"));
        assertTrue(DependencyParser.checkArtifactId((String) "${pom/artifactId}"));
        assertTrue(DependencyParser.checkArtifactId((String) "${project/artifactId}"));
        assertTrue(DependencyParser.checkArtifactId((String) "${project.artifactId}"));
    }

    @Test
    /**
     * Test for checkGroupId method of DependencyParser
     */
    public void testCheckGroupId() {
        assertFalse(DependencyParser.checkGroupId("42"));
        assertTrue(DependencyParser.checkGroupId((String) "${project/groupId}"));
        assertTrue(DependencyParser.checkGroupId((String) "${pom/groupId}"));
        assertTrue(DependencyParser.checkGroupId((String) "${project.groupId}"));
        assertTrue(DependencyParser.checkGroupId((String) "${pom.groupId}"));
    }

    @Test
    /**
     * Test for testCheckVersion method of DependencyParser with invalid element
     */
    public void testCheckVersion() {
        assertFalse(DependencyParser.checkVersion(new Element("Name")));
    }

    @Test
    /**
     * Test for testCheckVersion method of DependencyParser with invalid element
     */
    public void testCheckVersion2() {
        Element element = new Element("Name");
        element.addContent(new CDATA("String"));
        assertFalse(DependencyParser.checkVersion(element));
    }

    @Test
    /**
     * Test for testCheckVersion method of DependencyParser  with valid element
     */
    public void testCheckVersion3() {
        Element element = new Element("Name");
        element.addContent("${pom.version}");
        assertTrue(DependencyParser.checkVersion(element));
    }

    @Test
    /**
     * Test for testCheckVersion method of DependencyParser with valid element
     */
    public void testCheckVersion4() {
        Element element = new Element("Name");
        element.addContent(new CDATA("${project.version}"));
        assertTrue(DependencyParser.checkVersion(element));
    }

    @Test
    /**
     * Test for testCheckVersion method of DependencyParser with valid element
     */
    public void testCheckVersion5() {
        Element element = new Element("Name");
        element.addContent(new CDATA("${pom/version}"));
        assertTrue(DependencyParser.checkVersion(element));
    }

    @Test
    /**
     * Test for testCheckVersion method of DependencyParser with valid element
     */
    public void testCheckVersion6() {
        Element element = new Element("Name");
        element.addContent(new CDATA("${project/version}"));
        assertTrue(DependencyParser.checkVersion(element));
    }

    @Test
    /**
     * Test for testCheckVersion method of DependencyParser with invalid element
     */
    public void testCheckVersion7() {
        Element element = new Element("Name");
        element.addContent(new Comment("Text"));
        assertFalse(DependencyParser.checkVersion(element));
    }

    @Test
    /**
     * Test for testCheckVersion method of DependencyParser with invalid element
     */
    public void testCheckVersion8() {
        Element element = new Element("Name");
        element.addContent(new Element("Name"));
        assertFalse(DependencyParser.checkVersion(element));
    }



}

