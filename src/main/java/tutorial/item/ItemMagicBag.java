package tutorial.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tutorial.TutorialMain;
import tutorial.inventory.InventoryMagicBag;

public class ItemMagicBag extends BaseModItem
{
	public ItemMagicBag() {
		super();
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabMisc);
	}

	// Without this method, your inventory will NOT work!!!
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1; // return any value greater than zero
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (!player.isSneaking()) {
				player.openGui(TutorialMain.instance, TutorialMain.GUI_ITEM_INV, player.worldObj, 0, 0, 0);
			} else {
				new InventoryMagicBag(player.getHeldItem()).setInventorySlotContents(0, new ItemStack(Items.diamond, 4));
			}
		}
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
		list.add(EnumChatFormatting.ITALIC + "A magic bag that holds many items");
	}
}