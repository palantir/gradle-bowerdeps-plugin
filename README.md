## Design ##

### Goal ###

Integrate front end build systems (e.g. Grunt, Gulp) into the Gradle Java build lifecycle so that properly ordered full application builds (backend + BE libraries + frontend + FE libraries) can be achieved by running a single Gradle task. This will be useful for CI builds.

### Plan ###

Create a Gradle plugin that uses bower.json files to configure front end dependencies and build order. It will rely on existing node, grunt, and gulp gradle plugins to trigger the appropriate front end build process.

### Plugin Design ###

1. Creates a "bower" configuration in the project
2. Looks for and parses the bower.json file
3. Adds the main and devMain files as artifacts in the "bower" configuration that are builtBy the default front end build task, e.g. grunt default
4. Iterates over the dependencies and devDependencies and tries to resolve the paths to directories
5. For each resolved path, take the name of the dependency and attempts to find a peer Gradle project that matches
  - If the dependency is `example-lib-a: ../example-lib-a` it would iterate over all projects under the root project looking for a match:
    - :web-server matches? /:example-lib-a$/ - false
    - :web-app:example-app matches? /:example-lib-a$/ - false
    - :web-app:example-lib-a matches? /:example-lib-a$/ - true
6. Add each of the projects as project dependencies of the "bower" configuration
7. Adds the task dependencies of the "bower" configuration as dependencies of the default front end build task, e.g. grunt default

When ./gradlew build is run, the assemble task will cause each of the projects to be executed in order based on the inter-project dependencies.

### Possible Future Plans ###

- Add support for command line switches for production/development in order to control minified building, etc. (e.g. `./gradlew -Ptarget=production build` ==> `grunt default --target=production`).
- Expose default watch tasks that can be used to launch watch in parallel across many front end projects

## Usage ##

It isn’t strictly necessary to target gulp or grunt specifically – there already exist plugins we can leverage for both:

- https://github.com/srs/gradle-gulp-plugin
- https://github.com/srs/gradle-grunt-plugin

By checking for the existence of these plugins, we can attempt to do the right thing. If they aren’t available, or the build wants to use a different task, you can define it in the bowerdeps closure.

In each of these examples, running `./gradlew build` will cause the project to be built. `./gradlew assemble` also works.

### Grunt Default ###

```groovy
apply plugin: "com.moowork.grunt"
apply plugin: "com.palantir.bowerdeps"
```

### Grunt Custom Task ###

```groovy
apply plugin: "com.moowork.grunt"
apply plugin: "com.palantir.bowerdeps"

bowerdeps {
  buildTask "grunt_build"
}
```

### Gulp Default ###

```groovy
apply plugin: "com.moowork.gulp"
apply plugin: "com.palantir.bowerdeps"
```

### Gulp Custom Task ###

```groovy
apply plugin: "com.moowork.gulp"
apply plugin: "com.palantir.bowerdeps"

bowerdeps {
  buildTask "gulp_build"
}
```

### Custom ###

```groovy
apply plugin: "com.palantir.bowerdeps"

bowerdeps {
  // Any valid task name will work here
  buildTask "customBuild"
}

task customBuild(type: Exec) {
  // You can put an arbitrary command here
  commandLine './buildScript.sh'
}
```
