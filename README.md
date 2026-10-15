# Zenkai Ore Cycle — source starter

Target: Minecraft 1.20.2, Forge 48.1.0, Java 17.

## Hotkeys
- **K**: start and issue a Baritone `#mine` command.
- **J**: stop and issue `#stop`.
- **H**: drop non-allowlisted stacks in main inventory slots (hotbar is deliberately untouched).

## Important: this is not yet the complete unattended automation
This source starter does not automatically place/craft/break/recover a crafting table, verify inventory-full status, or deposit into `/backpack` Personal Vault #1/#2. The ZenkaiSMP custom vault screen has to be mapped before automating clicks safely. The current target rotation is a 10-minute timer, not triggered by a verified successful vault cycle; do not rely on it unattended. Check the exact Baritone command syntax on your installed version. Item cleanup identifies Senzu Beans by an item description containing `senzu`; server-specific naming may require adjustment.

Build with Java 17 and Gradle (`gradle build`) or import into IntelliJ IDEA as a Gradle project. The jar should be created under `build/libs/`.
