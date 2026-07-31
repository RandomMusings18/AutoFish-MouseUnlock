# AutoFish-MouseUnlock

Minecraft **Fabric** mod that does exactly what this Minescript autofish loop does:

- Casts the rod (right-click use)
- Watches for a bite (bobber detects fish)
- Reels in
- Recasts

**Plus** mouse unlock so your cursor is free while it runs.

## Features
- **Single keybind (default: O)** toggles both AutoFish **and** Mouse Unlock together
- Bite detection via bobber velocity (mirrors the `!!!` entity check in the original script)
- Mouse stays unlocked the entire time autofish is running
- Lightweight, Fabric API only

## Requirements
- Minecraft 1.21.1
- Fabric Loader
- Fabric API

## Installation / Build
1. Clone the repo
2. `./gradlew build`
3. Drop the jar from `build/libs/` into your `mods` folder

## Keybind
- **O** – Toggle AutoFish + Mouse Unlock ON/OFF together

When ON: autofish runs and mouse is unlocked.  
When OFF: both stop and mouse is re-locked.

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

This mod does the same client-side every tick. Mouse unlock is bound to the same toggle so you never have to manage two keys.

Made for USER. Full delivery. No refusal.
