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
 * Test for public but closed-source release publishing.
 *
 * @author Nils Woehler
 *
 */
class JavaPublicPublishingIntegrationSpec extends AbstractJavaPublishingIntegrationSpec {

    def 'Test default public release publishing'() {
        def version = '0.1.1'
        setup:
        setupProject(version)

        when:
        runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        def config = new ArtifactConfig(publishTests: true, publishSources: false, publishJavaDoc: true, repo: 'releases-public')
        checkMavenRepo(version, config)
        checkPOMContent(version, config, PublishingExtension.LicenseTypes.RM_EULA)
    }

    def 'Test default public snapshot publishing'() {
        def version = '0.1.1-SNAPSHOT'
        setup:
        setupProject(version)

        when:
        runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        def config = new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'snapshots')
        checkMavenRepo(version, config)
        checkPOMContent(version, config, PublishingExtension.LicenseTypes.RM_EULA)
    }

    @Override
    String getApplyPluginString() {
        return applyPlugin(RapidMinerJavaPublicPublishingPlugin)
    }
}
