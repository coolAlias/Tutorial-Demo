package tutorial.item;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tutorial.TutorialMain;

public class BaseModItem extends Item
{
	public BaseModItem() {
		super();
	}

	/**
	 * Register any item variant names here using e.g. {@link ModelLoader#registerItemVariants} or {@link ModelLoader#setCustomMeshDefinition}.
	 * This MUST be called during {@code FMLPreInitializationEvent}
	 * 
	 * Typical implementation taking advantage of {@link #getVariants()}:
	 * 
	 *	String[] variants = getVariants();
	 *	if (variants == null || variants.length < 1) {
	 *		String name = getUnlocalizedName();
	 *		variants = new String[]{ModInfo.ID + ":" + name.substring(name.lastIndexOf(".") + 1)};
	 *	}
	 *	for (int i = 0; i < variants.length; ++i) {
	 *		ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(variants[i], "inventory"));
	 *	}
	 */
	@SideOnly(Side.CLIENT)
	public void registerResources() {
		String name = getUnlocalizedName();
		name = TutorialMain.MODID + ":" + name.substring(name.lastIndexOf(".") + 1);
		TutorialMain.logger.info("Registering renderer for " + name);
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(name, "inventory"));
	}
}
