package tutorial;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import tutorial.client.gui.GuiCustomPlayerInventory;
import tutorial.client.gui.GuiMagicBag;
import tutorial.entity.ExtendedPlayer;
import tutorial.inventory.ContainerCustomPlayer;
import tutorial.inventory.ContainerMagicBag;
import tutorial.inventory.InventoryMagicBag;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy implements IGuiHandler
{
	public void registerRenderers() {}

	public int addArmor(String string) {
		return 0;
	}
	
	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z) {
		if (guiId == TutorialMain.GUI_CUSTOM_INV)  {
			return new ContainerCustomPlayer(player, player.inventory, ExtendedPlayer.get(player).inventory);
		} else if (guiId == TutorialMain.GUI_ITEM_INV)  {
			return new ContainerMagicBag(player, player.inventory, new InventoryMagicBag(player.getHeldItem()));
		} else {
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z) {
		if (guiId == TutorialMain.GUI_CUSTOM_INV) {
			return new GuiCustomPlayerInventory(player, player.inventory, ExtendedPlayer.get(player).inventory);
		} else if (guiId == TutorialMain.GUI_ITEM_INV)  {
			return new GuiMagicBag(player, player.inventory, new InventoryMagicBag(player.getHeldItem()));
		} else {
			return null;
		}
	}
}
