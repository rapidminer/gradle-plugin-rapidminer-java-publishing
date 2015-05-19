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

import nebula.test.functional.ExecutionResult

/**
 * Test for open-source release publishing.
 *
 * @author Nils Woehler
 *
 */
class JavaOpenSourcePublishingIntegrationSpec extends AbstractJavaPublishingIntegrationSpec {

    def 'Test default open-source release publishing'() {
        setup:
        setupProject('0.1.1')

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        checkMavenRepo('0.1.1', new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: true, repo: 'releases-public'))
    }

    def 'Test default open-source snapshot publishing'() {
        setup:
        setupProject('0.1.1-SNAPSHOT')

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        checkMavenRepo('0.1.1-SNAPSHOT', new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'snapshots'))
    }

    @Override
    String getApplyPluginString() {
        return applyPlugin(RapidMinerJavaOpenSourcePublishingPlugin)
    }
}
