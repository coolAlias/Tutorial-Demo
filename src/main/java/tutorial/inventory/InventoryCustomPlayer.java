package tutorial.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import tutorial.item.ItemUseMana;

/**
 * 
 * The abstract inventory takes care of most things already, so we only have
 * a few methods to implement.
 * 
 * Additionally, since this inventory may need to be copied (e.g. for persisting
 * IExtendedEntityProperties class), we will add a copy method.
 *
 */
public class InventoryCustomPlayer extends AbstractInventory
{
	/** The name your custom inventory will display in the GUI, possibly just "Inventory" */
	private final String name = "Custom Inventory";

	/** The key used to store and retrieve the inventory from NBT */
	private static final String SAVE_KEY = "CustomInvTag";

	/** Define the inventory size here for easy reference */
	// This is also the place to define which slot is which if you have different types,
	// for example SLOT_SHIELD = 0, SLOT_AMULET = 1;
	public static final int INV_SIZE = 2;

	public InventoryCustomPlayer() {
		// Make sure to initialize the inventory slots:
		this.inventory = new ItemStack[INV_SIZE];
	}

	/**
	 * Show our custom inventory name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Custom name is already translated, so we return true here
	 */
	@Override
	public boolean hasCustomName() {
		return true;
	}

	/**
	 * Our custom slots are similar to armor - only one item per slot
	 */
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	/**
	 * This method doesn't seem to do what it claims to do, as
	 * items can still be left-clicked and placed in the inventory
	 * even when this returns false
	 */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		// If you have different kinds of slots, then check them here:
		// if (slot == SLOT_SHIELD && stack.getItem() instanceof ItemShield) return true;

		// For now, only ItemUseMana items can be stored in these slots
		return stack.getItem() instanceof ItemUseMana;
	}

	@Override
	protected String getNbtKey() {
		return SAVE_KEY;
	}

	/**
	 * Makes this inventory an exact replica of the inventory provided
	 * (useful, for example, when persisting IExtendedEntityProperties)
	 */
	public void copy(AbstractInventory inv) {
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			inventory[i] = (stack == null ? null : stack.copy());
		}
		markDirty();
	}
}
