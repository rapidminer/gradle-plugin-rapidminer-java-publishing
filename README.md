## Introduction
* Add tasks for creating source, javadoc, and test Jars
* Defines jar, source, test, and javadoc Maven publications

## How to use (requires Gradle 2.1+)
	plugins {
		id 'com.rapidminer.java-publishing' version «plugin version»
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