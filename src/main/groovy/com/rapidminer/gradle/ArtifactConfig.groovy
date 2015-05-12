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
