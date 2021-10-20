import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Artifact {
    private String groupId;
    private String artifactId;
    private String version;
    private List<Artifact> dependencies;
    
    public Artifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.dependencies = new ArrayList<>();
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public List<Artifact> getDependencies() {
        return dependencies;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {this.artifactId = artifactId;}

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDependencies(List<Artifact> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact artifact = (Artifact) o;
        return Objects.equals(groupId, artifact.groupId) &&
                Objects.equals(artifactId, artifact.artifactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }

    @Override
    public String toString() {
        return groupId + '-' + artifactId + '-' + version;
    }
}
