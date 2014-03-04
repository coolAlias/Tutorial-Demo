package tutorial.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import tutorial.TutorialMain;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWizardArmor extends ItemArmor
{
	private IIcon overlay;
	//private static final String[] iconNames = new String[] {"wizard_helmet_overlay", "wizard_chestplate_overlay", "wizard_leggings_overlay", "wizard_boots_overlay"};

	//public static final String[] colorNumbers = new String[]
	//	{"191919", "CC4C4C", "667F33", "7F664C", "3366CC", "B266E5",
	//	"4C99B2", "999999", "4C4C4C", "F2B2CC", "7FCC19", "E5E533",
	//	"99B2F2", "E57FD8", "F2B233", "FFFFFF"};

	/**
	 * @param par1 - Item ID
	 * @param par2EnumArmorMaterial
	 * @param par3 - Render index
	 * @param par4 - Armor type: 0 helm, 1 plate, 2 legs, 3 boots
	 */
	public ItemWizardArmor(ArmorMaterial material, int renderIndex, int type) {
		super(material, renderIndex, type);
		setCreativeTab(CreativeTabs.tabCombat);
	}
	/*
	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack stack)
    {
		if (stack.itemID == TutorialMain.WizardHat.itemID)
		{
			player.addChatMessage("[ARMOR TICK] You're wearing a wizard hat!!! Yippee!");
		}
    }
	 */
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (stack.getItem() == TutorialMain.wizardPants) {
			// Don't know why, but Pants are rendered on opposite render layer from the other slots
			return type == null ? "tutorial:textures/models/armor/wizard_layer_2.png" : "tutorial:textures/models/armor/wizard_layer_2_overlay.png";
		} else if (stack.getItem() instanceof ItemWizardArmor) {
			return type == null ? "tutorial:textures/models/armor/wizard_layer_1.png" : "tutorial:textures/models/armor/wizard_layer_1_overlay.png";
		} else {
			return null;
		}
	}

	/**
	 * Return whether the specified armor ItemStack has a color.
	 */
	@Override
	public boolean hasColor(ItemStack stack) {
		return getArmorMaterial() != TutorialMain.armorWool ? false : (!stack.hasTagCompound() ? false : (!stack.getTagCompound().hasKey("display") ? false : stack.getTagCompound().getCompoundTag("display").hasKey("color")));
	}

	/**
	 * Return the color for the specified armor ItemStack.
	 */
	@Override
	public int getColor(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null) {
			return 10511680;
		} else {
			NBTTagCompound compound1 = compound.getCompoundTag("display");
			return compound1 == null ? 10511680 : (compound1.hasKey("color") ? compound1.getInteger("color") : 10511680);
		}
	}

	public void setColor(ItemStack stack, int color) {
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null) {
			compound = new NBTTagCompound();
			stack.setTagCompound(compound);
		}
		NBTTagCompound compound1 = compound.getCompoundTag("display");
		if (!compound.hasKey("display")) {
			compound.setTag("display", compound1);
		}
		compound1.setInteger("color", color);
	}

	/**
	 * Remove the color from the specified armor ItemStack.
	 */
	@Override
	public void removeColor(ItemStack stack) {
		NBTTagCompound nbttagcompound = stack.getTagCompound();
		if (nbttagcompound != null) {
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
			if (nbttagcompound1.hasKey("color")) {
				nbttagcompound1.removeTag("color");
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return false; // TODO true causes the game to crash...
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if (renderPass > 0) {
			return 16777215;
		} else {
			int j = this.getColor(stack);
			return (j < 0 ? 16777215 : j);
		}
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass) {
		return renderPass == 1 ? overlay : itemIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int damage, int renderPass) {
		return renderPass == 1 ? overlay : super.getIconFromDamageForRenderPass(damage, renderPass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		itemIcon = register.registerIcon("tutorial:" + getUnlocalizedName().substring(5));
		overlay = register.registerIcon("tutorial:" + getUnlocalizedName().substring(5) + "_overlay");
	}
}
