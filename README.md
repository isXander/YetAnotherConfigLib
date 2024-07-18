<center><div align="center">

![](https://raw.githubusercontent.com/isXander/YetAnotherConfigLib/1.19/src/main/resources/yacl-128x.png)

# YetAnotherConfigLib

![Enviroment](https://img.shields.io/badge/Enviroment-Client-purple)
[![Java 17](https://img.shields.io/badge/Language-Java%2017-9B599A.svg?color=orange)](https://www.oracle.com/news/announcement/oracle-releases-java-17-2021-09-14)
[![Discord](https://img.shields.io/discord/780023008668287017?color=blue&logo=discord&label=Discord)](https://short.isxander.dev/discord)

[![Modrinth](https://img.shields.io/modrinth/dt/1eAoo2KR?color=00AF5C&label=downloads&logo=modrinth)](https://modrinth.com/mod/yacl)
[![CurseForge](https://cf.way2muchnoise.eu/full_667299_downloads.svg)](https://curseforge.com/minecraft/mc-mods/yacl)

[![Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/isxander)

Yet Another Config Lib, like, what were you expecting?
  
[![](https://www.bisecthosting.com/partners/custom-banners/08bbd3ff-5c0d-4480-8738-de0f070a04dd.png)](https://bisecthosting.com/xander)

</div></center>

## Why does this mod even exist?

This mod was made to fill a hole in this area of Fabric modding. The existing main config libraries don't achieve what I want from them:

- **[Cloth Config API](https://modrinth.com/mod/cloth-config)**:<br/>**It's stale.** The developer of cloth has clarified that they are likely not going to add any more features. They don't want to touch it. ([citation](https://user-images.githubusercontent.com/43245524/206530322-3ae46008-5356-468e-9a73-63b859364d4e.png))
- **[SpruceUI](https://github.com/LambdAurora/SpruceUI)**:<br/>**It isn't designed for configuration.** In this essence the design feels cluttered. Further details available in [this issue](https://github.com/isXander/Zoomify/issues/85).
- **[MidnightLib](https://modrinth.com/mod/midnightlib)**:<br/>**It has cosmetics among other utilities.** It may not be large but some players (including me) wouldn't want cosmetics out of nowhere.
- **[OwoLib](https://modrinth.com/mod/owo-lib)**:<br/>**It's content focused.** It does a lot of other things as well as config, adding to the size.

As you can see, there's sadly a drawback with all of them and this is where YetAnotherConfigLib comes in.

## How is YACL better?

YACL has the favour of hindsight. Whilst developing this fresh library, I can make sure that it does everything right:

- **Client sided library.** YACL is built for client mods only, making it a smaller size.
- **Easy API.** YACL takes inspiration from [Sodium's](https://modrinth.com/mod/sodium) internal configuration library.
- **It's styled to fit in Minecraft.** YACL's GUI is designed to fit right in.

## Usage

See [The documentation site](https://docs.isxander.dev/yet-another-config-lib) for how to use YACL in your mod.

## Screenshots

<center><div align="center">

![java_A3zdbksGkC](https://user-images.githubusercontent.com/43245524/206924832-293b0780-2a8c-4b09-8765-155318d09ed9.png)

</div></center>

## License

This mod is under the [GNU Lesser General Public License, v3.0](LICENSE).
