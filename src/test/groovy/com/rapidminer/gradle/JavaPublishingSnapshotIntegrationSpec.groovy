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
