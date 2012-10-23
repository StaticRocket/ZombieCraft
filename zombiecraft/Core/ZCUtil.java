package zombiecraft.Core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import CoroAI.entity.EnumTeam;
import CoroAI.entity.c_EnhAI;

import zombiecraft.Core.Blocks.BlockMobSpawnerWave;
import zombiecraft.Core.Blocks.BlockPurchasePlate;
import zombiecraft.Core.Blocks.TileEntityMobSpawnerWave;
import zombiecraft.Core.Blocks.TileEntityPurchasePlate;
import zombiecraft.Core.Entities.EntityBullet;
import zombiecraft.Core.GameLogic.ZCGame;
import zombiecraft.Core.Items.ItemGun;
import zombiecraft.Forge.ZombieCraftMod;

import net.minecraft.src.*;

public class ZCUtil {
	
	public static String refl_mcp_Item_maxStackSize = "maxStackSize";
    public static String refl_c_Item_maxStackSize = "bU";
    public static String refl_s_Item_maxStackSize = "bU";
    public static String refl_mcp_Item_navigator = "navigator";
    public static String refl_c_Item_navigator = "ar";
    public static String refl_s_Item_navigator = "k";

    public static String refl_mcp_EntityPlayer_itemInUse = "itemInUse";
    public static String refl_c_EntityPlayer_itemInUse = "e";
    public static String refl_s_EntityPlayer_itemInUse = "e";
    public static String refl_mcp_EntityPlayer_itemInUseCount = "itemInUseCount";
    public static String refl_c_EntityPlayer_itemInUseCount = "f";
    public static String refl_s_EntityPlayer_itemInUseCount = "f";
    public static String refl_mcp_FoodStats_foodLevel = "foodLevel";
    public static String refl_c_FoodStats_foodLevel = "a";
    public static String refl_s_FoodStats_foodLevel = "a";
	
	
	
	public ZCUtil() {
		
	}
	
	public static Field s_getItemInUse() {
		return tryGetField(EntityPlayer.class, refl_s_EntityPlayer_itemInUse, refl_mcp_EntityPlayer_itemInUse);
	}
	
	public static Field s_getItemInUseCount() {
		return tryGetField(EntityPlayer.class, refl_s_EntityPlayer_itemInUseCount, refl_mcp_EntityPlayer_itemInUseCount);
	}
	
	public static Field s_getFoodLevel() {
		return tryGetField(FoodStats.class, refl_s_FoodStats_foodLevel, refl_mcp_FoodStats_foodLevel);
	}
	
	public static Field tryGetField(Class theClass, String obf, String mcp) {
		Field field = null;
		try {
			field = theClass.getDeclaredField(obf);
			field.setAccessible(true);
		} catch (Exception ex) {
			try {
				field = theClass.getDeclaredField(mcp);
				field.setAccessible(true);
			} catch (Exception ex2) { ex2.printStackTrace(); }
		}
		return field;
	}
	
	//apparently the tick updates are weird, this function is now unused....
	//this will only really be needed for when the user drops a gun while holding it, as item onUpdate calls if item isnt selected too
	public static boolean lastTickGunInHand = false;
	public static void handleGunUseBinds(boolean gunInHand) {
		lastTickGunInHand = gunInHand;
	}
	
	public static boolean shouldBulletPassThrough(EntityBullet bullet, int blockID) {
		if (blockID == 0 || blockID == Block.fence.blockID || blockID == Block.fenceIron.blockID || blockID == Block.fenceGate.blockID || blockID == ZCBlocks.barrier.blockID || (blockID >= ZCBlocks.barricadeS0.blockID && blockID <= ZCBlocks.barricadeS5.blockID)) {
			return true;
		}
		return false;
	}
	
	
	public static boolean shouldBulletHurt(EntityBullet bullet, Entity ent) {
    	if (ent instanceof c_EnhAI) {
    		if (bullet.owner instanceof c_EnhAI) {
    			//AI to AI
    			if (((c_EnhAI)bullet.owner).dipl_team != ((c_EnhAI)ent).dipl_team) {
    				return true;
    			} else {
    				return false;
    			}
    		} else {
    			//Player to AI
    			if (bullet.owner instanceof EntityPlayer && ((c_EnhAI)ent).dipl_team == EnumTeam.COMRADE) {
    				return false;
    			}
    			return true;
    		}
    	} else {
    		//Player to Player
    		if (bullet.owner instanceof EntityPlayer) {
    			if (ZCGame.instance() == null || ZCGame.instance().zcLevel == null) {
    				System.out.println("ZCGame.instance.zcLevel is null?!");
    				return true;
    			}
	    		if (ZCGame.instance().zcLevel.playersInGame.contains(ent)) {
	    			return ZCGame.instance().cfg_ff;
	    		}
	    	//AI to Player
    		} else if (bullet.owner instanceof c_EnhAI) {
    			if (ZCGame.instance().zcLevel.playersInGame.contains(ent)) {
	    			return false;
	    		}
    		}
    		return true;
    	}
    }
	
	public static void ammoDLCheck(EntityPlayer me, int ammoID) {
		
		if (!((AmmoDataLatcher)getData(me, DataTypes.ammoAmounts)).values.containsKey(ammoID)) {
			((AmmoDataLatcher)getData(me, DataTypes.ammoAmounts)).values.put(ammoID, 0);
		}
	}
	
	public static void setAmmoData(EntityPlayer ent, int ammoID, int ammoAmount) {
		ZCGame.instance().check(ent);
		ammoDLCheck(ent, ammoID);
		
		((AmmoDataLatcher)getData(ent, DataTypes.ammoAmounts)).values.put(ammoID, ammoAmount);
		
	}
	
	public static int getAmmoData(EntityPlayer ent, int ammoID) {
		ZCGame.instance().check(ent);
		ammoDLCheck(ent, ammoID);
		return (Integer)((AmmoDataLatcher)getData(ent, DataTypes.ammoAmounts)).values.get(ammoID);
	}
	
	public static void setData(EntityPlayer ent, DataTypes dtEnum, Object obj) {
		//System.out.println("set: " + ent.entityId);
		//DataLatcher dl = (DataLatcher)entFields.get(ent.entityId);
		//System.out.println("set: " + ent.entityId + "|" + ((DataLatcher)entFields.get(ent.entityId)).values.get(dtEnum) + " -> " + obj.toString());
		((DataLatcher)ZCGame.instance().entFields.get(ent.username)).values.put(dtEnum, obj);
	}
	
	public static Object getData(EntityPlayer ent, DataTypes dtEnum) {
		//DataLatcher dl = (DataLatcher)entFields.get(ent.entityId);
		//System.out.println("get: " + ent.entityId + "|" + ((DataLatcher)entFields.get(ent.entityId)).values.get(dtEnum));
		try {
			return ((DataLatcher)ZCGame.instance().entFields.get(ent.username)).values.get(dtEnum);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static void reload(EntityPlayer var0, int var1, World var2) {
		int ammoCount = getAmmoData(var0, var1);
		int clipSize = Item.itemsList[var1].getItemStackLimit();
		
		
		if (ammoCount > 0) {
			ammoCount -= ammoCount % clipSize;
			setAmmoData(var0, var1, ammoCount);
			
			//System.out.println("ammoCount: " + ammoCount);
			//System.out.println("ammoCount % clipSize: " + ammoCount % clipSize);
			
			System.out.println("RELOADING BROKEN!");
			//mod_ZCSdkGuns.reload(var2, (EntityPlayer)var0);
			
		}
	}
	
	//var1 = item ammo shifted index
	public static int useItemInInventory(EntityPlayer var0, ItemGun item)
    {
		//ammoDLCheck(var0, var1);
        //if data > 0: if data quantifies to clipsize: return 2, else: return 1;, else: return 0;
		//if ZCGame.instance.getData(var0, dtEnum)
		int ammoCount = getAmmoData(var0, item.ammoType.ordinal());
		int clipSize = item.magSize;
		
		System.out.println("ammoCount: " + ammoCount);
		
		//TEMP HACK FOR SERVER UNLIMITED AMMO, broken datalatcher
		//ammoCount = 250;
		
		if (ammoCount > 0) {
			ammoCount--;
			setAmmoData(var0, item.ammoType.ordinal(), ammoCount);
			try {
				ZCGame.instance().updateAmmoData(var0);
			} catch (Exception ex) {
				
				System.out.println("Caught!"); ex.printStackTrace();
			}
			
			//System.out.println("ammoCount: " + ammoCount);
			//System.out.println("ammoCount % clipSize: " + ammoCount % clipSize);
			
			//Reload!
			if (ammoCount % clipSize == 0 && ammoCount != 0) {
				return 2;
			} else {
				return 1;
			}
		}
		
		//temp unlimited
		//if (true) return 1;
		
		return 0;
    }
	
	public static int getAmmoCount(EntityPlayer var0, int var1)
    {
		int ammo = getAmmoData(var0, var1);
		return ammo;
    }
	
	/*public static int getClipCount(EntityPlayer var0, int var1)
    {
		int ammo = getAmmoData(var0, var1);
		return ammo;
    }*/
	
	public static int getInventorySlotContainItem(InventoryPlayer var0, int var1)
    {
        for (int var2 = 0; var2 < var0.mainInventory.length; ++var2)
        {
            if (var0.mainInventory[var2] != null && var0.mainInventory[var2].itemID == var1)
            {
                return var2;
            }
        }

        return -1;
    }
	
	public static boolean areBlocksMineable = true;
	public static float[] blockHardness;
	public static boolean areBlocksExplodable = true;
	public static float[] blockResistance;
	
	public static void setBlocksMineable(boolean var1)
    {
		
		//bug, if double set to true or false (happens when going from ssp to smp & vice versa), backup settings are set as unbreakable
		
		//var1 = true;
		//if (true) return;
		
		//prevent double setting breaking the backup
		if (areBlocksMineable == var1) return;
		
        try
        {
            int var2;

            if (!var1)
            {
                blockHardness = new float[Block.blocksList.length];

                for (var2 = 0; var2 < Block.blocksList.length; ++var2)
                {
                    if (Block.blocksList[var2] != null)
                    {
                        blockHardness[var2] = Block.blocksList[var2].getBlockHardness(null, 0, 0, 0);

                        if (var2 != Block.glass.blockID || !ZombieCraftMod.bulletsDestroyGlass)
                        {
                        	ZombieCraftMod.setHardness(var2, 6000000.0F);
                        }
                    }
                }
            }
            else if (blockHardness != null)
            {
                for (var2 = 0; var2 < Block.blocksList.length; ++var2)
                {
                    if (Block.blocksList[var2] != null)
                    {
                    	ZombieCraftMod.setHardness(var2, blockHardness[var2]);
                    }
                }
            }

            areBlocksMineable = var1;
        }
        catch (Exception var3)
        {
            ModLoader.getLogger().throwing("ZCUtil", "setBlocksMineable", var3);
            //ZCSdkTools.ThrowException(String.format("Error setting blocks mineable: %b.", new Object[] {Boolean.valueOf(var1)}), var3);
        }
    }
	
	public static void setPrivateValueBoth(Class var0, Object var1, String obf, String mcp, Object var3) {
    	try {
            try {
                setPrivateValue(var0, var1, obf, var3);
            } catch (NoSuchFieldException ex) {
                setPrivateValue(var0, var1, mcp, var3);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static Object getPrivateValueBoth(Class var0, Object var1, String obf, String mcp) {
    	try {
            try {
                return getPrivateValue(var0, var1, obf);
            } catch (NoSuchFieldException ex) {
                return getPrivateValue(var0, var1, mcp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static Object getPrivateValue(Class var0, Object var1, String var2) throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field var3 = var0.getDeclaredField(var2);
            var3.setAccessible(true);
            return var3.get(var1);
        }
        catch (IllegalAccessException var4)
        {
            ModLoader.throwException("An impossible error has occured!", var4);
            return null;
        }
    }

    static Field field_modifiers = null;

    public static void setPrivateValue(Class var0, Object var1, int var2, Object var3) throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field var4 = var0.getDeclaredFields()[var2];
            var4.setAccessible(true);
            int var5 = field_modifiers.getInt(var4);

            if ((var5 & 16) != 0)
            {
                field_modifiers.setInt(var4, var5 & -17);
            }

            var4.set(var1, var3);
        }
        catch (IllegalAccessException var6)
        {
            //logger.throwing("ModLoader", "setPrivateValue", var6);
            //throwException("An impossible error has occured!", var6);
        }
    }

    public static void setPrivateValue(Class var0, Object var1, String var2, Object var3) throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            if (field_modifiers == null)
            {
                field_modifiers = Field.class.getDeclaredField("modifiers");
                field_modifiers.setAccessible(true);
            }

            Field var4 = var0.getDeclaredField(var2);
            int var5 = field_modifiers.getInt(var4);

            if ((var5 & 16) != 0)
            {
                field_modifiers.setInt(var4, var5 & -17);
            }

            var4.setAccessible(true);
            var4.set(var1, var3);
        }
        catch (IllegalAccessException var6)
        {
            //logger.throwing("ModLoader", "setPrivateValue", var6);
            //throwException("An impossible error has occured!", var6);
        }
    }

	/*public void setBlocksExplodable(boolean var1)
    {
        try
        {
            int var2;

            if (!var1)
            {
                blockResistance = new float[Block.blocksList.length];

                for (var2 = 0; var2 < Block.blocksList.length; ++var2)
                {
                    if (Block.blocksList[var2] != null)
                    {
                        blockResistance[var2] = Block.blocksList[var2].blockResistance;

                        if (var2 != Block.glass.blockID || !mod_SdkGuns.bulletsDestroyGlass)
                        {
                            Block.blocksList[var2].blockResistance = 6000000.0F;
                        }
                    }
                }
            }
            else if (blockResistance != null)
            {
                for (var2 = 0; var2 < Block.blocksList.length; ++var2)
                {
                    if (Block.blocksList[var2] != null)
                    {
                        Block.blocksList[var2].blockResistance = blockResistance[var2];
                    }
                }
            }

            areBlocksExplodable = var1;
        }
        catch (Exception var3)
        {
            ModLoader.getLogger().throwing("mod_SdkFps", "setBlocksExplodable", var3);
            SdkTools.ThrowException(String.format("Error setting blocks explodable: %b.", new Object[] {Boolean.valueOf(var1)}), var3);
        }
    }*/
    
    
	
}
