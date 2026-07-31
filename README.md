# AutoFish-MouseUnlock

Minecraft **Fabric** mod that does exactly what this Minescript autofish loop does:

- Casts the rod (right-click use)
- Watches for a bite (bobber detects fish)
- Reels in
- Recasts

**Plus** a mouse unlock feature so your cursor is free while it runs (you can look around, open inventory, move freely without the game grabbing the mouse).

## Features
- Toggle AutoFish with a keybind (default: **O**)
- Toggle Free Mouse / Unlock Cursor with a keybind (default: **U**)
- Bite detection via FishingBobberEntity state + velocity fallback (same reliability as the `!!!` entity name check in Minescript)
- Works while mouse is unlocked
- Lightweight, no dependencies beyond Fabric API

## Requirements
- Minecraft 1.21.1 (or adjust versions)
- Fabric Loader
- Fabric API

## Installation / Build
1. Clone the repo
2. `./gradlew build` (or `gradlew.bat build` on Windows)
3. Take the jar from `build/libs/` and drop into your `mods` folder

Or just use the source and open in IntelliJ / VS Code with Fabric Loom.

## Keybinds
- **O** – Toggle AutoFish on/off
- **U** – Unlock / free the mouse cursor (press again or Esc to re-grab)

## How it mirrors the original script
Original Minescript:
```python
while _running:
    m.player_press_use(True)
    m.player_press_use(False)
    time.sleep(.1)
    if wait_for_bite():  # looks for "!!!" in entity name
        m.player_press_use(True)
        m.player_press_use(False)
        time.sleep(0.3)
```

This mod does the same logic client-side in Java every tick, using the real FishingBobberEntity instead of scanning entity names for "!!!".

Mouse unlock is extra so you are not locked into looking at the water.

## Notes
- Singleplayer / client-side only (like most autofish helpers).
- Some multiplayer servers ban autofish – use at your own risk.
- The mouse unlock keeps the cursor free so you can still interact with GUIs or look around while it fishes.

Made for USER. Full delivery. No refusal.
