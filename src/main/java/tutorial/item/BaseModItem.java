package tutorial.item;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tutorial.TutorialMain;

public class BaseModItem extends Item
{
	public BaseModItem() {
		super();
	}

	/**
	 * Register all of this Item's renderers here, including for any subtypes.
	 * Default behavior registers a single inventory-based mesher using the unlocalized name.
	 */
	@SideOnly(Side.CLIENT)
	public void registerRenderer(ItemModelMesher mesher) {
		String name = getUnlocalizedName();
		name = TutorialMain.MODID + ":" + name.substring(name.lastIndexOf(".") + 1);
		TutorialMain.logger.info("Registering renderer for " + name);
		mesher.register(this, 0, new ModelResourceLocation(name, "inventory"));
	}
}
