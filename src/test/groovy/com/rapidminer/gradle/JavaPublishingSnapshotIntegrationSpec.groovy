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
 * Test for internal snapshot publishing.
 *
 * @author Nils Woehler
 *
 */
class JavaPublishingSnapshotIntegrationSpec extends AbstractJavaPublishingIntegrationSpec {

    static final VERSION = '0.1.1-SNAPSHOT'

    def 'Test default snapshot publishing'() {
        setup:
        setupProject(VERSION)

        when:
        runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
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
        runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
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
        runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
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
        runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
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
        def result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        def config =  new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'public-snapshots')
        result.standardOutput.contains('No credentials defined for publication extension. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, config)
        checkPOMContent(VERSION, config, PublishingExtension.LicenseTypes.RM_EULA)
    }

    def 'Test snapshot publishing with added artifact'() {
        setup:
        setupProject(VERSION)
        buildFile  << """

        task utilJar(type: Jar) {
            from(sourceSets.main.output)
        }

        publication {
            snapshots {
                artifact utilJar { classifier 'utils' }
            }
        }
        """.stripIndent()

        when:
        def result = runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        def config = new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'snapshots')
        result.standardOutput.contains('No credentials defined for publication extension. Looking for \'nexusUser\' and \'nexusPassword\' project properties.')
        checkMavenRepo(VERSION, config, 'utils')
        checkPOMContent(VERSION, config, PublishingExtension.LicenseTypes.RM_EULA)
    }

    def 'Test snapshot publishing with different vendor and URL'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            vendor = 'Another Company'
            vendorUrl = 'www.anothercompany.com'
        }
        """.stripIndent()

        when:
        runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        def config = new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'snapshots')
        checkMavenRepo(VERSION, config)
        checkPOMContent(VERSION, config, 'Another Company', 'www.anothercompany.com', PublishingExtension.LicenseTypes.RM_EULA)
    }

    def 'Test snapshot publishing with empty vendor and URL'() {
        setup:
        setupProject(VERSION)
        buildFile  << """
        publication {
            vendor ''
            vendorUrl ''
        }
        """.stripIndent()

        when:
        runTasksSuccessfully('publishJarPublicationToMavenRepository')

        then:
        def config = new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'snapshots')
        checkMavenRepo(VERSION, config)
        checkPOMContent(VERSION, config, '', '', PublishingExtension.LicenseTypes.RM_EULA)
    }

    @Override
    String getApplyPluginString() {
        return applyPlugin(RapidMinerJavaPublishingPlugin)
    }

}
