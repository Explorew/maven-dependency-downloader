public class ArtifactResolveException extends RuntimeException {

    /**
     * custom exception that can be thrown if anything goes wrong during artifact resolving.
     * @param cause reason for excpetion
     */
    public ArtifactResolveException(String cause)
    {
        super(cause);
    }
}
