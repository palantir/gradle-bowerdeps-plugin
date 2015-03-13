package com.palantir.bowerdeps

import nebula.test.IntegrationSpec
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.grunt.GruntPlugin
import com.moowork.gradle.gulp.GulpPlugin

class BowerDepsPluginIntegSpec extends IntegrationSpec {
    def setup() {
        buildFile << applyPlugin(NodePlugin)
        buildFile << "\n"

        buildFile << applyPlugin(BowerDepsPlugin)
        buildFile << "\n"

        buildFile << '''
            node {
                version = "0.12.0"
                npmVersion = "2.7.1"
                download = true
                workDir = file("build/node")
            }
        '''.stripIndent()
    }

    def 'no plugins and no build task specified'() {
        setup:
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
        def result = runTasksSuccessfully('nodeSetup', 'build')

        then:
        result.wasExecuted('nodeSetup')
        result.wasUpToDate('build')
    }

    def 'grunt dependencies added'() {
        setup:
        buildFile << "\n" + applyPlugin(GruntPlugin) + "\n"

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

        def gruntfile = createFile("Gruntfile.js")
        gruntfile << '''
        module.exports = function(grunt) {
            grunt.registerTask('default', []);
        };
        '''.stripIndent()

        when:
        def result = runTasksSuccessfully('installGrunt', 'build')

        then:
        result.wasExecuted('installGrunt')
        result.wasExecuted('grunt_default')
        !result.wasUpToDate('build')
    }

    def 'gulp dependencies added'() {
        setup:
        buildFile << "\n" + applyPlugin(GulpPlugin) + "\n"

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

        def gulpfile = createFile("gulpfile.js")
        gulpfile << '''
        var gulp = require('gulp');

        gulp.task('default', function() {
        });
        '''.stripIndent()

        when:
        def result = runTasksSuccessfully('installGulp', 'build')

        then:
        result.wasExecuted('installGulp')
        result.wasExecuted('gulp_default')
        !result.wasUpToDate('build')
    }

    def 'grunt dependencies added to alternate task'() {
        setup:
        buildFile << "\n" + applyPlugin(GruntPlugin) + "\n"

        buildFile << '''
            bowerdeps {
                buildTask = 'grunt_watch'
            }
        '''.stripIndent()

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

        def gruntfile = createFile("Gruntfile.js")
        gruntfile << '''
        module.exports = function(grunt) {
            grunt.registerTask('default', []);
            grunt.registerTask('watch', []);
        };
        '''.stripIndent()

        when:
        def result = runTasksSuccessfully('installGrunt', 'build')

        then:
        result.wasExecuted('installGrunt')
        result.wasExecuted('grunt_watch')
        !result.wasExecuted('grunt_default')
        !result.wasUpToDate('build')
    }

    def 'gulp dependencies added to alternate task'() {
        setup:
        buildFile << "\n" + applyPlugin(GulpPlugin) + "\n"

        buildFile << '''
            bowerdeps {
                buildTask = 'gulp_watch'
            }
        '''.stripIndent()

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

        def gulpfile = createFile("gulpfile.js")
        gulpfile << '''
        var gulp = require('gulp');

        gulp.task('default', function() {
        });
        gulp.task('watch', function() {
        });
        '''.stripIndent()

        when:
        def result = runTasksSuccessfully('installGulp', 'build')

        then:
        result.wasExecuted('installGulp')
        result.wasExecuted('gulp_watch')
        !result.wasExecuted('gulp_default')
        !result.wasUpToDate('build')
    }
}
