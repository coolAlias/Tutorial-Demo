package tutorial.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ContainerMagicBag extends Container
{
	/** The Item Inventory for this Container */
	public final InventoryMagicBag inventory;

	private static final int
	ARMOR_START = InventoryMagicBag.INV_SIZE, ARMOR_END = ARMOR_START + 3,
	INV_START = ARMOR_END+1, INV_END = INV_START+26,
	HOTBAR_START = INV_END+1, HOTBAR_END = HOTBAR_START+8;

	public ContainerMagicBag(EntityPlayer player, InventoryPlayer inv, InventoryMagicBag bag)
	{
		int i = 0;
		inventory = bag;

		// CUSTOM INVENTORY SLOTS
		for (i = 0; i < InventoryMagicBag.INV_SIZE; ++i) {
			addSlotToContainer(new SlotMagicBag(inventory, i, 80 + (18*(i%5)), 8 + (18 * (int)(i/5))));
		}

		// ARMOR SLOTS
		for (i = 0; i < 4; ++i) {
			addSlotToContainer(new SlotArmor(player, inv, inv.getSizeInventory() - 1 - i, 8, 8 + i * 18, i));
		}

		// PLAYER INVENTORY - uses default locations for standard inventory texture file
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		// PLAYER ACTION BAR - uses default locations for standard action bar texture file
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inv, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			// If item is in our custom Inventory or an ARMOR slot
			if (par2 < INV_START)
			{
				// try to place in player inventory / action bar
				if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END+1, true))
				{
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			// Item is in inventory / hotbar, try to place in custom inventory or armor slots
			else
			{
				// Item being shift-clicked is armor - try to put in armor slot
				if (itemstack1.getItem() instanceof ItemArmor)
				{
					int type = ((ItemArmor) itemstack1.getItem()).armorType;
					if (!this.mergeItemStack(itemstack1, ARMOR_START + type, ARMOR_START + type + 1, false))
					{
						return null;
					}
				}

				// item is in inventory or action bar
				else if (par2 >= INV_START)
				{
					// place in custom inventory
					if (!this.mergeItemStack(itemstack1, 0, ARMOR_START, false))
					{
						return null;
					}
				}
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}
}