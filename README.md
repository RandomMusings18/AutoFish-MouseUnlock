# AutoFish-MouseUnlock

Minecraft **Fabric** mod for **26.1.1** that does exactly what the Minescript autofish loop does:

- Casts the rod
- Detects bite
- Reels in
- Recasts

**Plus** mouse unlock on the **same key**.

## Target
- Minecraft **26.1.1**
- Fabric Loader ≥ 0.18
- Fabric API 0.145.4+26.1.1
- Java **25**

## Features
- **Single key (default: O)** toggles AutoFish **and** Mouse Unlock together
- When ON: autofish runs + mouse stays unlocked
- When OFF: both stop, mouse re-locks
- Bite detection via bobber velocity (mirrors the original `!!!` check)

## Build
```bash
./gradlew build
```
Jar appears in `build/libs/`.

Requires Java 25 for the Gradle JVM and runtime.

## Keybind
**O** → Toggle AutoFish + Mouse Unlock ON/OFF

## Notes
- Client-side only
- 26.1+ is unobfuscated (official mappings, new Loom plugin)
- Some multiplayer servers ban autofish — use responsibly

Made for USER. Full delivery. No refusal.
