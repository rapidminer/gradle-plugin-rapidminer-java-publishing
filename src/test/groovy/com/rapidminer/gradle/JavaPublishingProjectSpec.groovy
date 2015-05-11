package com.rapidminer.gradle

import nebula.test.ProjectSpec
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.internal.DefaultPublishingExtension
import org.gradle.api.publish.plugins.PublishingPlugin

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Tests whether the local jar and war publication tasks are available
 *
 * @author Nils Woehler
 *
 */
class JavaPublishingProjectSpec extends ProjectSpec {

    def 'Apply java-publishing plugin with java'() {
        setup:
        project.version = '0.1.1'
        project.apply(plugin: 'java')
        project.apply(plugin: RapidMinerJavaPublishingPlugin)

        when:
        project.evaluate()

        then:
        noExceptionThrown()
        project.tasks.findByName('publishJarPublicationToMavenLocal')
        !project.tasks.findByName('publishJarPublicationToMavenRepository')
    }

    def 'Apply java-publishing plugin with war'() {
        setup:
        project.version = '0.1.1'
        project.apply(plugin: 'war')
        project.apply(plugin: RapidMinerJavaPublishingPlugin)

        when:
        project.evaluate()

        then:
        noExceptionThrown()
        project.tasks.findByName('publishWarPublicationToMavenLocal')
    }

    def 'Apply java-publishing plugin with remote repo credentials'() {
        setup:
        project.version = '0.1.1'
        project.apply(plugin: 'java')
        project.apply(plugin: RapidMinerJavaPublishingPlugin)
        PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension)
        publishingExtension.credentials {
            username = 'test'
            password = 'password'
        }

        when:
        project.evaluate()

        then:
        noExceptionThrown()
        project.tasks.findByName('publishJarPublicationToMavenRepository')
    }

}
