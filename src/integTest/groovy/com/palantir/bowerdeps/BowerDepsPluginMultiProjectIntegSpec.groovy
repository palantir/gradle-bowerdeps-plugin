/*
 * Copyright 2015 Palantir Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.bowerdeps

import nebula.test.IntegrationSpec
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.grunt.GruntPlugin
import com.moowork.gradle.gulp.GulpPlugin

class BowerDepsPluginMultiProjectIntegSpec extends IntegrationSpec {
    def 'no plugins and no build task specified'() {
        setup:
        // Root
        buildFile << applyPlugin(NodePlugin)
        buildFile << "\n"

        buildFile << '''
        node {
            version = "0.12.0"
            npmVersion = "2.7.1"
            download = true
            workDir = file("build/node")
        }
        '''.stripIndent()

        // Example App
        File exampleapp = addSubproject('example-app', """
        ${applyPlugin(NodePlugin)}
        ${applyPlugin(GulpPlugin)}
        ${applyPlugin(BowerDepsPlugin)}

        node {
          nodeModulesDir = file("../")
          npmVersion = "2.7.1"
          workDir = file("../build/node")
        }
        """.stripIndent())

        def exappgulpfile = createFile('gulpfile.js', exampleapp)
        exappgulpfile << '''
        var gulp = require('gulp');

        gulp.task('default', function() {
        });
        '''.stripIndent()

        def exappbower = createFile(BowerDepsPlugin.BOWER_MANIFEST_NAME, exampleapp)
        exappbower << '''
        {
          "name": "example-app",
          "version": "0.0.0",
          "authors": [
            "First Last <first.last@example.com>"
          ],
          "license": "MIT",
          "main": [
            "dist/foo.js"
          ],
          "devMain": "dist/bar.js",
          "ignore": [
            "**/.*",
            "node_modules",
            "bower_components",
            "test",
            "tests"
          ],
          "dependencies": {
            "example-lib-a": "../example-lib-a",
            "bootstrap": "~3.3.2",
            "blueprint": "ssh://git@example.com/bower/blueprint.git#0.6.0",
            "angular": "1.3.8",
            "codemirror": "4.5.0",
            "jquery": "2.1.3",
            "lodash": "2.4.1",
            "select2": "ssh://git@example.com/bower/select2.git#dabcde",
            "sigma": "https://github.com/palantir/sigma.js.git#df531c0a0f6cd244b104059298890a3a6ff5b46b",
          },
          "devDependencies": {
            "angular-mocks": "1.3.8",
            "blanket": "5fb818ce7acd98ea3d8e577121d065dedf22056d",
            "chai": "1.9.1",
            "mocha": "1.20.1",
            "sinonjs": "1.10.2"
          }
        }
        '''.stripIndent()

        // Example Lib A
        File exampleliba = addSubproject('example-lib-a', """
        ${applyPlugin(NodePlugin)}
        ${applyPlugin(GruntPlugin)}
        ${applyPlugin(BowerDepsPlugin)}

        node {
          nodeModulesDir = file("../")
          npmVersion = "2.7.1"
          workDir = file("../build/node")
        }

        bowerdeps {
          buildTask = 'grunt_watch'
        }
        """.stripIndent())

        def exlibagruntfile = createFile('Gruntfile.js', exampleliba)
        exlibagruntfile << '''
        module.exports = function(grunt) {
            grunt.registerTask('watch', []);
        };
        '''.stripIndent()

        def exlibabower = createFile(BowerDepsPlugin.BOWER_MANIFEST_NAME, exampleliba)
        exlibabower << '''
        {
          "name": "example-lib-a",
          "version": "0.0.0",
          "authors": [
            "First Last <first.last@example.com>"
          ],
          "main": "test.js",
          "license": "MIT",
          "ignore": [
            "**/.*",
            "node_modules",
            "bower_components",
            "test",
            "tests"
          ],
          "dependencies": {
            "example-lib-b": "../example-lib-b",
            "jquery": "~2.1.3"
          }
        }
        '''.stripIndent()

        // Example Lib B
        File examplelibb = addSubproject('example-lib-b', """
        ${applyPlugin(NodePlugin)}
        ${applyPlugin(GruntPlugin)}
        ${applyPlugin(BowerDepsPlugin)}

        node {
            nodeModulesDir = file("../")
            npmVersion = "2.7.1"
            workDir = file("../build/node")
        }

        grunt {
            workDir = file("test")
        }
        """.stripIndent())

        def exlibbgruntfile = createFile('test/Gruntfile.js', examplelibb)
        exlibbgruntfile << '''
        module.exports = function(grunt) {
            grunt.registerTask('default', []);
        };
        '''.stripIndent()

        def exlibbbower = createFile(BowerDepsPlugin.BOWER_MANIFEST_NAME, examplelibb)
        exlibbbower << '''
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
        def result = runTasksSuccessfully('nodeSetup', 'installGrunt', 'installGulp', ':example-app:build')

        then:
        result.wasExecuted('nodeSetup')
        result.wasExecuted(':example-lib-b:grunt_default')
        result.wasExecuted(':example-lib-a:grunt_watch')
        result.wasExecuted(':example-app:gulp_default')
    }
}
