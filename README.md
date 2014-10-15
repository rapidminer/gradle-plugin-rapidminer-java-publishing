## Introduction
* Add tasks for creating source, javadoc, and test Jars
* Define jar (main, source,test) and javadoc Maven publications

## How to use
	buildscript {
		dependencies {
			classpath 'com.rapidminer.gradle:java-publishing:$VERSION'
		}
	}

	apply plugin: 'com.rapidminer.gradle.java-publishing'
	
## Applied Plugins
* maven-publish (http://www.gradle.org/docs/current/userguide/publishing_maven.html)

## Added Tasks

##### sourceJar
Creates a Jar containing all Project sources

##### javadocJar
Creates a Jar containing the project's JavaDoc

##### testJar
Creates a Jar containing the project's test classes