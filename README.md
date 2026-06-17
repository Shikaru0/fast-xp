# FastXP

A client-side open source mod that auto-throws projectiles faster when holding the throw/use key.

## Features

- **Auto-throws** when holding right-click - throw speed is based on the throwDelay
- **Works with all projectiles** - If it implements `ProjectileItem`, it's supported
- **Configuration** - use the Mod Menu config screen or edit `config/fastxp.json` directly
- **Server opt-out** - sends a notification on join, and the server can remotely disable the mod (action 0 = soft disable for the session, action 1 = kick with a message)

## Configuration

A `config/fastxp.json` file is created on first launch. You can edit it manually or use the Mod Menu config screen.

```json
{
  "enabled": true,
  "throwDelay": 2,
  "projectiles": []
}
```

- `enabled` - toggles the mod on and off
- `throwDelay` - ticks between throws
- `projectiles` - list of enabled projectile IDs. Empty = all projectiles work

## Dependencies

**_Mod Menu_** is optional but recommended for the config screen.
