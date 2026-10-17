# Zenkai Ore Cycle

Minecraft 1.20.2, Forge 48.1.0, Java 17. Client-side only. Requires Baritone (separate mod).

## The loop (starts with gold, then coal, forever)
1. Baritone mines (`#mine gold_ore deepslate_gold_ore` or the coal version).
2. When no more ore fits in the inventory it sends `#stop`.
3. `/craft` opens a crafting screen. Ore is crafted into raw gold blocks / coal blocks, keeping a reserve of at least 45 ore.
4. `/vault` opens Personal Vaults. Gold blocks + reserve go in Vault #2, coal blocks + reserve go in Vault #1. The screen is closed.
5. It switches to the other ore and goes back to step 1.

Hotbar slots 1-3 (pickaxe, senzu beans, crafting tables) are never moved, mined into or dropped.
While mining, common junk (cobble, stone, dirt, gravel, rotten flesh, bones...) is dropped every 5 seconds so it cannot fill the inventory.
If the ore count does not grow for 2 minutes, the mine command is sent again.

## Keys (rebind under Options > Controls > Zenkai Ore Cycle)
- **Z** start / stop the loop
- **X** stop everything and send `#stop`
- **C** drop every non-allowlisted stack in the main inventory (manual cleanup)

Keys do nothing while chat or a menu is open.

## It stops by itself (and tells you in chat) when
you die; `/craft` or `/vault` does not open what it expects; the crafting recipe is not unlocked; a vault is full;
or the inventory is still nearly full after storing everything.

## Settings
`config/zenkaiorecycle.properties` is created on first launch: `vault_command`, `craft_command`, `spare_ore` (45),
`mine_gold_command`, `mine_coal_command`, `stop_command`. Edit and restart the game, no rebuild needed.

## Build
Push this folder to a GitHub repo. `.github/workflows/build.yml` builds it and publishes the jar as the
`zenkaiorecycle-jar` artifact on the Actions tab. Or run `gradle build` with Java 17 and take the jar from `build/libs/`.
