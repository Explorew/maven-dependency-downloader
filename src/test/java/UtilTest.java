import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilTest {

    @Test
    public void testGetPomURL() {
        assertEquals("https://search.maven.org/remotecontent?filepath=", Util.getPomURL());
    }

    @Test
    public void testGetJarURL() {
        assertEquals("https://search.maven.org/remotecontent?filepath=", Util.getJarURL());
    }

    @Test
    public void testGetSearchURL() {
        assertEquals("https://search.maven.org/", Util.getSearchURL());
    }


    @Test
    public void testCreatePomPath() {
        assertEquals("42/42/1.0.2/42-1.0.2.pom", Util.createPomPath(new Artifact("42", "42", "1.0.2")));
    }

    @Test
    public void testCreatePomPath2() {
        Artifact artifact = new Artifact("42", "42", "1.0.2");
        artifact.setArtifactId("41");
        assertEquals("42/41/1.0.2/41-1.0.2.pom", Util.createPomPath(artifact));
    }

    @Test
    public void testCreateReversedPomPath() {
        assertEquals("42/1.0.2/42/1.0.2-42.pom", Util.createReversedPomPath(new Artifact("42", "42", "1.0.2")));
    }

    @Test
    public void testCreateReversedPomPath2() {
        Artifact artifact = new Artifact("42", "42", "1.0.2");
        artifact.setArtifactId("41");
        assertEquals("42/1.0.2/41/1.0.2-41.pom", Util.createReversedPomPath(artifact));
    }

    @Test
    public void testNamespace() {
        assertEquals("http://maven.apache.org/POM/4.0.0", Util.namespace());
    }

    @Test
    public void testGetSearchPath() {
        assertEquals("solrsearch/select?q=g:\"42\"+AND+a:\"42\"&core=gav&rows=20&wt=pom", Util.getSearchPath("42", "42"));
    }

    @Test
    public void testGetJarPath() {
        assertEquals("42/42/1.0.2/42-1.0.2.jar", Util.getJarPath(new Artifact("42", "42", "1.0.2")));
    }

    @Test
    public void testGetJarPath2() {
        Artifact artifact = new Artifact("42", "42", "1.0.2");
        artifact.setArtifactId("41");
        assertEquals("42/41/1.0.2/41-1.0.2.jar", Util.getJarPath(artifact));
    }
}

