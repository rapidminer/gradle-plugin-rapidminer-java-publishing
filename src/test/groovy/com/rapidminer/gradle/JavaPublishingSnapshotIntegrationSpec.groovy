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
 * Test for release publishing.
 *
 * @author Nils Woehler
 *
 */
class JavaPublishingSnapshotIntegrationSpec extends JavaPublishingIntegrationSpec {

    static final VERSION = '0.1.1-SNAPSHOT'

    def 'Test default snapshot publishing'() {
        setup:
        setupProject(VERSION)

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        result.standardOutput.contains('No credentials defined. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'snapshots'))
    }

    def 'Test javadoc snapshot publishing'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            snapshots {
                publishJavaDoc = true
            }
        }
        """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        result.standardOutput.contains('No credentials defined. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: true, repo: 'snapshots'))
    }

    def 'Test snapshot publishing without sources'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            snapshots {
                publishSources = false
            }
        }
        """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        result.standardOutput.contains('No credentials defined. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: false, publishJavaDoc: false, repo: 'snapshots'))
    }

    def 'Test snapshot publishing without tests'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            snapshots {
                publishTests = false
            }
        }
        """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        result.standardOutput.contains('No credentials defined. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: false, publishSources: true, publishJavaDoc: false, repo: 'snapshots'))
    }

    def 'Test snapshot publishing with different repo'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            snapshots {
                repo = 'public-snapshots'
            }
        }
        """.stripIndent()

        when:
        ExecutionResult result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        result.standardOutput.contains('No credentials defined. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'public-snapshots'))
    }

}
