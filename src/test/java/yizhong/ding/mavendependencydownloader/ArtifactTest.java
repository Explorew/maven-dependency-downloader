package yizhong.ding.mavendependencydownloader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class ArtifactTest {
    @Test
    /**
     * Test for the constructor of Artifact
     */
    public void testConstructor() {
        Artifact actualArtifact = new Artifact("42", "42", "1.0.2");
        actualArtifact.setArtifactId("42");
        ArrayList<Artifact> artifactList = new ArrayList<Artifact>();
        actualArtifact.setDependencies(artifactList);
        actualArtifact.setGroupId("42");
        actualArtifact.setVersion("1.0.2");
        assertEquals("42", actualArtifact.getArtifactId());
        assertSame(artifactList, actualArtifact.getDependencies());
        assertEquals("42", actualArtifact.getGroupId());
        assertEquals("1.0.2", actualArtifact.getVersion());
        assertEquals("42:42:1.0.2", actualArtifact.toString());
    }

    @Test
    /**
     * Test for override equals method
     */
    public void testEquals() {
        assertFalse((new Artifact("42", "42", "1.0.2")).equals(null));
        assertFalse((new Artifact("42", "42", "1.0.2")).equals("Different type to Artifact"));
    }

    @Test
    /**
     * Test for override equals method
     */
    public void testEquals2() {
        Artifact artifact = new Artifact("42", "42", "1.0.2");
        assertTrue(artifact.equals(artifact));
        int expectedHashCodeResult = artifact.hashCode();
        assertEquals(expectedHashCodeResult, artifact.hashCode());
    }

    @Test
    /**
     * Test for override equals method
     *  Note: Artifacts with same groupId and artifactId are considered as equal.
     */
    public void testEquals3() {
        Artifact artifact = new Artifact("42", "42", "1.0.2");
        Artifact artifact1 = new Artifact("42", "42", "1.0.3");

        assertTrue(artifact.equals(artifact1));
        int expectedHashCodeResult = artifact.hashCode();
        assertEquals(expectedHashCodeResult, artifact1.hashCode());
    }

    @Test
    /**
     * Test for override equals method
     */
    public void testEquals4() {
        Artifact artifact = new Artifact("41", "42", "1.0.2");
        assertFalse(artifact.equals(new Artifact("42", "42", "1.0.2")));
    }

}

