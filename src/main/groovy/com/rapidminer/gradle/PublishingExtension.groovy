/*
 * Copyright 2013-2015 RapidMiner GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rapidminer.gradle

/**
 * Extension for the java-publishing plugin which allows to define remote Maven repository and snapshot/release publications.
 *
 * @author Nils Woehler
 *
 */
class PublishingExtension {

    Credentials credentials
    String baseUrl = 'https://maven.rapidminer.com/content/repositories/'
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
