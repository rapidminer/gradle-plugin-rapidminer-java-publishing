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
 * Test for internal release publishing.
 *
 * @author Nils Woehler
 *
 */
class JavaPublishingReleaseIntegrationSpec extends AbstractJavaPublishingIntegrationSpec {

    static final VERSION = '0.1.1'

    def 'Test default release publishing'() {
        setup:
        setupProject(VERSION)

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: true, repo: 'releases'))
    }

    def 'Test source release publishing'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            releases {
                publishSources = false
            }
        }
        """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: false, publishJavaDoc: true, repo: 'releases'))
    }

    def 'Test release publishing without javadoc'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            releases {
                publishJavaDoc = false
            }
        }
        """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'releases'))
    }

    def 'Test release publishing without tests'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            releases {
                publishTests = false
            }
        }
        """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: false, publishSources: true, publishJavaDoc: true, repo: 'releases'))
    }

    def 'Test release publishing with different repo'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            releases {
                repo = 'public-releases'
            }
        }
        """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: true, repo: 'public-releases'))
    }

    def 'Test snapshot publishing with added artifact'() {
        setup:
        setupProject(VERSION)
        buildFile  << """

        task utilJar(type: Jar) {
            from(sourceSets.main.output)
        }

        publication {
            releases {
                artifact utilJar { classifier 'utils' }
            }
        }
        """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        result.standardOutput.contains('No credentials defined for publication extension. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: true, repo: 'releases'), 'utils')
    }

    @Override
    String getApplyPluginString() {
        return applyPlugin(RapidMinerJavaPublishingPlugin)
    }

}
