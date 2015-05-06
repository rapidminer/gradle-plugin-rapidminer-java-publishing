/*
 * Copyright 2013-2014 RapidMiner GmbH.
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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExcludeRule
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.maven.MavenPublication

/**
 * The java-publishing plugin that uses the 'maven-publish' plugin to preconfigure RapidMiner specific project publications.
 *
 * @author Nils Woehler
 *
 */
class RapidMinerJavaPublishingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        PublishingExtension extension = project.extensions.create('publication', PublishingExtension)

        project.configure(project) {
            apply plugin: 'maven-publish'

            // create and configure sourceJar task
            tasks.create(name: 'sourceJar', type: org.gradle.api.tasks.bundling.Jar, dependsOn: classes)
            sourceJar {
                from sourceSets.main.allSource
                classifier = 'sources'
            }

            // create and configure javadocJar task
            tasks.create(name: 'javadocJar', type: org.gradle.api.tasks.bundling.Jar, dependsOn: javadoc)
            javadocJar {
                classifier = 'javadoc'
                from javadoc.destinationDir
            }

            /*
             * Configure an artifact which contains the classes from the test source set
             */
            configurations { testArtifacts.extendsFrom testRuntime }

            // create and configure testJar tasl
            tasks.create(name: 'testJar', type: org.gradle.api.tasks.bundling.Jar) {
                classifier 'test'
                from sourceSets.test.output
            }

            // specify artifacts
            artifacts {
                jar
                sourceJar
                javadocJar
                testArtifacts testJar
            }

            afterEvaluate {
                def isSnapshot = project.version.endsWith('-SNAPSHOT')

                // In case no credentials are defined...
                if (!extension.credentials) {
                    project.logger.info "No credentials defined. Looking for 'nexusUser' and 'nexusPassword' project properties."

                    // .. check if nexusUser and nexusPassword project properties are set
                    if (!project.hasProperty('nexusUser')) {
                        project.logger.info "Project property 'nexusUser' not found. Remote Maven repository will not be configured!"
                    } else {
                        if (!project.hasProperty('nexusPassword')) {
                            project.logger.info "Project property 'nexusPassword' not found. Remote Maven repository will not be configured!"
                        } else {
                            extension.credentials = new Credentials(username: project.nexusUser, password: project.nexusPassword)
                        }
                    }
                }

                publishing {
                    if(!extension.baseUrl){
                        project.logger.info 'No repository baseUrl defined. Skipping definition of remote Maven repository.'
                    } else if(!extension.credentials) {
                        project.logger.info 'No repository credentials defined. Skipping definition of remote Maven repository.'
                    } else {
                        repositories {
                            maven {
                                def repo = isSnapshot ? extension.snapshots.repo : extension.releases.repo
                                url(extension.baseUrl.endsWith('/') ?: extension.baseUrl + '/') + repo
                                credentials {
                                    username = extension.credentials.username
                                    password = extension.credentials.password
                                }
                            }
                        }
                    }

                    publications {
                        "${plugins.hasPlugin('war') ? 'war' : 'jar'}"(MavenPublication) {
                            if (plugins.hasPlugin('war')) {
                                from components.web
                            } else {
                                from components.java
                            }

                            if (isSnapshot) {
                                if (extension.snapshots.publishTests) {
                                    artifact tasks.testJar
                                }
                                if (extension.snapshots.publishJavaDoc) {
                                    artifact tasks.javadocJar
                                }
                                if (extension.snapshots.publishSources) {
                                    artifact tasks.sourceJar
                                }
                            } else {
                                if (extension.releases.publishTests) {
                                    artifact tasks.testJar
                                }
                                if (extension.releases.publishJavaDoc) {
                                    artifact tasks.javadocJar
                                }
                                if (extension.releases.publishSources) {
                                    artifact tasks.sourceJar
                                }
                            }

                            fixPomForOldGradleVersion(project, gradle, pom)
                        }
                    }
                }
            }
        }
    }

    def void fixPomForOldGradleVersion(project, gradle, pom) {
        // Hack to ensure that the generated POM file contains the correct exclusion patterns.
        // Has been fixed with Gradle 2.1
        if (Double.valueOf(gradle.gradleVersion) < 2.1) {
            project.configurations[JavaPlugin.RUNTIME_CONFIGURATION_NAME].allDependencies.findAll {
                it instanceof ModuleDependency && !it.excludeRules.isEmpty()
            }.each { ModuleDependency dep ->
                pom.withXml {
                    def xmlDep = asNode().dependencies.dependency.find {
                        it.groupId[0].text() == dep.group && it.artifactId[0].text() == dep.name
                    }
                    def xmlExclusions = xmlDep.exclusions[0]
                    if (!xmlExclusions) xmlExclusions = xmlDep.appendNode('exclusions')
                    dep.excludeRules.each { ExcludeRule rule ->
                        def xmlExclusion = xmlExclusions.appendNode('exclusion')
                        xmlExclusion.appendNode('groupId', rule.group)
                        xmlExclusion.appendNode('artifactId', rule.module)
                    }
                }
            }
        }
    }

}
