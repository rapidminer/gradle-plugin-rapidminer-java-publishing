package com.rapidminer.gradle

/**
 * Extension for the java-publishing plugin which allows to define remote Maven repository and snapshot/release publications.
 *
 * @author Nils Woehler
 *
 */
class PublishingExtension {
    String baseUrl = 'https://maven.rapidminer.com/nexus/content/repositories/'
    Credentials credentials

    ArtifactConfig releases = new ArtifactConfig(publishTests: true, publishSources: false, publishJavaDoc: true, repo: 'releases')
    ArtifactConfig snapshots = new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'snapshots')

    /**
     * Delegates the provided Closure to the ArtifactConfig.
     */
    def snapshots(Closure closure){
        snapshots.apply(closure)
    }

    /**
     * Delegates the provided Closure to the ArtifactConfig.
     */
    def releases(Closure closure){
        releases.apply(closure)
    }

    /**
     * Delegates the provided Closure to the ArtifactConfig.
     */
    def credentials(Closure closure){
        if(!credentials){
            credentials = new Credentials()
        }
        credentials.apply(closure)
    }
}
