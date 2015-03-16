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

import java.util.concurrent.atomic.AtomicBoolean
import nebula.test.PluginProjectSpec

class BowerDepsPluginProjectSpec extends PluginProjectSpec {
    @Override
    String getPluginName() {
        return 'com.palantir.bowerdeps'
    }

    def 'creates extension and configuration'() {
        when:
        project.apply plugin: pluginName

        then:
        project.extensions.getByName('bowerdeps')
        project.configurations.getByName('bowerdeps')
    }

    def 'can evaluate'() {
        setup:
        def signal = new AtomicBoolean(false)

        project.afterEvaluate {
            signal.getAndSet(true)
        }

        project.apply plugin: pluginName

        when:
        project.evaluate()

        then:
        noExceptionThrown()
        signal.get() == true
    }

    def 'evaluation with no manifest adds no dependencies or artifacts'() {
        setup:
        project.apply plugin: pluginName

        when:
        project.evaluate()

        then:
        noExceptionThrown()
        project.configurations.getByName('bowerdeps').artifacts.size() == 0
        project.configurations.getByName('bowerdeps').dependencies.size() == 0
    }
}
