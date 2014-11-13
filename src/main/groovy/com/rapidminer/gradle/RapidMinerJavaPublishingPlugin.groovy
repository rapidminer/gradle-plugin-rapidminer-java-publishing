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

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.ExcludeRule
import org.gradle.api.plugins.JavaPlugin



/**
 *
 * @author Nils Woehler
 *
 */
class RapidMinerJavaPublishingPlugin implements Plugin<Project> {

	private static final String ENCODING = 'UTF-8'
	private static final String JAVA_COMPATIBILITY = JavaVersion.VERSION_1_7

	@Override
	void apply(Project project) {

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

			publishing {
				publications {
					jar(org.gradle.api.publish.maven.MavenPublication) {
						if (plugins.hasPlugin('war')) {
							from components.web
						} else {
							from components.java
						}
						
						// Hack to ensure that the generated POM file contains the correct exclusion patterns.
						// Has been fixed with Gradle 2.1
						if(Double.valueOf(gradle.gradleVersion) < 2.1) {
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
					testJar(org.gradle.api.publish.maven.MavenPublication) { artifact tasks.testJar }
					sourceJar(org.gradle.api.publish.maven.MavenPublication) { artifact tasks.sourceJar  }
					javaDoc(org.gradle.api.publish.maven.MavenPublication) { artifact tasks.javadocJar }
				}
			}
		}
	}
}
