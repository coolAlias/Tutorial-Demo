package tutorial.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tutorial.item.ItemUseMana;

public class SlotCustom extends Slot
{
	public SlotCustom(IInventory inventory, int par2, int par3, int par4)
	{
		super(inventory, par2, par3, par4);
	}
	
	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots
	 * (and now also not always true for our custom inventory slots)
	 */
	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		// We only want our custom item to be storable in this slot
		return itemstack.getItem() instanceof ItemUseMana;
	}
}
