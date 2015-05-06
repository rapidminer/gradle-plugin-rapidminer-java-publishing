package com.rapidminer.gradle

/**
 * POGO that allows to define the repository and artifacts of a project release.
 *
 * @author Nils Woehler
 *
 */
class ArtifactConfig {

    String repo
    boolean publishTests
    boolean publishSources
    boolean publishJavaDoc

    /**
     * Applies the provided closure (e.g. to configure class fields).
     */
    def apply(Closure closure) {
        closure.delegate = this
        closure()
    }
}
