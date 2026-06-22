# Damage Tint
![Damage Tint](/media/damage-tint.png)

A client-side mod that adds a red vignette around the screen when your health gets low. The lower your health, the stronger the tint becomes.

## Mod Info
### Commands
- `/tint`: Shows the current configuration.
- `/tint <health>`: Sets the health threshold in HP.
- `/tint dynamic`: Shows whether Dynamic Mode is enabled.
- `/tint dynamic <true|false>`: Enables or disables Dynamic Mode.
- `/tint reset`: Resets all settings to their defaults.

### Threshold
The threshold determines when the tint starts appearing.
Examples:
- `/tint 10`: Tint starts at 10 HP (5 hearts).
- `/tint 6`: Tint starts at 6 HP (3 hearts).

### Dynamic Mode
When enabled, the configured threshold is ignored and your current **maximum health** is used instead.
Examples:
- 20 max HP (10 hearts): Tint scales from 20 HP down to 0 HP.
- 40 max HP (20 hearts): Tint scales from 40 HP down to 0 HP.
This is useful with mods that increase or decrease your maximum health, as the effect automatically scales with your health pool.

### Configuration
Settings are stored in `config/damage_tint.json`.
The configuration is global and applies to every world and server.

## Compiling
- Clone this repo.
- Follow the fabric docs for setup/build: https://docs.fabricmc.net/1.21.11/develop/
  
