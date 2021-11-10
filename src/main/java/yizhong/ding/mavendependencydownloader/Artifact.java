package yizhong.ding.mavendependencydownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Artifact class represents the logical artifact coordinates of maven artifact.
 *  It contains the coordinates (groupId, artifactId, and version) of an artifact.
 * @author Yizhong Ding
 */
public class Artifact {
    private String groupId;
    private String artifactId;
    private String version;
    private List<Artifact> dependencies;

    /**
     * Constructor of Artifact class taking an Artifact coordinates as parameters.
     * @param groupId the group id of the Artifact
     * @param artifactId the artifact id of the Artifact
     * @param version the version of the Artifact
     */
    public Artifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.dependencies = new ArrayList<>();
    }

    /**
     * Getter for group id
     * @return returns the group id of this Artifact
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Getter for artifact id
     * @return returns the artifact id of this Artifact
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Getter for version
     * @return returns the version string of this Artifact
     */
    public String getVersion() {
        return version;
    }

    /**
     * Getter for dependencies
     * @return returns the dependencies (a List of Artifacts) of this Artifact
     */
    public List<Artifact> getDependencies() {
        return dependencies;
    }

    /**
     * Setter for group id
     * @param groupId new group id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Setter for artifactId
     * @param artifactId new artifact id
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Setter for version
     * @param version new version string
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Setter for dependencies
     * @param dependencies new dependencies List
     */
    public void setDependencies(List<Artifact> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    /**
     * Equals methods compares the value of two Artifacts.
     *  Note: The override equals method only checks if values of groupId
     *  and artifactId of two Artifacts are equal.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact artifact = (Artifact) o;
        return Objects.equals(groupId, artifact.groupId) &&
                Objects.equals(artifactId, artifact.artifactId);
    }

    @Override
    /**
     * Override hashCode method.
     */
    public int hashCode() {
        return Objects.hash(groupId, artifactId);
    }

    @Override
    /**
     * Override toString method.
     *  Output string would join groupId, artifactId, and version with "-" sign.
     */
    public String toString() {
        return groupId + '-' + artifactId + '-' + version;
    }
}
