import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DependencyResolverTest {

    @Test
    public void testResolveDependencies() {
        Artifact target = new Artifact("junit", "junit", "4.13.2");
        List<Artifact> list = new ArrayList<>();
        list.add(new Artifact("junit", "junit", "4.13.2"));
        list.add(new Artifact("org.hamcrest", "hamcrest-library", "1.3"));
        list.add(new Artifact("org.hamcrest", "hamcrest-core", "1.3"));
        assertEquals(list, DependencyResolver.resolveDependencies(target, null));
        File folder = new File("./temp");
        folder.delete();
    }

    @Test
    public void testResolveDependencies2() {
        Artifact target = new Artifact("junit", "junit", "4.13.2");
        List<Artifact> list = new ArrayList<>();
        list.add(new Artifact("junit", "junit", "4.13.2"));
        list.add(new Artifact("org.hamcrest", "hamcrest-library", "1.3"));
        assertNotEquals(list, DependencyResolver.resolveDependencies(target, null));
        File folder = new File("./temp");
        folder.delete();
    }
}
