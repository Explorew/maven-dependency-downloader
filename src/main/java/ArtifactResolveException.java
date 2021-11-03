public class ArtifactResolveException extends Exception {
    /**
     * @author Yizhong Ding
     * Custom exception that can be thrown if anything goes wrong during artifact resolving.
     * @param cause reason for exception
     */
    public ArtifactResolveException(String cause)
    {
        super(cause);
    }
}
