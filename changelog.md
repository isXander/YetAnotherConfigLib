# YetAnotherConfigLib 3.4.0

This build supports the following versions:
- Fabric 1.20.1
- Fabric 1.20.4
- Fabric 1.20.5
- Forge 1.20.1
- NeoForge 1.20.4

## For developers...

This is the first build which uses a new build toolchain to support multiple minecraft versions in the same codebase.
With this change, the artifact of the mod has changed.

```groovy
modImplementation "dev.isxander:yet-another-config-lib:{modversion}+{mcversion}-{loader}"
// for example...
modImplementation "dev.isxander:yet-another-config-lib:3.4.0+1.20.1-fabric"
```

If you are using YACL in an architectury project (one with a common subproject), you will from now on depend on
the fabric version of YACL. There is no longer any common artifact. I don't believe there to be any issues with doing it
like this.

## Additions

- New colour picker widget by [Superkat32](https://github.com/isXander/YetAnotherConfigLib/pull/140)
  - This works with existing colour options.

![Colour picker example](https://i.imgur.com/G6Yx6RU.png)

- Added a custom tab API so categories can provide their own GUI, instead of YACL options.
  - Mod developers can make their category class implement `CustomTabProvider`. Javadoc is available.
- Add a new Kotlin DSL for creating your YACL UIs. This is a wrapper on top of the java builder interface providing extra functionality for Kotlin users.
  - YACL does not provide any kotlin libraries, nor does it depend on them. If you would like to use this DSL, make sure your own mod depends on your loader's Kotlin mod.
  - This is included in the main artifact. You don't need additional dependencies.
  - An example usage can be found [in the testmod](https://github.com/isXander/YetAnotherConfigLib/blob/multiversion/dev/src/testmod/kotlin/dev/isxander/yacl3/test/DslTest.kt)
- Improved the backend of dropdown controllers by [Crendgrim](https://github.com/isXander/YetAnotherConfigLib/pull/162)

## Bug Fixes

- Fix GIFs not being preloaded in the resource reloader

