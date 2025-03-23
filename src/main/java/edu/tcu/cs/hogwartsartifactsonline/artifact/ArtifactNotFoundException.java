package edu.tcu.cs.hogwartsartifactsonline.artifact;

public class ArtifactNotFoundException extends RuntimeException {
    public ArtifactNotFoundException(String artifactId) {
        super("Could not find artifact with Id " + artifactId + ":(");

    }
}
