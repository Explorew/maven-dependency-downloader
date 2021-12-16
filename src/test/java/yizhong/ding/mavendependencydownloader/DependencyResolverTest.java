package yizhong.ding.mavendependencydownloader;

import org.junit.Test;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void testResolveDependencies() throws ArtifactResolveException {
        Artifact target = new Artifact("junit", "junit", "4.13.2");
        List<Artifact> list = new ArrayList<>();
        list.add(new Artifact("junit", "junit", "4.13.2"));
        list.add(new Artifact("org.hamcrest", "hamcrest-core", "1.3"));
        assertEquals(list, DependencyResolver.resolveDependencies(target, "./temp"));
        assertEquals(list, DependencyResolver.resolveArtifact("junit", "junit", "4.13.2", "./temp"));
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
    public void testSpringBootTransitiveDependencies() throws ArtifactResolveException {
        Artifact springBoot = new Artifact("org.springframework.boot", "spring-boot-starter-web", "2.2.6.RELEASE");
        verifyArtifactDependencyAmount(springBoot, "src/test/java/resources/SpringBootTest.txt");
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of guava matches the expected list.
     */
    public void testGoogleGuavaTransitiveDependencies() throws ArtifactResolveException {
        Artifact guava = new Artifact("com.google.guava", "guava", "31.0.1-jre");
        verifyArtifactDependencyAmount(guava, "src/test/java/resources/Guava.txt");
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of Tomcat Embed Websocket matches the expected list.
     */
    public void testTomcatEmbedWebsocketTransitiveDependencies() throws ArtifactResolveException {
        Artifact websocket = new Artifact("org.apache.tomcat.embed", "tomcat-embed-websocket", "10.1.0-M8");
        verifyArtifactDependencyAmount(websocket, "src/test/java/resources/Websocket.txt");
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of Spring Boot Starter matches the expected list.
     */
    public void testSpringBootStarterTransitiveDependencies() throws ArtifactResolveException {
        Artifact springBootStarter = new Artifact("org.springframework.boot", "spring-boot-starter", "2.2.6.RELEASE");
        verifyArtifactDependencyAmount(springBootStarter, "src/test/java/resources/SpringBootStarter.txt");
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of Mockito matches the expected list.
     */
    public void testMockitoTransitiveDependencies() throws ArtifactResolveException {
        Artifact mockito = new Artifact("org.mockito", "mockito-core", "4.2.0");
        verifyArtifactDependencyAmount(mockito, "src/test/java/resources/Mockito.txt");
    }

    @Test
    /**
     * Test if the resolved transitive dependency list of Jackson matches the expected list.
     */
    public void testJacksonTransitiveDependencies() throws ArtifactResolveException {
        Artifact jackson = new Artifact("com.fasterxml.jackson.core", "jackson-databind", "2.13.0");
        verifyArtifactDependencyAmount(jackson, "src/test/java/resources/Jackson.txt");
    }

    /**
     * Helper method to verify if the transitive artifacts collected for a given artifact are expected.
     *
     * @param artifact As an input artifact for which we want to collect all transitive dependencies.
     * @param filename Target test file.
     * @throws ArtifactResolveException Throw an exception if it fails to resolve the dependencies.
     */
    private void verifyArtifactDependencyAmount(Artifact artifact, String filename) throws ArtifactResolveException {
        // Collect transitive hull
        List<Artifact> dependencies = DependencyResolver.resolveArtifact(artifact, TESTING_TEMP_DIR);

        // Verify the dependencies of target Artifact are correct
        Set<Artifact> expected = readTestFile(filename);
        boolean res = true;
        for (Artifact dependency : dependencies) {
            if(!expected.contains(dependency) || !dependency.equals(Util.getFromSet(expected, dependency))){
                Logger.error("Incorrect Artifact! Expected: " + Util.getFromSet(expected, dependency) + ". Actually: " + dependency);
                res = false;
            }
        }

        // Delete the test download folder
        File folder = new File(TESTING_TEMP_DIR);
        deleteFolder(folder);

        // Verify amount of dependencies in target folder
        if(dependencies.size() != expected.size()) res = false;
        assertEquals(res, true);
    }

    /**
     * This helper method will read the dependencies provided by MVN and generate a list of Artifact object.
     * @param filename The name of the text file.
     * @return  Expected dependency list.
     */
    public Set<Artifact> readTestFile(String filename) {
        Set<Artifact> expected = new HashSet<>();
        try {
            FileInputStream stream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String strLine;
            boolean firstLine = true;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                Artifact artifact = parseArtifact(strLine, firstLine);
                expected.add(artifact);
                firstLine = false;
            }
            stream.close();
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
        return expected;
    }

    /**
     * This helper method will parse one line of the dependency list.
     * @param strLine target line.
     * @param firstLine a button indicating if this is the first line.
     * @return return a parsed Artifact object.
     */
    private Artifact parseArtifact(String strLine, boolean firstLine) {
        // In case that curr line is the first line
        int startIndex = firstLine ?  strLine.indexOf("]") + 1 : strLine.indexOf("-") + 1;
        String[] substrings = strLine.substring(startIndex).trim().split(":");
        String groupId = substrings[0];
        String artifactId = substrings[1];
        String version = substrings[3];
        return new Artifact(groupId, artifactId, version);
    }

    /**
     * Helper method to delete the folder after test
     *
     * @param folder target folder
     */
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}
