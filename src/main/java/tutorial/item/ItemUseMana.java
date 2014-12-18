package tutorial.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tutorial.TutorialMain;
import tutorial.entity.ExtendedPlayer;

public class ItemUseMana extends BaseModItem
{
	public ItemUseMana() {
		super();
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			ExtendedPlayer props = ExtendedPlayer.get(player);
			if (props.consumeMana(15)) {
				TutorialMain.logger.info("Player had enough mana. Do something awesome!");
			} else {
				TutorialMain.logger.info("Player ran out of mana. Sad face.");
				props.replenishMana();
			}
		}

		return stack;
	}
}