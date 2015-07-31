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
 * Extension for the java-publishing plugin which allows to define remote Maven repository and snapshot/release publications.
 *
 * @author Nils Woehler
 *
 */
class PublishingExtension {

    public interface LicenseType {
        def getName()
        def getUrl()
        def getDistribution()
    }

    public enum LicenseTypes implements  LicenseType {
        AGPL_V3(name: 'GNU Affero General Public License, Version 3.0', url: 'http://www.gnu.org/licenses/agpl-3.0.html', distribution: 'repo'),
        APACHE_V2(name: 'Apache License, Version 2.0', url: 'http://www.apache.org/licenses/LICENSE-2.0', distribution: 'repo'),
        LGPL_V3(name: 'GNU Lesser General Public License, Version 3.0', url: 'http://www.gnu.org/licenses/lgpl-3.0.html', distribution: 'repo'),
        RM_EULA(name: 'RapidMiner EULA', url: 'www.rapidminer.com/web/eula', distribution: 'manual')

        def name
        def url

        /**
         * This describes how the project may be legally distributed.
         * The two stated methods are repo (they may be downloaded from a Maven repository) or manual (they must be manually installed).
         */
        def distribution
    }

    Credentials credentials
    String baseUrl = 'https://maven.rapidminer.com/content/repositories/'
    ArtifactConfig releases = new ArtifactConfig(publishTests: true, publishSources: false, publishJavaDoc: true, repo: 'releases')
    ArtifactConfig snapshots = new ArtifactConfig(publishTests: true, publishSources: true, publishJavaDoc: false, repo: 'snapshots')
    String vendor = 'RapidMiner GmbH'
    String vendorUrl = 'www.rapidminer.com'
    Closure groupId
    Closure artifactId
    LicenseType licenseType

    /**
     * Delegates the provided Closure to the ArtifactConfig.
     */
    def snapshots(Closure closure) {
        snapshots.apply(closure)
    }

    /**
     * Delegates the provided Closure to the ArtifactConfig.
     */
    def releases(Closure closure) {
        releases.apply(closure)
    }

    /**
     * Delegates the provided Closure to the ArtifactConfig.
     */
    def credentials(Closure closure) {
        if (!credentials) {
            credentials = new Credentials()
        }
        credentials.apply(closure)
    }

    def groupId(Closure closure) {
        this.groupId = closure
    }

    def artifactId(Closure closure) {
        this.artifactId = closure
    }

    def vendor(String vendor){
        this.vendor = vendor
    }

    def vendorUrl(String url){
        this.vendorUrl = url
    }

    def license(LicenseType licenseType){
        this.licenseType = licenseType
    }

    def license(String license) {
        this.licenseType = LicenseTypes.valueOf(license)
    }

    def baseUrl(String baseUrl){
        this.baseUrl = baseUrl
    }

}
