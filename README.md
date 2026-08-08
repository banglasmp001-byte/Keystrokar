# Multi-Action Keybind

A **PojavLauncher-inspired multi-action touch keybind system** for **Minecraft Java Edition 1.21.1** built with Fabric.

Create **unlimited** custom on-screen touch buttons. Each button can trigger up to **4** keyboard keys and/or mouse actions simultaneously — perfect for mobile-style macro setups, accessibility, or stream decks.

---

## Features

- ✅ **Unlimited Main Buttons** — no cap on how many you can create
- ✅ **Up to 4 actions per button** — keyboard keys and/or mouse clicks
- ✅ **On-screen overlay** — movable, resizable, adjustable opacity buttons
- ✅ **Edit mode** — drag buttons anywhere on screen
- ✅ **Mod Menu integration** — configure directly from Mod Menu
- ✅ **Persistent config** — survives restarts, world/server changes
- ✅ **Client-side only** — works on any server
- ✅ **Lightweight** — minimal performance impact

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Minecraft | 1.21.1 (Java Edition) |
| Java | 21+ |
| Fabric Loader | 0.16.9+ |
| Fabric API | 0.102.0+1.21.1 |
| Mod Menu | 11.0.3 (optional, recommended) |

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.1
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21.1
3. (Optional) Download [Mod Menu](https://modrinth.com/mod/modmenu) for 1.21.1
4. Download `multiactionkeybind-1.0.0.jar` from [Releases](../../releases) or build it yourself
5. Place all JAR files in your `.minecraft/mods/` folder
6. Launch Minecraft 1.21.1 with Fabric

---

## Mod Menu Configuration

With Mod Menu installed:

1. Open Minecraft → **Mods** (from the main menu or pause menu)
2. Find **Multi-Action Keybind** in the list
3. Click **Configure** (the settings gear icon)
4. The full configuration screen opens

Without Mod Menu, use the keybind **Numpad Enter** (default) to open the config screen in-game.

---

## Keybinds (Defaults)

| Action | Default Key |
|--------|-------------|
| Toggle Overlay | Numpad 0 |
| Open Config Screen | Numpad Enter |
| Toggle Edit Mode | Numpad . (Decimal) |

These can be changed in Minecraft's **Options → Controls → Keybinds** under the **Multi-Action Keybind** category.

---

## How to Create a Main Button

1. Open the **Config Screen** (Numpad Enter or Mod Menu)
2. Click **+ Add Main Button**
3. Click the **Main Button** field and press the key you want as the trigger
4. Add up to **4 actions** — click `+ Add Action` and press the desired key/mouse button
5. Adjust **Position**, **Size**, and **Opacity** with the sliders
6. Toggle **Enabled: ON/OFF**
7. Click **Save**

---

## How to Add Actions (Up to 4)

In the Edit Button screen:

- **Slot 1–4**: Click an empty slot, press any key or mouse button
- To **remove** an action: click the **X** button next to it
- When all 4 slots are filled, `+ Add Action` is disabled and a warning shows:
  > Maximum 4 actions per button

### Supported Action Types

**Keyboard:**
A–Z, 0–9, Space, Shift (L/R), Ctrl (L/R), Alt (L/R), Tab, Enter, Escape, Backspace, Delete, Arrow Keys, F1–F12, Home, End, Page Up/Down, Insert, Caps Lock, Numpad 0–9, and common symbols.

**Mouse:**
Left Click, Right Click, Middle Click, Mouse 4, Mouse 5.

---

## How to Move / Resize Buttons

### During Gameplay

1. Press **Numpad .** to enter **Edit Mode** (a yellow `[EDIT MODE]` indicator appears)
2. **Click and drag** any button to reposition it
3. Press **Numpad .** again to exit Edit Mode (position is auto-saved)

### In Config Screen

Use the **Position X / Y** sliders in the Edit Button screen for precise placement.
Use the **Size** slider (24–120 px) to resize.

---

## Configuration File Location

The config is stored at:

```
<minecraft-dir>/config/multiactionkeybind.json
```

Example (`%AppData%\.minecraft\config\multiactionkeybind.json` on Windows):

```json
{
  "overlayVisible": true,
  "editMode": false,
  "buttons": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "triggerKeyCode": 82,
      "triggerType": "keyboard",
      "triggerDisplayName": "R",
      "posX": 120.0,
      "posY": 200.0,
      "size": 48.0,
      "opacity": 0.8,
      "enabled": true,
      "actions": [
        { "actionType": "keyboard", "code": 49, "displayName": "1" },
        { "actionType": "mouse",    "code": 0,  "displayName": "LEFT CLICK" }
      ]
    }
  ]
}
```

If the config is missing or corrupted, the mod recovers gracefully by starting fresh.

---

## GitHub Build Instructions

### Prerequisites (Local Build)

- Java 21 JDK installed
- Git

### Clone and Build

```bash
git clone https://github.com/YourUser/multiactionkeybind.git
cd multiactionkeybind

# Linux/macOS
chmod +x gradlew
./gradlew build

# Windows
gradlew.bat build
```

> **Note:** Before building locally, download the real `gradle-wrapper.jar`:
> ```bash
> curl -fsSL "https://raw.githubusercontent.com/gradle/gradle/v8.8.0/gradle/wrapper/gradle-wrapper.jar" \
>   -o gradle/wrapper/gradle-wrapper.jar
> ```

### JAR Output Location

```
build/libs/
├── multiactionkeybind-1.0.0.jar          ← Install this one
├── multiactionkeybind-1.0.0-sources.jar  ← Sources (optional)
└── multiactionkeybind-1.0.0-dev.jar      ← Dev/deobf (not for installation)
```

Install **`multiactionkeybind-1.0.0.jar`** (the one without `-sources` or `-dev`).

---

## GitHub Actions

### Automatic Build

Every push to `main`/`master` triggers a build via GitHub Actions.

1. Go to your repository on GitHub
2. Click **Actions** tab
3. Find the **Build Fabric Mod** workflow
4. Click on a run → **Artifacts** section
5. Download `multiactionkeybind-release` — this is the installable JAR

### Manual Trigger

1. Go to **Actions** → **Build Fabric Mod**
2. Click **Run workflow** → **Run workflow**

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Overlay not visible | Press **Numpad 0** to toggle the overlay |
| Config screen won't open | Make sure you're in-game (not in a menu); try **Numpad Enter** |
| Buttons not executing | Check that actions are configured and button is **Enabled: ON** |
| Duplicate key conflict | The mod warns about duplicate trigger keys but doesn't crash |
| Config corrupted | Delete `config/multiactionkeybind.json` — it regenerates fresh |
| Build fails: wrapper jar | Run `gradle wrapper --gradle-version 8.8` or download the jar manually |
| `ClassNotFoundException` | Ensure Fabric API is installed alongside the mod |
| Mod not in Mod Menu | Ensure Mod Menu 11.x is installed for 1.21.1 |

---

## License

MIT License — see [LICENSE](LICENSE) file.

---

## Credits

Inspired by the multi-action touch keybind system in [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher).  
This is an **independent** Fabric mod implementation using only Fabric/Minecraft APIs.
