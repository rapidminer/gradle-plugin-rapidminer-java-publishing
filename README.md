## Introduction
* Add tasks for creating source, javadoc, and test Jars
* Defines a jar/war publication that defines different publications for snapshots/releases which can be configured
  via the 'publication' extension.

## How to use (requires Gradle 2.4+)
	plugins {
		id 'com.rapidminer.java-publishing' version <plugin version>
		// OR: id 'com.rapidminer.java-publishing.public' version <plugin version>
		// OR: id 'com.rapidminer.java-publishing.open-source' version <plugin version>
	}
	
	publication {
	    // The baseUrl of the repository to publish to. It should contain everything but the repository to publish to.
		baseUrl = 'https://maven.rapidminer.com/nexus/content/repositories/'
		
		/*
		 * Allows to define credentials for the configured repository.
		 * 
		 * In case it is not set the extension will look for the project properties
		 * called 'nexusUser' and 'nexusPassword' which can be globally configured via the gradle.properties in ~/.gradle
		 */
		credentials {
			username = '---'
			password = '...'
		}
		
		/*
		 * Allows to configure the repository to publish releases to and which artifacts should be published for releases. 
		 * The defaults are shown below.
		 */
		releases {
			repo = 'releases'
			publishTests = true
            publishSources = false
            publishJavaDoc = true
		}
		
		/*
		 * Allows to configure the repository to publish snapshots to and which artifacts should be published for snapshots. 
		 * The defaults are shown below.
		 */
		snapshots {
			repo = 'snapshots'
			publishTests = true
			publishSources = true
			publishJavaDoc = false
		}
	}
	
	
## Applied Plugins
* maven-publish (http://www.gradle.org/docs/current/userguide/publishing_maven.html)

## Added Tasks

##### sourceJar
Creates a Jar containing all project sources

##### javadocJar
Creates a Jar containing the project's JavaDoc

##### testJar
Creates a Jar containing the project's test classes
