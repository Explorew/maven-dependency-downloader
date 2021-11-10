public class ArtifactResolveException extends Exception {
    /**
     * Custom exception that can be thrown if anything goes wrong during artifact resolving.
     * @param cause reason for exception
     * @author Yizhong Ding
     */
    public ArtifactResolveException(String cause)
    {
        super(cause);
    }
}
