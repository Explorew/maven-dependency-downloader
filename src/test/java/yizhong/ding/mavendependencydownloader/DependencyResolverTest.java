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


    // ToDo: write a test that checks for these exact artefacts:
    /*
    [INFO]    +- org.springframework.boot:spring-boot-starter:jar:2.2.6.RELEASE:compile
[INFO]    |  +- org.springframework.boot:spring-boot:jar:2.2.6.RELEASE:compile
[INFO]    |  +- org.springframework.boot:spring-boot-autoconfigure:jar:2.2.6.RELEASE:compile
[INFO]    |  +- org.springframework.boot:spring-boot-starter-logging:jar:2.2.6.RELEASE:compile
[INFO]    |  |  +- ch.qos.logback:logback-classic:jar:1.2.3:compile
[INFO]    |  |  |  +- ch.qos.logback:logback-core:jar:1.2.3:compile
[INFO]    |  |  |  \- org.slf4j:slf4j-api:jar:1.7.25:compile
[INFO]    |  |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.12.1:compile
[INFO]    |  |  |  \- org.apache.logging.log4j:log4j-api:jar:2.12.1:compile
[INFO]    |  |  \- org.slf4j:jul-to-slf4j:jar:1.7.30:compile
[INFO]    |  +- jakarta.annotation:jakarta.annotation-api:jar:1.3.5:compile
[INFO]    |  +- org.springframework:spring-core:jar:5.2.5.RELEASE:compile
[INFO]    |  |  \- org.springframework:spring-jcl:jar:5.2.5.RELEASE:compile
[INFO]    |  \- org.yaml:snakeyaml:jar:1.25:runtime
[INFO]    +- org.springframework.boot:spring-boot-starter-json:jar:2.2.6.RELEASE:compile
[INFO]    |  +- com.fasterxml.jackson.core:jackson-databind:jar:2.10.3:compile
[INFO]    |  |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.10.3:compile
[INFO]    |  |  \- com.fasterxml.jackson.core:jackson-core:jar:2.10.3:compile
[INFO]    |  +- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:jar:2.10.3:compile
[INFO]    |  +- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:jar:2.10.3:compile
[INFO]    |  \- com.fasterxml.jackson.module:jackson-module-parameter-names:jar:2.10.3:compile
[INFO]    +- org.springframework.boot:spring-boot-starter-tomcat:jar:2.2.6.RELEASE:compile
[INFO]    |  +- org.apache.tomcat.embed:tomcat-embed-core:jar:9.0.33:compile
[INFO]    |  +- org.apache.tomcat.embed:tomcat-embed-el:jar:9.0.33:compile
[INFO]    |  \- org.apache.tomcat.embed:tomcat-embed-websocket:jar:9.0.33:compile
[INFO]    +- org.springframework.boot:spring-boot-starter-validation:jar:2.2.6.RELEASE:compile
[INFO]    |  +- jakarta.validation:jakarta.validation-api:jar:2.0.2:compile
[INFO]    |  \- org.hibernate.validator:hibernate-validator:jar:6.0.18.Final:compile
[INFO]    |     +- org.jboss.logging:jboss-logging:jar:3.3.2.Final:compile
[INFO]    |     \- com.fasterxml:classmate:jar:1.3.4:compile
[INFO]    +- org.springframework:spring-web:jar:5.2.5.RELEASE:compile
[INFO]    |  \- org.springframework:spring-beans:jar:5.2.5.RELEASE:compile
[INFO]    \- org.springframework:spring-webmvc:jar:5.2.5.RELEASE:compile
[INFO]       +- org.springframework:spring-aop:jar:5.2.5.RELEASE:compile
[INFO]       +- org.springframework:spring-context:jar:5.2.5.RELEASE:compile
[INFO]       \- org.springframework:spring-expression:jar:5.2.5.RELEASE:compile
     */

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
     * Test if the resolved transitive dependency hull of guava matches the expected list.
     */
    public void testGoogleGuavaTransitiveAmount() throws ArtifactResolveException {
        Artifact guava = new Artifact("com.google.guava", "guava", "31.0.1-jre");
        verifyArtifactDependencyAmount(guava, 7);
    }

    @Test
    /**
     * Test if the resolved transitive dependency hull of spring boot starter matches the expected list.
     */
    public void testSpringBootStarterTransitiveAmount() throws ArtifactResolveException {
        Artifact springBootStarter = new Artifact("org.springframework.boot", "spring-boot-starter", "2.2.6.RELEASE");
        verifyArtifactDependencyAmount(springBootStarter, 18);
    }

    @Test
    /**
     * Test if the resolved transitive dependency hull of mockito matches the expected list.
     */
    public void testMockitoTransitiveAmount() throws ArtifactResolveException {
        Artifact mockito = new Artifact("org.mockito", "mockito-core", "4.1.0");
        verifyArtifactDependencyAmount(mockito, 4);
    }

    @Test
    /**
     * Test if the resolved transitive dependency hull of jackson matches the expected list.
     */
    public void testJacksonTransitiveAmount5() throws ArtifactResolveException {
        Artifact jackson = new Artifact("com.fasterxml.jackson.core", "jackson-databind", "2.13.0");
        verifyArtifactDependencyAmount(jackson, 3);
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
}
