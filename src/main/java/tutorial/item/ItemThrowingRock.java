package tutorial.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tutorial.entity.EntityThrowingRock;

public class ItemThrowingRock extends BaseModItem
{
	public ItemThrowingRock() {
		super();
		setMaxStackSize(18);
		setCreativeTab(CreativeTabs.tabCombat);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
		}
		world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		if (!world.isRemote) {
			world.spawnEntityInWorld(new EntityThrowingRock(world, player));
		}
		
		return stack;
	}
}
