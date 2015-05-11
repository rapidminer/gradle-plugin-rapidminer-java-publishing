package com.rapidminer.gradle

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

/**
 * Abstract super class for all JavaPublishing integration specs
 *
 * @author Nils Woehler
 *
 */
abstract class JavaPublishingIntegrationSpec extends IntegrationSpec {

    protected void checkMavenRepo(String version, ArtifactConfig artifactConfig) {
        File repoDir = new File(projectDir, "testRepo/${artifactConfig.repo}/com/rapidminer/${moduleName}/${version}/")
        assert repoDir.exists()

        def baseName
        if(version.endsWith('-SNAPSHOT')){

            // read timestamp from Maven metadata
            def mavenMDFile = new File(repoDir, 'maven-metadata.xml')
            assert mavenMDFile.exists()
            def timestamp = new XmlSlurper().parse(mavenMDFile).versioning.snapshot.timestamp
            def mavenVersion = version.replace('-SNAPSHOT', '')
            baseName = "${moduleName}-${mavenVersion}-${timestamp}-1"
        } else {
            baseName =  "${moduleName}-${version}"
        }

        assert new File(repoDir, "${baseName}.jar").exists()
        assert new File(repoDir, "${baseName}.pom").exists()
        assert new File(repoDir, "${baseName}-sources.jar").exists() == artifactConfig.publishSources
        assert new File(repoDir, "${baseName}-javadoc.jar").exists() == artifactConfig.publishJavaDoc
        assert new File(repoDir, "${baseName}-test.jar").exists() == artifactConfig.publishTests
    }

    protected void setupProject(String version) {
        writeHelloWorld('com.rapidminer')
        copyResources 'SimpleTestClass.java', 'src/main/java/com/rapidminer/SimpleTestClass.java'
        copyResources 'SimpleTestCaseClass.java', 'src/test/java/com/rapidminer/SimpleTestCaseClass.java'
        buildFile << """
            apply plugin: 'java'
            ${applyPlugin(RapidMinerJavaPublishingPlugin)}

            version '$version'
            group = 'com.rapidminer'

            dependencies {
                compile 'junit:junit:4.12'
            }

            publication {
                baseUrl = 'testRepo'
            }
        """.stripIndent()

    }
}
