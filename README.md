Tired of JSON? Look no further: this Gradle plugin integrates your mod's metadata into your build script so that you don't have to touch that nasty JSON.
No more `"version": "${version}"` in your JSON either. :triumph: Instead, fabric.mod.gradle automatically derives mod ID, version and description from your project and scans for entrypoints when none is specified.
With that said, these defaults are overridable. And of course, `schemaVersion` is set to `1`. :)

After all classes have been compiled, the metadata are generated (for most people) in `build/generated/resources/main`.

Despite this plugin's name, you do not need a separate file for your metadata but you may make one.

See [the test project](./test/project) for examples.

Mixin configuration still has to be done through JSON but I intend to change that and make it fully automatic by default.

