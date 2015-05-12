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
