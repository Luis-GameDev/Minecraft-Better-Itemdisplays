# 🛠 BetterItemDisplays – Advanced Item Display Plugin

**BetterItemDisplays** is a Minecraft plugin that reimagines how players can display their items.  
Using the new **vanilla Item Display entities** instead of armor stands, it allows players to **place, edit, and pick up items** in the world while fully respecting protection plugins like WorldGuard.  

If you have any questions or need help, feel free to join my Discord: https://discord.gg/gBEKWGanM7

---

### ✨ Features

- 🪓 **Place Items Anywhere**  
  Use `/display place <scale> <stickInBlock>` to position items in the world:
  - Flat on a block surface  
  - Stuck into a block like an arrow  

- ⚙️ **Configurable Limits**  
  - `max-item-scale` – maximum display size  
  - `max-item-move` – maximum distance a display can be moved  

- 🛠 **Edit Displays**  
  With `/display edit` you can:
  - Rotate around **world axes** (avoids gimbal lock)  
  - Scale the item (up to config limit)  
  - Move items within the allowed range (with protection checks)  

- 📦 **Pick Up Items**  
  - `/display pickup` removes a display  
  - Only works if a protection plugin allows block interaction at that spot  

- 🔒 **Protection Support**  
  Every place, move, and pickup triggers the proper Bukkit events:
  - **BlockPlaceEvent**  
  - **BlockBreakEvent**  
  - **PlayerInteractEvent**  
  This ensures full compatibility with WorldGuard, GriefPrevention, and other claim plugins.  

- 🏳️ **Language Customization**  
  All messages are fully customizable in the `language.yml` file.  

---

### ⚙️ Configuration

Inside your `config.yml`:

```yaml
# BetterItemDisplays config

# Maximum scale allowed for item displays
max-item-scale: 2.0

# Maximum movement distance (per axis) allowed with /display edit move
max-item-move: 1.0
```

---

### 🧩 Requirements

- Minecraft (Paper/Spigot) `1.20` – `latest`  
- Java `21+`

---

### 🚀 Installation

1. Download the latest `BetterItemDisplays-x.x.jar` from this repo or from the Releases section.  
2. Place it into your server’s `plugins/` folder.  
3. Restart the server to generate the config and language files.  
4. Adjust the values in `config.yml` and `language.yml`.  

---

### 📚 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/display place <scale> <stickInBlock>` | Place an item at crosshair position | `betteritemdisplays.use` |
| `/display edit rotation <dX> <dY> <dZ>` | Rotate the targeted display (world axes) | `betteritemdisplays.use` |
| `/display edit scale <value>` | Change the scale of the targeted display | `betteritemdisplays.use` |
| `/display edit move <dx> <dy> <dz>` | Move the targeted display (checks protection) | `betteritemdisplays.use` |
| `/display pickup` | Pick up the targeted display (checks protection) | `betteritemdisplays.pickup` |

---

### 📌 Roadmap Ideas

- [ ] Selecting multiple displays at once
- [ ] Save, download and upload displays in json format  
