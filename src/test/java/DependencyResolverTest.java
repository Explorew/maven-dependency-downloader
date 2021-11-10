import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class DependencyResolverTest {

    @Test
    /**
     * Test for downloading the dependency of junit
     */
    public void testResolveDependencies() {
        Artifact target = new Artifact("junit", "junit", "4.13.2");
        List<Artifact> list = new ArrayList<>();
        list.add(new Artifact("junit", "junit", "4.13.2"));
        list.add(new Artifact("org.hamcrest", "hamcrest-library", "1.3"));
        list.add(new Artifact("org.hamcrest", "hamcrest-core", "1.3"));
        assertEquals(list, DependencyResolver.resolveDependencies(target, "./temp"));
        File folder = new File("./temp");
        folder.delete();
    }


    @Test
    /**
     * Test for validating the input artifact data
     */
    public void testHandleInputArtifact() throws Exception {
        Artifact actualHandleInputArtifactResult = DependencyResolver.handleInputArtifact("42", "42", "1.0.2");
        assertEquals("42", actualHandleInputArtifactResult.getArtifactId());
        assertEquals("1.0.2", actualHandleInputArtifactResult.getVersion());
        assertEquals("42", actualHandleInputArtifactResult.getGroupId());
        assertTrue(actualHandleInputArtifactResult.getDependencies().isEmpty());
    }

    @Test
    /**
     * Test for validating the input artifact data
     */
    public void testHandleInputArtifact2() throws Exception {
        assertThrows(Exception.class, () -> DependencyResolver.handleInputArtifact("", "42", "1.0.2"));
    }

    @Test
    /**
     * Test for validating the input artifact data
     */
    public void testHandleInputArtifact3() throws Exception {
        assertThrows(Exception.class, () -> DependencyResolver.handleInputArtifact("42", "", "1.0.2"));
    }

    @Test
    /**
     * Test for validating the input artifact data
     */
    public void testHandleInputArtifact4() throws Exception {
        assertThrows(Exception.class, () -> DependencyResolver.handleInputArtifact("42", "42", ""));
    }
}
