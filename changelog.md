# YetAnotherConfigLib 3.7.0

This version of YACL has many different version targets.
Ensure you download the correct version of YACL for your MC version.

- Fabric 1.21.6 (snapshots)
- Fabric 1.21.5
- Fabric 1.21.4
- Fabric 1.21.2 (also supports 1.21.3)
- Fabric 1.20.1
- Fabric 1.20.4
- Fabric 1.20.6 (also supports 1.20.5)
- Fabric 1.21.1
- NeoForge 1.21.4
- NeoForge 1.21.2 (also supports 1.21.3)
- NeoForge 1.21.1

## New features

- Add toggle to prevent loading .webp and .gif at resource reload.
    - When disabled, the images will be loaded on-demand when a YACL screen is opened.
    - It is disabled by default now. This should improve startup time with mods that use such images.

## Changes

- Add 1.21.6 target (25w21a)
- Removed all the 1.20.x targets.
    - YACL now only supports 1.21.1 and later.

## Bug fixes

- Fixes related to the colour picker widget
    - The YACLScreen now continues to tick when the colour picker is open.
    - Clicking outside the colour picker now closes it (without the click being consumed by the screen)
    - Fix requiring a double click to open a colour picker after it has been closed.
