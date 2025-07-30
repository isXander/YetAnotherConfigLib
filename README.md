# YetAnotherConfigLib

![Enviroment](https://img.shields.io/badge/Enviroment-Client-purple)
[![Java 17](https://img.shields.io/badge/Language-Java%2017-9B599A.svg?color=orange)](https://www.oracle.com/news/announcement/oracle-releases-java-17-2021-09-14)
[![Discord](https://img.shields.io/discord/780023008668287017?color=blue&logo=discord&label=Discord)](https://short.isxander.dev/discord)

[![Modrinth](https://img.shields.io/modrinth/dt/1eAoo2KR?color=00AF5C&label=downloads&logo=modrinth)](https://modrinth.com/mod/yacl)
[![CurseForge](https://cf.way2muchnoise.eu/full_667299_downloads.svg)](https://curseforge.com/minecraft/mc-mods/yacl)

[![Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/isxander)

A mod designed to fit a modder's needs for client-side configuration.

[![](https://www.bisecthosting.com/partners/custom-banners/08bbd3ff-5c0d-4480-8738-de0f070a04dd.png)](https://bisecthosting.com/xander)

## Why does this mod even exist?

This mod was made to fill a hole in this area of Fabric modding. The existing main config libraries don't achieve what I want from them:

- **[Cloth Config API](https://modrinth.com/mod/cloth-config)**: **It's stale.** The developer of cloth has clarified that they are likely not going to add any more features. They don't want to touch it. ([citation](https://user-images.githubusercontent.com/43245524/206530322-3ae46008-5356-468e-9a73-63b859364d4e.png))
- **[SpruceUI](https://github.com/LambdAurora/SpruceUI)**: **It isn't designed for configuration.** In this essence the design feels cluttered. Further details available in [this issue](https://github.com/isXander/Zoomify/issues/85).
- **[OwoLib](https://modrinth.com/mod/owo-lib)**: **It's content focused.** It does a lot of other things as well as config, adding to the size.

As you can see, there's sadly a drawback with all of them and this is where YetAnotherConfigLib comes in.

## Why use YACL?

### Features

YACL has a ton of configuration features:

- Custom control widgets
    - Create your own unique "controller" if the default set does not suit your needs
- Rich descriptions
    - Clickable & hoverable text, powered by vanilla's Text component system
    - WebP (including animated) image previews
    - Custom rich-renderable section to replace image
- Multiple controllers for the same type:
    - Sliders or fields for numbers
    - Dropdowns, cyclers, or raw text fields for strings
    - Tickboxes or ON/OFF text display for booleans
    - ...and more!
- Fully-featured color picker
- Accessible with full compatibility for keyboard control (optimised for Controlify usage)
- High organisation with tabs (categories) and collapsable groups
- Built-in serialization/deserialization techniques so you can skip the error-prone config code
- Full alternative Kotlin DSL

### Version support

YACL supports a huge amount of versions, all kept up to date and released simultaneously, thanks to the amazing
[Stonecutter](https://stonecutter.kikugie.dev/) build tool.

| Version                 | Fabric | Forge | NeoForge |
|-------------------------|--------|-------|----------|
| **1.20.1**              | ✅      | ✅     | ⛔        |
| **1.20.4**              | ✅      | ⛔     | ✅        |
| **1.20.5 - 1.20.6**     | ✅      | ⛔     | ✅        |
| **1.21.0 - 1.21.1**     | ✅      | ⛔     | ✅        |
| **1.21.2** (RC version) | ✅      | ⛔     | ⛔        |

That's **9** different targets, supporting versions that are 500+ days old!

_**Note**: Forge (LexForge) is not and will not be supported past 1.20.1. 
If you're a developer, please port to NeoForge.
If you're a user, you may find that all your favourite mods have already done so._

Each is a separate build, so make sure your users get the correct YACL version for their target of choice.

### Design

YACL is designed to fit right in with the vanilla GUI aesthetic, and will evolve with Minecraft itself. Take a look at
the gallery to see how even in all the currently supported versions, YACL's design looks different to fit in with
vanilla GUI updates.

![image preview](https://cdn.modrinth.com/data/1eAoo2KR/images/5862570281f5109119c11f21a1bba52b6a2ab17f.png)

## Usage for Developers

See [The documentation site](https://docs.isxander.dev/yet-another-config-lib) for how to use YACL in your mod.

## License

This mod is under the [GNU Lesser General Public License, v3.0](LICENSE).
