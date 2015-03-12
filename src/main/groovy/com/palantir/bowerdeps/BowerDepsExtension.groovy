package com.palantir.bowerdeps

import org.gradle.api.Project

class BowerDepsExtension {
    final static String NAME = 'bowerdeps'

    File workDir

    String buildTask

    BowerDepsExtension(final Project project) {
        this.workDir = project.projectDir
    }
}
