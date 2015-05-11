package com.rapidminer.gradle

import nebula.test.PluginProjectSpec

/**
 * Test whether the plugin can be applied properly (idempotently and in a multi-project).
 *
 * @author Nils Woehler
 *
 */
class JavaPublishingPluginSpec extends PluginProjectSpec {

    @Override
    String getPluginName() {
        return 'com.rapidminer.java-publishing'
    }
}
