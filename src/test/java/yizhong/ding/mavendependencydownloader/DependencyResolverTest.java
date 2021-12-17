package yizhong.ding.mavendependencydownloader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class DependencyResolverTest {

    private final String TESTING_TEMP_DIR = "test";

    @Test
    /**
     * Test for downloading the dependency of junit
     */
    public void testResolveDependencies() throws ArtifactResolveException, IOException {
        Artifact target = new Artifact("junit", "junit", "4.13.2");
        List<Artifact> list = new ArrayList<>();
        list.add(new Artifact("junit", "junit", "4.13.2"));
        list.add(new Artifact("org.hamcrest", "hamcrest-core", "1.3"));
        assertEquals(list, DependencyResolver.resolveDependencies(target, TESTING_TEMP_DIR));
        assertEquals(list, DependencyResolver.resolveArtifact("junit", "junit", "4.13.2", TESTING_TEMP_DIR));
        FileUtils.deleteDirectory(new File(TESTING_TEMP_DIR));
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
    public void testHandleInputMissingGroupId() throws Exception {
        assertThrows(ArtifactResolveException.class, () -> DependencyResolver.handleInputArtifact("", "42", "1.0.2"));
    }

    @Test
    /**
     * Test for validating the input artifact data
     */
    public void testHandleInputMissingArtifactId() throws Exception {
        assertThrows(ArtifactResolveException.class, () -> DependencyResolver.handleInputArtifact("42", "", "1.0.2"));
    }

    @Test
    /**
     * Test for validating the input artifact data
     */
    public void testHandleInputArtifactMissingVersion() throws Exception {
        assertThrows(ArtifactResolveException.class, () -> DependencyResolver.handleInputArtifact("42", "42", ""));
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of spring boot starter web matches the expected list.
     */
    public void testSpringBootTransitiveDependencies() throws ArtifactResolveException, IOException {
        Artifact springBoot = new Artifact("org.springframework.boot", "spring-boot-starter-web", "2.2.6.RELEASE");
        verifyArtifactDependencies(springBoot, "SpringBootTransitiveDeps.txt");
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of guava matches the expected list.
     */
    public void testGoogleGuavaTransitiveDependencies() throws ArtifactResolveException, IOException {
        Artifact guava = new Artifact("com.google.guava", "guava", "31.0.1-jre");
        verifyArtifactDependencies(guava, "GuavaTransitiveDeps.txt");
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of Tomcat Embed Websocket matches the expected list.
     */
    public void testTomcatEmbedWebsocketTransitiveDependencies() throws ArtifactResolveException, IOException {
        Artifact websocket = new Artifact("org.apache.tomcat.embed", "tomcat-embed-websocket", "10.1.0-M8");
        verifyArtifactDependencies(websocket, "WebsocketTransitiveDeps.txt");
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of Spring Boot Starter matches the expected list.
     */
    public void testSpringBootStarterTransitiveDependencies() throws ArtifactResolveException, IOException {
        Artifact springBootStarter = new Artifact("org.springframework.boot", "spring-boot-starter", "2.2.6.RELEASE");
        verifyArtifactDependencies(springBootStarter, "SpringBootStarterTransitiveDeps.txt");
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of Mockito matches the expected list.
     */
    public void testMockitoTransitiveDependencies() throws ArtifactResolveException, IOException {
        Artifact mockito = new Artifact("org.mockito", "mockito-core", "4.2.0");
        verifyArtifactDependencies(mockito, "MockitoTransitiveDeps.txt");
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of Jackson matches the expected list.
     */
    public void testJacksonTransitiveDependencies() throws ArtifactResolveException, IOException {
        Artifact jackson = new Artifact("com.fasterxml.jackson.core", "jackson-databind", "2.13.0");
        verifyArtifactDependencies(jackson, "JacksonTransitiveDeps.txt");
    }

    /**
     * Helper method to verify if the transitive artifacts collected for a given artifact are expected.
     *
     * @param artifact As an input artifact for which we want to collect all transitive dependencies.
     * @param filename Target test file (must reside test/java/resources)
     * @throws ArtifactResolveException Throw an exception if it fails to resolve the dependencies.
     */
    private void verifyArtifactDependencies(Artifact artifact, String filename) throws ArtifactResolveException, IOException {

        // Collect transitive hull
        List<Artifact> dependencies = DependencyResolver.resolveArtifact(artifact, TESTING_TEMP_DIR);

        // Verify the collected dependencies match the expected set (imported from text file)
        Set<Artifact> expected = readTestFile(filename);

        // Verify amount
        assertTrue("Expected dependencies: " + expected.size() + ", found: " + dependencies.size(), dependencies.size() == expected.size());

        // Verify exact match, every retrieved artefact must be in list of expected artefacts
        for (Artifact dependency : dependencies) {
            assertTrue("Unit test downloaded " + dependency + ", but this artifact does not match an entry in the expected list.", expected.contains(dependency));
        }

        // Delete the test download folder
        FileUtils.deleteDirectory(new File(TESTING_TEMP_DIR));
    }

    /**
     * This helper method will read the dependencies provided by MVN and generate a list of Artifact object. Files with
     * expected dependency sets can be used to verify the correct transitive hull constructed for test artefacts.
     *
     * @param transitiveDependencyFileName The name of the text file, listing the expected transitive maven
     *                                     dependencies
     * @return Expected dependency list as a set of artefacts
     */
    public Set<Artifact> readTestFile(String transitiveDependencyFileName) throws IOException {
        Set<Artifact> expected = new HashSet<>();

        // resolve filename to location in test resource folder:
        String[] allDependencies = IOUtils.toString(
                // Note: Usage of slash here instead of File.pathseparator is intentional. The commons IO knows how to handle it.
                this.getClass().getResourceAsStream("/" + transitiveDependencyFileName),
                "UTF-8").split(System.getProperty("line.separator"));

        for (String dependency : allDependencies) {
            expected.add(parseArtifact(dependency));
        }

        // Return set with all artifacts extracted of file.
        return expected;
    }

    /**
     * This helper method will parse one string-encoded line representing an artifact (with groupid and version) into an
     * artifact object.
     *
     * @param strLine target line. Must be of syntax: "[group.id]:[artifact.id]:[scope]:[version]"
     * @return return a parsed Artifact object.
     */
    private Artifact parseArtifact(String strLine) {

        // In case that curr line is the first line
        String[] substrings = strLine.split(":");
        String groupId = substrings[0];
        String artifactId = substrings[1];
        String version = substrings[3];

        // Create Artifact version based on groupId, version, artifactId
        return new Artifact(groupId, artifactId, version);
    }
}
