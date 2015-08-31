package info.skydark.maidsmith;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityModeBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.Random;

/**
 * Created by skydark on 15-8-30.
 */
public class SkyD_EntityMode_Smith extends LMM_EntityModeBase{
    public static final int mmode_Smith = 0x1428;
    public static final int mmode_Smith_Follower = 0x1427;

    private int waittick = -1;

    public SkyD_EntityMode_Smith(LMM_EntityLittleMaid pEntity) {
        super(pEntity);
    }

    @Override
    public int priority() {
        return 5714;
    }

    @Override
    public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
        EntityAITasks[] ltasks = new EntityAITasks[2];
        ltasks[0] = pDefaultMove;
        ltasks[1] = new EntityAITasks(owner.aiProfiler);
        owner.addMaidMode(ltasks, "Smith", mmode_Smith);
        owner.addMaidMode(ltasks, "SmithFollower", mmode_Smith_Follower);
    }

    @Override
    public boolean changeMode(EntityPlayer pentityplayer) {
        ItemStack litemstack = owner.maidInventory.getStackInSlot(0);
        if (litemstack != null) {
            if (litemstack.getItem() == Item.getItemFromBlock(Blocks.anvil)) {
                ItemStack stack = owner.maidInventory.getStackInSlot(1);
                if (stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.bookshelf)) {
                    owner.setMaidMode("Smith");
                } else {
                    owner.setMaidMode("SmithFollower");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setMode(int pMode) {
        switch (pMode) {
            case mmode_Smith :
                owner.setSitting(true);
                owner.aiWander.setEnable(false);
                owner.aiFollow.setEnable(false);
                owner.aiJumpTo.setEnable(false);
                owner.aiAvoidPlayer.setEnable(false);
            case mmode_Smith_Follower:
                owner.setBloodsuck(false);
                owner.aiAttack.setEnable(false);
                owner.aiShooting.setEnable(false);
                waittick = -1;
                return true;
        }
        return false;
    }

    @Override
    public int getNextEquipItem(int pMode) {
        switch (pMode) {
            case mmode_Smith :
            case mmode_Smith_Follower:
                for (int i = 0; i < owner.maidInventory.maxInventorySize; i++) {
                    ItemStack item = owner.maidInventory.getStackInSlot(i);
                    if (isItemRepairable(item)) {
                        return i;
                    }
                }
                break;
        }
        return -1;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1nbtTagCompound) {
        waittick = par1nbtTagCompound.getInteger("waittick");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1nbtTagCompound) {
        par1nbtTagCompound.setInteger("waittick", waittick);
    }

    @Override
    public boolean checkItemStack(ItemStack pItemStack) {
        return isItemRepairable(pItemStack);
    }

    private boolean isItemRepairable(ItemStack stack) {
        return stack != null && stack.isItemDamaged() && stack.getItem().isRepairable() && stack.getItemDamage() > 0;
    }

    @Override
    public void onUpdate(int pMode) {
        if (pMode != mmode_Smith && pMode != mmode_Smith_Follower) {
            return;
        }
        if (waittick > 0) {
            waittick--;
            return;
        }
        int enchantability = 0;
        if (waittick == 0) {
            World worldObj = owner.worldObj;
            if (worldObj.isRemote) {
                return;
            }
            ItemStack tool = owner.getEquipmentInSlot(0);
            if (isItemRepairable(tool)) {
                int x = (int) (owner.posX + 0.5);
                int y = (int) owner.posY;
                int z = (int) (owner.posZ + 0.5);
                float power = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (!(i == 0 && j == 0)
                                && worldObj.isAirBlock(x + i, y, z + j)
                                && worldObj.isAirBlock(x + i, y + 1, z + j)) {
                            power += ForgeHooks.getEnchantPower(worldObj, x + i * 2, y, z + j * 2);
                            power += ForgeHooks.getEnchantPower(worldObj, x + i * 2, y + 1, z + j * 2);
                            if (i != 0 && j != 0) {
                                power += ForgeHooks.getEnchantPower(worldObj, x + i * 2, y, z + j);
                                power += ForgeHooks.getEnchantPower(worldObj, x + i * 2, y + 1, z + j);
                                power += ForgeHooks.getEnchantPower(worldObj, x + i, y, z + j * 2);
                                power += ForgeHooks.getEnchantPower(worldObj, x + i, y + 1, z + j * 2);
                            }
                        }
                    }
                }
                enchantability = tool.getItem().getItemEnchantability(tool);
                int bookpower = (int) (power + .5);
                int recover = (int) (Config.repairRate
                        * logistic(Config.bookshelfMultiplierLimit, Config.bookshelfFactor, bookpower)) + 1;
                tool.setItemDamage(tool.getItemDamage() - recover);
                if (tool.getItemDamage() == 0) {
                    owner.getNextEquipItem();
                }

            }
        }
        waittick = (int) (Config.waitTick / logistic(Config.enchantMultiplierLimit, Config.enchantFactor, enchantability)) + 10;
    }

    public static double logistic(double max, double base, double x) {
        return (2 / (1 + Math.pow(base, x)) - 1) * (max - 1) + 1;
    }
}
