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

import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Unroll

class BowerDepsPluginSpec extends Specification {

    JsonSlurper slurper = new JsonSlurper()

    @Unroll
    def 'manifest testing - #title'() {
        setup:
        def manifest = slurper.parseText(json)
        def plugin = new BowerDepsPlugin()

        when:
        def artifacts = plugin.getManifestArtifacts(manifest)

        then:
        artifacts.size() == size
        // Items is either null (empty) or check that all items are contained in the artifacts array
        // uses "inject" (the groovy version of reduce or foldl) to check contains and return a boolean
        items == null || items.inject(true) { acc, val -> artifacts.contains(val) && acc }

        where:
title | json | items | size
"no main or devMain" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | null | 0
"no devMain and main is a string" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "main": "foo/test.js",
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | ["foo/test.js"] | 1
"no devManin and main is an array" |
'''
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
}
''' | ["foo/test.js"] | 1
"no devMain and main is an array with many values" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "main": [
    "foo/test.js",
    "bar/ex.html",
    "css/foo.less"
  ],
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | ["foo/test.js",
       "bar/ex.html",
       "css/foo.less"] | 3
"no devMain and main is an empty array" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "main": [],
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | null | 0
"no main and devMain is a string" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "devMain": "foo/bar.js",
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | ["foo/bar.js"] | 1
"no main and devMain is an array" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "devMain": [
    "foo/bar.js"
  ],
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | ["foo/bar.js"] | 1
"no main and devMain is an array with many values" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "devMain": [
    "foo/bar.js",
    "jack/ofall.html",
    "bonus/round/of.css"
  ],
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | ["foo/bar.js",
       "jack/ofall.html",
       "bonus/round/of.css"] | 3
"no main and devMain is an empty array" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "devMain": [],
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | null | 0
"main is a string and devMain is a string" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "main": "helloworld.js",
  "devMain": "foobar.html",
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | ["helloworld.js",
       "foobar.html"] | 2
"main is an array and devMain is a string" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "main": [
    "helloworld.js"
  ],
  "devMain": "foobar.html",
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | ["helloworld.js",
       "foobar.html"] | 2
"main is an array and devMain is an array" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "main": [
    "helloworld.js"
  ],
  "devMain": [
    "foobar.html"
  ],
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | ["helloworld.js",
       "foobar.html"] | 2
"main is an array and devMain is an empty array" |
'''
{
  "name": "example-lib-b",
  "version": "0.0.0",
  "authors": [
    "First Last <first.last@example.com>"
  ],
  "main": [
    "helloworld.js"
  ],
  "devMain": [],
  "license": "MIT",
  "ignore": [
    "**/.*",
    "node_modules",
    "bower_components",
    "test",
    "tests"
  ]
}
''' | ["helloworld.js"] | 1
    }
}
