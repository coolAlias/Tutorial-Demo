package tutorial.item.crafting;

import java.util.ArrayList;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import tutorial.TutorialMain;
import tutorial.item.ItemWizardArmor;

public class RecipesWizardArmorDyes implements IRecipe
{
	@Override
	public boolean matches(InventoryCrafting crafting, World world) {
		ItemStack itemstack = null;
		ArrayList arraylist = new ArrayList();
		for (int i = 0; i < crafting.getSizeInventory(); ++i) {
			ItemStack itemstack1 = crafting.getStackInSlot(i);
			if (itemstack1 != null) {
				if (itemstack1.getItem() instanceof ItemWizardArmor) {
					ItemWizardArmor itemarmor = (ItemWizardArmor) itemstack1.getItem();
					if (itemarmor.getArmorMaterial() != TutorialMain.armorWool || itemstack != null) {
						return false;
					}

					itemstack = itemstack1;
				} else {
					if (itemstack1.getItem() != Items.dye) {
						return false;
					}

					arraylist.add(itemstack1);
				}
			}
		}

		return itemstack != null && !arraylist.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		ItemStack itemstack = null;
		int[] aint = new int[3];
		int i = 0;
		int j = 0;
		ItemWizardArmor itemarmor = null;
		int k;
		int l;
		float f;
		float f1;
		int i1;

		for (k = 0; k < crafting.getSizeInventory(); ++k) {
			ItemStack itemstack1 = crafting.getStackInSlot(k);
			if (itemstack1 != null) {
				if (itemstack1.getItem() instanceof ItemWizardArmor) {
					itemarmor = (ItemWizardArmor) itemstack1.getItem();
					if (itemarmor.getArmorMaterial() != TutorialMain.armorWool || itemstack != null) {
						return null;
					}

					itemstack = itemstack1.copy();
					itemstack.stackSize = 1;

					if (itemarmor.hasColor(itemstack1)) {
						l = itemarmor.getColor(itemstack);
						f = (float)(l >> 16 & 255) / 255.0F;
						f1 = (float)(l >> 8 & 255) / 255.0F;
						float f2 = (float)(l & 255) / 255.0F;
						i = (int)((float)i + Math.max(f, Math.max(f1, f2)) * 255.0F);
						aint[0] = (int)((float)aint[0] + f * 255.0F);
						aint[1] = (int)((float)aint[1] + f1 * 255.0F);
						aint[2] = (int)((float)aint[2] + f2 * 255.0F);
						++j;
					}
				} else {
					if (itemstack1.getItem() != Items.dye) {
						return null;
					}

					float[] afloat = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(itemstack1.getMetadata()));
					int j1 = (int)(afloat[0] * 255.0F);
					int k1 = (int)(afloat[1] * 255.0F);
					i1 = (int)(afloat[2] * 255.0F);
					i += Math.max(j1, Math.max(k1, i1));
					aint[0] += j1;
					aint[1] += k1;
					aint[2] += i1;
					++j;
				}
			}
		}

		if (itemarmor == null) {
			return null;
		} else {
			k = aint[0] / j;
			int l1 = aint[1] / j;
			l = aint[2] / j;
			f = (float)i / (float)j;
			f1 = (float)Math.max(k, Math.max(l1, l));
			k = (int)((float)k * f / f1);
			l1 = (int)((float)l1 * f / f1);
			l = (int)((float)l * f / f1);
			i1 = (k << 8) + l1;
			i1 = (i1 << 8) + l;
			itemarmor.setColor(itemstack, i1);
			return itemstack;
		}
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting crafting) {
		ItemStack[] stacks = new ItemStack[crafting.getSizeInventory()];
		for (int i = 0; i < stacks.length; ++i) {
			ItemStack stack = crafting.getStackInSlot(i);
			stacks[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(stack);
		}
		return stacks;
	}
}
