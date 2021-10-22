import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DependencyResolverTest {

    @Test
    //TODO: test download method
    public void testDownload() {

    }
    @Test
    //TODO: change test download method
    public void testHandleInput() throws Exception {
        assertEquals((new Artifact("com.jolira", "guice", "3.0.0")), DependencyResolver.handleInput());
    }
}
