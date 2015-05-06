package com.rapidminer.gradle

/**
 * POGO that allows to specify credentials for a remote Maven repository.
 *
 * @author Nils Woehler
 *
 */
class Credentials {

    String username
    String password

    /**
     * Applies the provided closure (e.g. to configure class fields).
     */
    def apply(Closure closure) {
        closure.delegate = this
        closure()
    }
}
