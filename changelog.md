# YetAnotherConfigLib 3.7.0

### 3.7.0 targets

|        | Fabric   | NeoForge | Forge |
|--------|----------|----------|-------|
| 1.21.6 | ✅        | ✅        | ❌     |
| 1.21.5 | ✅        | ✅        | ❌     |
| 1.21.4 | ✅        | ✅        | ❌     |
| 1.21.3 | ✅        | ✅        | ❌     |
| 1.21.2 | ✅ 1.21.3 | ✅ 1.21.3 | ❌     |
| 1.21.1 | ✅        | ✅        | ❌     |
| 1.20.6 | ❌        | ❌        | ❌     |
| 1.20.4 | ❌        | ❌        | ❌     |
| 1.20.1 | ❌        | ❌        | ❌     |

## New features

- Add toggle to prevent loading .webp and .gif at resource reload.
    - When disabled, the images will be loaded on-demand when a YACL screen is opened.
    - It is disabled by default now. This should improve startup time with mods that use such images.
- You can now set the default collapsed state in option groups via the Kotlin DSL. You could already do this via Java.

## Changes

- Add 1.21.6 target
- Removed all the 1.20.x targets.
    - YACL now only supports 1.21.1 and later.
- Allow the use of horizontal mouse scrolling in the options list. ([kevinthegreat1`#271`](https://github.com/isXander/YetAnotherConfigLib/pull/271))

## Bug fixes

- Fixes related to the colour picker widget
    - The YACLScreen now continues to tick when the colour picker is open.
    - Clicking outside the colour picker now closes it (without the click being consumed by the screen)
    - Fix requiring a double click to open a colour picker after it has been closed.
- Fix the last option sometimes being cut off in the options list. ([kevinthegreat1`#271`](https://github.com/isXander/YetAnotherConfigLib/pull/271))
- Fix NullPointerException when using `null` with `InstantStateManager`.

## Language updates

- New Argentine Spanish translation. ([Texaliuz`#267`](https://github.com/isXander/YetAnotherConfigLib/pull/267))
- Update Chinese translation. ([AC19970`#268`](https://github.com/isXander/YetAnotherConfigLib/pull/268))
- Update Polish translation. ([lumiscosity`#269`](https://github.com/isXander/YetAnotherConfigLib/pull/269))
- Update Brazilian Portuguese translation. ([seriousfreezing`#272`](https://github.com/isXander/YetAnotherConfigLib/pull/272))
