<div align="center">

![](https://raw.githubusercontent.com/isXander/YetAnotherConfigLib/1.19/src/main/resources/yacl-128x.png)

# YetAnotherConfigLib

![Enviroment](https://img.shields.io/badge/Enviroment-Client-purple)
[![Java 17](https://img.shields.io/badge/Language-Java%2017-9B599A.svg?color=orange)](https://www.oracle.com/news/announcement/oracle-releases-java-17-2021-09-14)
[![Discord](https://img.shields.io/discord/780023008668287017?color=blue&logo=discord&label=Discord)](https://discord.com/invite/rURmwrzUcz)

[![Modrinth](https://img.shields.io/badge/dynamic/json?color=158000&label=downloads&prefix=+%20&query=downloads&url=https://api.modrinth.com/api/v1/mod/1eAoo2KR&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMSAxMSIgd2lkdGg9IjE0LjY2NyIgaGVpZ2h0PSIxNC42NjciICB4bWxuczp2PSJodHRwczovL3ZlY3RhLmlvL25hbm8iPjxkZWZzPjxjbGlwUGF0aCBpZD0iQSI+PHBhdGggZD0iTTAgMGgxMXYxMUgweiIvPjwvY2xpcFBhdGg+PC9kZWZzPjxnIGNsaXAtcGF0aD0idXJsKCNBKSI+PHBhdGggZD0iTTEuMzA5IDcuODU3YTQuNjQgNC42NCAwIDAgMS0uNDYxLTEuMDYzSDBDLjU5MSA5LjIwNiAyLjc5NiAxMSA1LjQyMiAxMWMxLjk4MSAwIDMuNzIyLTEuMDIgNC43MTEtMi41NTZoMGwtLjc1LS4zNDVjLS44NTQgMS4yNjEtMi4zMSAyLjA5Mi0zLjk2MSAyLjA5MmE0Ljc4IDQuNzggMCAwIDEtMy4wMDUtMS4wNTVsMS44MDktMS40NzQuOTg0Ljg0NyAxLjkwNS0xLjAwM0w4LjE3NCA1LjgybC0uMzg0LS43ODYtMS4xMTYuNjM1LS41MTYuNjk0LS42MjYuMjM2LS44NzMtLjM4N2gwbC0uMjEzLS45MS4zNTUtLjU2Ljc4Ny0uMzcuODQ1LS45NTktLjcwMi0uNTEtMS44NzQuNzEzLTEuMzYyIDEuNjUxLjY0NSAxLjA5OC0xLjgzMSAxLjQ5MnptOS42MTQtMS40NEE1LjQ0IDUuNDQgMCAwIDAgMTEgNS41QzExIDIuNDY0IDguNTAxIDAgNS40MjIgMCAyLjc5NiAwIC41OTEgMS43OTQgMCA0LjIwNmguODQ4QzEuNDE5IDIuMjQ1IDMuMjUyLjgwOSA1LjQyMi44MDljMi42MjYgMCA0Ljc1OCAyLjEwMiA0Ljc1OCA0LjY5MSAwIC4xOS0uMDEyLjM3Ni0uMDM0LjU2bC43NzcuMzU3aDB6IiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGZpbGw9IiM1ZGE0MjYiLz48L2c+PC9zdmc+)](https://modrinth.com/mod/yacl)
[![CurseForge](https://cf.way2muchnoise.eu/full_667299_downloads.svg)](https://curseforge.com/minecraft/mc-mods/yacl)

[![Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/isxander)

Yet Another Config Lib, like, what were you expecting?

</div>

## Why does this mod even exist?

This mod was made to fill a hole in this area of Fabric modding. The existing main config libraries don't achieve what I want from them:

- **[Cloth Config API](https://modrinth.com/mod/cloth-config)**:<br/>**It's stale.** The developer of cloth has clarified that they are likely not going to add any more features. They don't want to touch it.
- **[SpruceUI](https://github.com/LambdAurora/SpruceUI)**:<br/>**It isn't designed for configuration.** In this essence the design feels cluttered. Further details available in this issue.
- **[MidnightLib](https://modrinth.com/mod/midnightlib)**:<br/>**It's mostly bloat and doesn't have a focus on config.** This library includes other unwanted features such as cosmetics.
- **[OwoLib](https://modrinth.com/mod/owo-lib)**:<br/>**It's content focused.** Like MidnightLib, it does a lot of other things as well, adding to the size.

As you can see, there's sadly a drawback with all of them and this is where YetAnotherConfigLib comes in.

## How is YACL better?

YACL has the favour of hindsight. Whilst developing this fresh library, I can make sure that it does everything right:

- It's just a config library. YACL contains no other features, just config screen generation.
- It's lightweight. YACL leaves managing your config up to you, it doesn't contain an alternative to [AutoConfig](https://shedaniel.gitbook.io/cloth-config/auto-config/introduction-to-auto-config-1u) or similar. (Can be paired with my other library, [Settxi](https://github.com/isXander/Settxi), for this feature)
- Easy API. YACL takes inspiration from [Sodium's](https://modrinth.com/mod/sodium) internal configuration library.
- It's styled to fit in Minecraft. YACL's GUI is designed to fit right in.

## Usage

[The wiki](https://github.com/isXander/YetAnotherConfigLib/wiki/Usage) contains a full documentation on how to use YACL.

## Screenshots

<div align="center">

<img src="https://user-images.githubusercontent.com/77157639/188269356-dc13c12a-bd11-4e47-943d-e0e172c51c93.png">

</div>

## License

This mod is under the [GNU Lesser General Public License, v3.0](LICENSE).
