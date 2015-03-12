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
