# Gradle Plugin for Multi-Project Bower Dependency Ordering

[![Build Status](https://travis-ci.org/palantir/gradle-bowerdeps-plugin.svg?branch=develop)](https://travis-ci.org/palantir/gradle-bowerdeps-plugin)

This plugin is useful in multi-project gradle builds where `bower.json` is used to declare dependencies between projects. It sets the cross project dependencies so that projects build in the correct order.

### Plugin Design ###

1. Creates a "bower" configuration in the project
2. Looks for and parses the bower.json file
3. Adds the main and devMain files as artifacts in the "bower" configuration that are builtBy the default front end build task, e.g. `grunt default`
4. Iterates over the dependencies and devDependencies and tries to resolve the paths to directories
5. For each resolved path, take the name of the dependency and attempts to find a peer Gradle project that matches
  - If the dependency is `example-lib-a: ../example-lib-a` it would iterate over all projects under the root project looking for a match:
    - :web-server matches? /:example-lib-a$/ - false
    - :web-app:example-app matches? /:example-lib-a$/ - false
    - :web-app:example-lib-a matches? /:example-lib-a$/ - true
6. Add each of the projects as project dependencies of the "bower" configuration
7. Adds the task dependencies of the "bower" configuration as dependencies of the default front end build task, e.g. `grunt default

## Installing the plugin ##

Releases of this plugin are hosted at https://bintray.com/palantir/releases/gradle-bowerdeps-plugin.

Setup the plugin like this:

```groovy
buildscript {
    repositories {
        maven {
            url 'http://dl.bintray.com/palantir/releases'
        }
    }

    dependencies {
        classpath 'com.palantir:gradle-bowerdeps-plugin:0.1.0'
    }

}

apply plugin: 'com.palantir.bowerdeps'
```

When `./gradlew build` is run, the `assemble` task will cause each of the projects to be executed in order based on the inter-project dependencies.

## Configuring the plugin ##

The configuration block for the plugin looks like this.

```groovy
bowerdeps {
  // name of the task to run to build the bower main files
  buildTask 'grunt_build'

  // working directory where bower.json is located
  workDir file('folder/containing/bower/manifest')
}
```

In each of these examples, running `./gradlew build` will cause the project to be built. `./gradlew assemble` also works.

### Grunt Default ###

You can utilize https://github.com/srs/gradle-grunt-plugin and the `buildTask` will automatically be set to `grunt_default`.

```groovy
apply plugin: 'com.moowork.grunt'
apply plugin: 'com.palantir.bowerdeps'
```

### Grunt Custom Task ###

```groovy
apply plugin: 'com.moowork.grunt'
apply plugin: 'com.palantir.bowerdeps'

bowerdeps {
  buildTask 'grunt_build'
}
```

### Gulp Default ###

You can utilize https://github.com/srs/gradle-gulp-plugin and the `buildTask` will automatically be set to `gulp_default`.

```groovy
apply plugin: 'com.moowork.gulp'
apply plugin: 'com.palantir.bowerdeps'
```

### Gulp Custom Task ###

```groovy
apply plugin: 'com.moowork.gulp'
apply plugin: 'com.palantir.bowerdeps'

bowerdeps {
  buildTask 'gulp_build'
}
```

### Custom ###

```groovy
apply plugin: 'com.palantir.bowerdeps'

bowerdeps {
  // Any valid task name will work here
  buildTask 'customBuild'
}

task customBuild(type: Exec) {
  // You can put an arbitrary command here
  commandLine './buildScript.sh'
}
```
