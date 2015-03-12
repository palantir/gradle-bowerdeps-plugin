package com.palantir.bowerdeps

import nebula.test.IntegrationSpec
import com.moowork.gradle.grunt.GruntPlugin

class BowerDepsIntegTest extends IntegrationSpec {
    def 'example'() {
        setup:
        buildFile << applyPlugin(GruntPlugin)
        buildFile << "\n"
        buildFile << applyPlugin(BowerDepsPlugin)
        buildFile << "\n"

        def bowerJson = createFile(BowerDepsPlugin.BOWER_MANIFEST_NAME)
        bowerJson << '''
        {
          "name": "example-lib-b",
          "version": "0.0.0",
          "authors": [
            "First Last <first.last@example.com>"
          ],
          "main": [
            "foo/test.js"
          ],
          "license": "MIT",
          "ignore": [
            "**/.*",
            "node_modules",
            "bower_components",
            "test",
            "tests"
          ]
        }'''.stripIndent()

        when:
        def result = runTasksSuccessfully('tasks')

        then:
        result.failure == null
    }
}
