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
/**
 * The java-publishing plugin that uses the 'maven-publish' plugin to preconfigure RapidMiner publications
 * for public but closed-source projects.
 *
 * @author Nils Woehler
 *
 */
class RapidMinerJavaPublicPublishingPlugin extends AbstractRapidMinerJavaPublishingPlugin {


    @Override
    def void configurePublicationExtensionDefaults(PublishingExtension extension) {
        extension.releases = new ArtifactConfig(publishTests: true, publishSources: false, publishJavaDoc: true, repo: 'releases-public')
        extension.snapshots = new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'snapshots')
        extension.licenseType = PublishingExtension.LicenseTypes.RM_EULA
    }
}
