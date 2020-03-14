# TW-Core
Core plugin that introduces a variety of gameplay changes and mechanics as well as serves as a library for the other plugins to use. Handles spawning, creating and selecting NPCs as well as managing player ownership better than Citizens without the need for Admin intervention. Built specifically for TW:R.

v. 1.4.0

# CHANGELOG: 
Implements:

- Gameplay Additions:
  - Arrow flamibility
  - Buffing bow power
  - Anti enchant
  - Custom crafting
  - Weapon and tool upgrades
  - Key locking
  - Faster rails
  - Food storage and rot
  - Calendar
  - Playtime counter

- Libraries:
  - ArrayWrapper lib
  - ConfigManager lib
  - Noteblock API
  - Enumerator
  
Planned:

- NPC Additions:
  - Farmers
  - Caravan

- Gameplay Additions:
  - Circumnavigation
  - Inventory weight
  
- NPC Additions:
  - Cargo load/unload
  - Builder
  - Better unit selection
  
- Libraries
  - NPC Utils
  - Multiblock API
  
 # Enumerator
 
Features
- Allows 1.8.8-1.13.2 material names to work on any 1.8.8-1.13.2 server
- Backwards and forwards compatibility (as long as the material exists)
- Regular Potions, Splash Potions, Lingering Potions
- Tipped Arrows
- Enchantment Books
- Colored Leather Armor

Usage
- UMaterial.valueOf(<enum name as a string>)
- UMaterial.valueOf(String materialName, byte data)
- UMaterial.<material name>.getItemStack()
- UMaterial.<material name>.getMaterial()
- You could also use the getEnchantmentBook function to get an Enchanted Book with 1 or more enchants
- You could also use the getColoredLeather function to get a leather helmet, chestplate, leggings, or boots with a custom color. (getColoredLeather(Material leatherMaterial, int amount, int red, int green, int blue))
  
