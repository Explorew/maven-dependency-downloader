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

    private final String TESTING_TEMP_DIR = "./temp";


    @Test
    /**
     * Test for downloading the dependency of junit
     */
    public void testResolveDependencies() {
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

        /*
+- org.springframework.boot:spring-boot-starter:jar:2.2.6.RELEASE:compile
|  +- org.springframework.boot:spring-boot:jar:2.2.6.RELEASE:compile
|  +- org.springframework.boot:spring-boot-autoconfigure:jar:2.2.6.RELEASE:compile
|  +- org.springframework.boot:spring-boot-starter-logging:jar:2.2.6.RELEASE:compile
|  |  +- ch.qos.logback:logback-classic:jar:1.2.3:compile
|  |  |  +- ch.qos.logback:logback-core:jar:1.2.3:compile
|  |  |  \- org.slf4j:slf4j-api:jar:1.7.25:compile
|  |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.12.1:compile
|  |  |  \- org.apache.logging.log4j:log4j-api:jar:2.12.1:compile
|  |  \- org.slf4j:jul-to-slf4j:jar:1.7.30:compile
|  +- jakarta.annotation:jakarta.annotation-api:jar:1.3.5:compile
|  +- org.springframework:spring-core:jar:5.2.5.RELEASE:compile
|  |  \- org.springframework:spring-jcl:jar:5.2.5.RELEASE:compile
|  \- org.yaml:snakeyaml:jar:1.25:runtime
+- org.springframework.boot:spring-boot-starter-json:jar:2.2.6.RELEASE:compile
|  +- com.fasterxml.jackson.core:jackson-databind:jar:2.10.3:compile
|  |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.10.3:compile
|  |  \- com.fasterxml.jackson.core:jackson-core:jar:2.10.3:compile
|  +- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:jar:2.10.3:compile
|  +- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:jar:2.10.3:compile
|  \- com.fasterxml.jackson.module:jackson-module-parameter-names:jar:2.10.3:compile
+- org.springframework.boot:spring-boot-starter-tomcat:jar:2.2.6.RELEASE:compile
|  +- org.apache.tomcat.embed:tomcat-embed-core:jar:9.0.33:compile
|  +- org.apache.tomcat.embed:tomcat-embed-el:jar:9.0.33:compile
|  \- org.apache.tomcat.embed:tomcat-embed-websocket:jar:9.0.33:compile
+- org.springframework.boot:spring-boot-starter-validation:jar:2.2.6.RELEASE:compile
|  +- jakarta.validation:jakarta.validation-api:jar:2.0.2:compile
|  \- org.hibernate.validator:hibernate-validator:jar:6.0.18.Final:compile
|     +- org.jboss.logging:jboss-logging:jar:3.3.2.Final:compile
|     \- com.fasterxml:classmate:jar:1.3.4:compile
+- org.springframework:spring-web:jar:5.2.5.RELEASE:compile
|  \- org.springframework:spring-beans:jar:5.2.5.RELEASE:compile
\- org.springframework:spring-webmvc:jar:5.2.5.RELEASE:compile
   +- org.springframework:spring-aop:jar:5.2.5.RELEASE:compile
   +- org.springframework:spring-context:jar:5.2.5.RELEASE:compile
   \- org.springframework:spring-expression:jar:5.2.5.RELEASE:compile
         */

        Artifact springBoot = new Artifact("org.springframework.boot", "spring-boot-starter", "2.2.6.RELEASE");
        verifyArtifactDependencyAmount(springBoot, 37);
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
        assertEquals(expectedTransitiveDependencyAmount, folder.list().length);
        folder.delete();
    }
}
