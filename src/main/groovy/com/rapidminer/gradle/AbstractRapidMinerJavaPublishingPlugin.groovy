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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExcludeRule
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.internal.DefaultPublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.bundling.Jar

/**
 * Abstract super class for both publishing plugins.
 *
 * @author Nils Woehler
 *
 */
abstract class AbstractRapidMinerJavaPublishingPlugin implements Plugin<Project> {

    protected Project project

    @Override
    void apply(Project project) {
        this.project = project

        PublishingExtension extension = project.extensions.create('publication', PublishingExtension)

        configurePublicationExtensionDefaults(extension)

        project.configure(project) {
            apply plugin: 'maven-publish'

            // create and configure sourceJar task
            tasks.create(name: 'sourceJar', type: Jar, dependsOn: classes)
            sourceJar {
                from sourceSets.main.allSource
                classifier = 'sources'
            }

            // create and configure javadocJar task
            tasks.create(name: 'javadocJar', type: Jar, dependsOn: javadoc)
            javadocJar {
                classifier = 'javadoc'
                from javadoc.destinationDir
            }

            /*
             * Configure an artifact which contains the classes from the test source set
             */
            configurations { testArtifacts.extendsFrom testRuntime }

            // create and configure testJar task
            tasks.create(name: 'testJar', type: Jar) {
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

            // Define basic jar or war publication
            publishing {
                publications {
                    "${plugins.hasPlugin('war') ? 'war' : 'jar'}"(MavenPublication) {
                        if (plugins.hasPlugin('war')) {
                            from components.web
                        } else {
                            from components.java
                        }

                        fixPomForOldGradleVersion(gradle, pom)
                    }
                }
                repositories {
                    maven {
                        name 'Maven'
                    }
                }
            }

            // Dynamically add artifacts to the Maven publication depending on the plugin extension configuration
            withMavenPublication { MavenPublication mavenPub ->
                if (isSnapshot()) {
                    addArtifactsDynamically mavenPub, extension.snapshots, extension.artifactId, extension.groupId
                } else {
                    addArtifactsDynamically mavenPub, extension.releases, extension.artifactId, extension.groupId
                }
            }

            afterEvaluate {

                // Configure remote Maven repository
                withRepository { MavenArtifactRepository repository ->

                    project.logger.info 'Configuring Nexus maven repository URL and credentials'

                    // Configure repository URL
                    def baseUrl = extension.baseUrl?.endsWith('/') ? extension.baseUrl : (extension.baseUrl + '/')
                    def repo = isSnapshot() ? extension.snapshots.repo : extension.releases.repo
                    repository.url = "${baseUrl}${repo}"
                    project.logger.info "Repository URL is: ${repository.url}"

                    // Configure repository Credentials
                    boolean removeRemoteRepoPublishTask = false

                    // In case no credentials are defined...
                    if (!extension.credentials) {
                        project.logger.info "No credentials defined for publication extension. Looking for 'nexusUser' and 'nexusPassword' project properties."

                        // .. check if nexusUser and nexusPassword project properties are set
                        if (!project.hasProperty('nexusUser')) {
                            project.logger.info "Project property 'nexusUser' not found."
                            removeRemoteRepoPublishTask = true
                        } else {
                            if (!project.hasProperty('nexusPassword')) {
                                project.logger.info "Project property 'nexusPassword' not found."
                                removeRemoteRepoPublishTask = true
                            } else {
                                project.logger.info "Both 'nexusUser' and 'nexusPassword' found. Using as Maven repository credentials."
                                extension.credentials = new Credentials(username: project.nexusUser, password: project.nexusPassword)
                            }
                        }
                    } else {
                        project.logger.info 'Using Maven repository credentials defined in publication extension.'
                    }

                    if (removeRemoteRepoPublishTask) {
                        def publishRemoteTask = project.tasks.findByName("publish${plugins.hasPlugin('war') ? 'War' : 'Jar'}PublicationToNexusRepository")
                        if (publishRemoteTask) {
                            project.logger.info 'Removing remote publishing task as it will not work properly without credentials.'
                            project.tasks.remove(publishRemoteTask)
                        }
                    } else {
                        repository.credentials {
                            username = extension.credentials.username
                            password = extension.credentials.password
                        }
                    }

                }

                if (plugins.hasPlugin('java') && extension.vendor) {
                    // configure jar manifests with Vendor and Version
                    jar {
                        manifest {
                            attributes(
                                    'Manifest-Version': '1.0',
                                    'Created-By': extension.vendor,
                                    'Specification-Version': project.version,
                                    'Specification-Vendor': extension.vendor,
                                    'Implementation-Version': project.version,
                                    'Implementation-Vendor': extension.vendor,
                            )
                        }
                    }
                }
            }
        }
    }

    def isSnapshot() {
        return project.version?.endsWith('-SNAPSHOT')
    }

    /**
     *
     * @param gradle
     * @param pom
     * @return
     */
    def fixPomForOldGradleVersion(gradle, pom) {
        // Hack to ensure that the generated POM file contains the correct exclusion patterns.
        // This has been fixed with Gradle 2.1
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

    /**
     *
     * @param withRepoClosure
     */
    def void withRepository(Closure repoClosure) {

        def configureRepositoryClosure = {

            // Wait for the maven-publishing plugin to be applied.
            project.plugins.withType(PublishingPlugin) { PublishingPlugin publishingPlugin ->
                DefaultPublishingExtension publishingExtension = project.getExtensions().getByType(DefaultPublishingExtension)
                publishingExtension.repositories.withType(MavenArtifactRepository, repoClosure)
            }
        }

        // It's possible that we're running in someone else's afterEvaluate, which means we need to run this immediately
        if (project.getState().executed) {
            configureRepositoryClosure.call()
        } else {
            project.afterEvaluate configureRepositoryClosure
        }
    }

    /**
     *
     * @param withPubClosure
     * @return
     */
    def void withMavenPublication(Closure withPubClosure) {

        // New publish plugin way to specify artifacts in resulting publication
        def addArtifactClosure = {

            // Wait for the maven-publishing plugin to be applied.
            project.plugins.withType(PublishingPlugin) { PublishingPlugin publishingPlugin ->
                DefaultPublishingExtension publishingExtension = project.getExtensions().getByType(DefaultPublishingExtension)
                publishingExtension.publications.withType(MavenPublication, withPubClosure)
            }
        }

        // It's possible that we're running in someone else's afterEvaluate, which means we need to run this immediately
        if (project.getState().executed) {
            addArtifactClosure.call()
        } else {
            project.afterEvaluate addArtifactClosure
        }
    }

    /**
     *
     * @param mavenPub
     * @param config
     * @param artifactId
     * @param groupId
     * @return
     */
    def addArtifactsDynamically(MavenPublication mavenPub, ArtifactConfig config, String artifactId, String groupId) {
        if(artifactId){
            mavenPub.artifactId = artifactId
        }
        if(groupId){
            mavenPub.groupId = groupId
        }
        if (config.publishTests) {
            mavenPub.artifact project.tasks.testJar
        }
        if (config.publishJavaDoc) {
            mavenPub.artifact project.tasks.javadocJar
        }
        if (config.publishSources) {
            mavenPub.artifact project.tasks.sourceJar
        }
        config.artifacts.each {
            mavenPub.artifact it
        }
    }

    /**
     * Called after creating the PublishingExtension to configure the default values for each publishing plugin.
     *
     * @param extension the publishing extension that has to be configured
     */
    def abstract void configurePublicationExtensionDefaults(PublishingExtension extension)

}
