# Clue Combat Helper

A RuneLite plugin that shows combat advice for treasure trail clue scroll encounters.

## Features

- Detects hard, elite and master coordinate, emote and hot/cold steps that spawn an enemy
- Shows the enemy you'll face, its level, gear, what to bring and a short tip
- Reads the clue's enemy from RuneLite to show the exact enemy, or "either/or" when the spawn is random
- Master hot/cold: as you use the strange device, the panel narrows down which enemy the remaining dig spots spawn and whether they are in the Wilderness
- Once an either/or enemy appears (Armadylean or Bandosian guard, Brassican Mage or Ancient Wizards), the panel switches to advice for the one that spawned
- After the kill, the panel turns into a one-line reminder: "Dig again" for coordinate and hot/cold steps, or "Talk to Uri" for emote steps
- Wilderness warning for Wilderness steps, when you are in the Wilderness, and on PvP worlds
- Checks your inventory for food, antipoison and (for Wilderness steps) a teleport that works up to level 30 Wilderness
- Warns when your weapon can't hit the flying Armadylean guard
- Reminds you that each Feel of the master strange device costs 3-8 Hitpoints, with an optional low HP warning

## Supported encounters

- Saradomin wizard (hard, outside the Wilderness)
- Zamorak wizard (hard, Wilderness)
- Double agent (hard emote steps, level 108 outside and 65 inside the Wilderness; master emote steps, level 141)
- Armadylean guard and Bandosian guard (elite coordinates)
- Brassican Mage and Ancient Wizards (master coordinates and master hot/cold)

Beginner, easy and medium steps have no combat encounters, so the panel stays hidden for them.

## Settings

Display

- **Show overlay**: hide the panel without turning the plugin off
- **When to show**: Always (default), Only near the spot, or Only once the enemy has spawned
- **Near spot distance**: how many tiles counts as near, for "Only near the spot" (default 20)
- **Only Wilderness steps**: hide the panel unless the step is in the Wilderness (or you are in the Wilderness or on a PvP world)
- **Compact mode**: just the enemy name, level, gear and warnings
- **Show tips** and **Show Bring**: turn those lines on or off
- **Check inventory**: under Bring, list what you have and what's missing (food, antipoison, level 30 teleport)
- **After-kill reminder**: show "Dig again" or "Talk to Uri" once the enemy dies

Warnings

- **Wilderness warning**: the red Wilderness / PvP world line
- **Armadylean guard weapon warning**: warns if your equipped weapon can't hit the guard, and on either/or steps if you carry nothing that can
- **Low HP warning (hot/cold)** and **Low HP below**: warn on master hot/cold steps when your Hitpoints are at or below the chosen level (default 20)

Colours

- Background, title, level, gear, bring, tip, label and warning colours. The defaults are the original pink-on-teal theme.

The panel can be moved like any RuneLite overlay: hold Alt and drag it.

## Notes

- The level 30 teleport check looks for charged glory, ring of wealth, combat bracelet, skills necklace, slayer ring, eternal glory, Pharaoh's sceptre and royal or grand seed pods. Teleport tabs and spells also work, but only up to level 20 Wilderness, so they don't count for this check.
- Enemy levels and max hits come from the Old School RuneScape Wiki.
