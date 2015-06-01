## Change Log

#### 0.2.0
* Streamlined publishing configuration by adding a plugin extension called 'publication' that ships with highly opinionated 
  default values defined by three different plugins ('com.rapidminer.java-publishing', 'com.rapidminer.java-publishing.public', 
  and 'com.rapidminer.java-publishing.open-source')
* The 'jar' publication is now called 'war' publication in case the 'war' plugin is applied
* Removed javaDoc publication as it is now included in the jar/war publication

#### 0.1.3
* Removes Test and Source publishing tasks as they break dependency fetching. Instead they are also executed when publishing the Java Jar.

#### 0.1.2
* Adds shortened plugin name 'com.rapidminer.java-publishing' to comply with plugins.gradle.org standards

#### 0.1.1
* Only fix POM exclusions for Gradle versions below 2.1

#### 0.1.0 
* Extension release






