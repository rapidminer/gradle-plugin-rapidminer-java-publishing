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

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

/**
 * Test for release publishing.
 *
 * @author Nils Woehler
 *
 */
class JavaPublishingReleaseIntegrationSpec extends JavaPublishingIntegrationSpec {

    static final VERSION = '0.1.1'

    def 'Test default release publishing'() {
        setup:
        setupProject(VERSION)

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        result.standardOutput.contains('No credentials defined. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: false, publishJavaDoc: true, repo: 'releases'))
    }

    def 'Test source release publishing'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            releases {
                publishSources = true
            }
        }
        """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        result.standardOutput.contains('No credentials defined. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: true, repo: 'releases'))
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
        result.standardOutput.contains('No credentials defined. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: false, publishJavaDoc: false, repo: 'releases'))
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
        result.standardOutput.contains('No credentials defined. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: false, publishSources: false, publishJavaDoc: true, repo: 'releases'))
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
        result.standardOutput.contains('No credentials defined. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: false, publishJavaDoc: true, repo: 'public-releases'))
    }

}
