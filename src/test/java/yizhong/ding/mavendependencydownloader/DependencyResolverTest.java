package yizhong.ding.mavendependencydownloader;

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

    private final String TESTING_TEMP_DIR = "./test";


    @Test
    /**
     * Test for downloading the dependency of junit
     */
    public void testResolveDependencies() throws ArtifactResolveException{
        Artifact target = new Artifact("junit", "junit", "4.13.2");
        List<Artifact> list = new ArrayList<>();
        list.add(new Artifact("junit", "junit", "4.13.2"));
        list.add(new Artifact("org.hamcrest", "hamcrest-core", "1.3"));
        assertEquals(list, DependencyResolver.resolveDependencies(target, "./temp"));
        File folder = new File("./temp");
        folder.delete();
    }

    @Test
    /**
     * Test for downloading the dependency of junit
     */
    public void testResolveArtifact() throws ArtifactResolveException {
        List<Artifact> list = new ArrayList<>();
        list.add(new Artifact("junit", "junit", "4.13.2"));
        list.add(new Artifact("org.hamcrest", "hamcrest-core", "1.3"));
        assertEquals(list, DependencyResolver.resolveArtifact("junit", "junit", "4.13.2", "./temp"));
        File folder = new File("./temp");
        folder.delete();
    }

    @Test
    /**
     * Test if the resolved transitive dependency hull of spring boot matches the expected list.
     */
    public void testSpringBootTransitiveAmount() throws ArtifactResolveException {
        Artifact springBoot = new Artifact("org.springframework.boot", "spring-boot-starter-web", "2.2.6.RELEASE");
        verifyArtifactDependencyAmount(springBoot, 37);
    }

    @Test
    /**
     * Test if the resolved transitive dependency hull of spring boot matches the expected list.
     */
    public void testSpringBootTransitiveAmount2() throws ArtifactResolveException {
        Artifact springBoot = new Artifact("com.google.guava", "guava", "31.0.1-jre");
        verifyArtifactDependencyAmount(springBoot, 7);
    }

    @Test
    /**
     * Test if the resolved transitive dependency hull of spring boot matches the expected list.
     */
    public void testSpringBootTransitiveAmount3() throws ArtifactResolveException {
        Artifact springBoot = new Artifact("org.springframework.boot", "spring-boot-starter", "2.2.6.RELEASE");
        verifyArtifactDependencyAmount(springBoot, 18);
    }

    @Test
    /**
     * Test if the resolved transitive dependency hull of spring boot matches the expected list.
     */
    public void testSpringBootTransitiveAmount4() throws ArtifactResolveException {
        Artifact springBoot = new Artifact("org.mockito", "mockito-core", "4.1.0");
        verifyArtifactDependencyAmount(springBoot, 4);
    }

    @Test
    /**
     * Test if the resolved transitive dependency hull of spring boot matches the expected list.
     */
    public void testSpringBootTransitiveAmount5() throws ArtifactResolveException {
        Artifact springBoot = new Artifact("com.fasterxml.jackson.core", "jackson-databind", "2.13.0");
        verifyArtifactDependencyAmount(springBoot, 3);
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

    /**
     * Helper method to verify if the transitive hull collected for a given artifact has the expected size.
     *
     * @param artifact                           as an input artifuact for which we want to collect all transitive
     *                                           dependencies.
     * @param expectedTransitiveDependencyAmount as the amount expected for the transitive hull set size.
     * @author Maximilian Schiedermeier
     */
    private void verifyArtifactDependencyAmount(Artifact artifact, int expectedTransitiveDependencyAmount) throws ArtifactResolveException {
        // Collect transitive hull
        DependencyResolver.resolveArtifact(artifact, TESTING_TEMP_DIR);

        // Verify amount of dependencies in target folder
        File folder = new File(TESTING_TEMP_DIR);
        int length = folder.list().length;
        deleteFolder(folder);
        assertEquals(expectedTransitiveDependencyAmount, length);
    }

    /**
     * Helper method to delete the folder after test
     * @param folder target folder
     */
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files != null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}
