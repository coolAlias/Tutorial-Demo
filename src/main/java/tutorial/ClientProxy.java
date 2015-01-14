package tutorial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tutorial.client.KeyHandler;
import tutorial.client.gui.GuiManaBar;
import tutorial.entity.EntityThrowingRock;
import tutorial.item.BaseModItem;

public class ClientProxy extends CommonProxy
{
	private final Minecraft mc = Minecraft.getMinecraft();

	@Override
	public void registerRenderers() {
		ItemModelMesher mesher = mc.getRenderItem().getItemModelMesher();
		// For my BaseModItems, I let the Item itself handle registration:
		((BaseModItem) TutorialMain.magicBag).registerRenderer(mesher);
		((BaseModItem) TutorialMain.throwingRock).registerRenderer(mesher);
		((BaseModItem) TutorialMain.useMana).registerRenderer(mesher);
		((BaseModItem) TutorialMain.wabbajack).registerRenderer(mesher);
		
		// If render registration isn't implemented in the class, it has to be done manually:
		if (TutorialMain.wizardArmorFlag) {
			// None of these have subtypes, so can be registered with just one damage value
			registerItemRenderer(mesher, TutorialMain.wizardHat);
			registerItemRenderer(mesher, TutorialMain.wizardRobe);
			registerItemRenderer(mesher, TutorialMain.wizardPants);
			registerItemRenderer(mesher, TutorialMain.wizardBoots);
		}

		RenderingRegistry.registerEntityRenderingHandler(EntityThrowingRock.class,
				new RenderSnowball(mc.getRenderManager(), TutorialMain.throwingRock, mc.getRenderItem()));
		
		// can register other client-side only things here, too:

		// The RenderGameOverlayEvent is in the MinecraftForge package, so we will
		// register our mana bar overlay to that event bus:
		MinecraftForge.EVENT_BUS.register(new GuiManaBar(mc));

		// KeyInputEvent is in the FML package, meaning it's posted to the FML event bus
		// rather than the regular Forge event bus:
		FMLCommonHandler.instance().bus().register(new KeyHandler(mc));
	}

	/**
	 * Registers an item with no subtypes using the unlocalized name as the texture name
	 */
	private void registerItemRenderer(ItemModelMesher mesher, Item item) {
		registerItemRenderer(mesher, item, 0);
	}

	/**
	 * Registers a specific item subtype using the unlocalized name as the texture name
	 * @param meta	Always 0 if only one type, otherwise the subtype's metadata value
	 */
	private void registerItemRenderer(ItemModelMesher mesher, Item item, int meta) {
		String name = item.getUnlocalizedName();
		name = TutorialMain.MODID + ":" + name.substring(name.lastIndexOf(".") + 1);
		registerItemRenderer(mesher, item, name, meta);
	}

	/**
	 * Registers a specific item subtype using the specified texture name
	 * @param name	Exact name of the texture file to be used, including the "modid:" prefix
	 * @param meta	Always 0 if only one type, otherwise the subtype's metadata value
	 */
	private void registerItemRenderer(ItemModelMesher mesher, Item item, String name, int meta) {
		TutorialMain.logger.info("Registering renderer for " + name);
		mesher.register(item, meta, new ModelResourceLocation(name, "inventory"));
	}

	// Deprecated???
	@Override
	public int addArmor(String armor) {
		//return RenderingRegistry.addNewArmourRendererPrefix(armor);
		return 0;
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		// Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
		// your packets will not work as expected because you will be getting a
		// client player even when you are on the server!
		// Sounds absurd, but it's true.

		// Solution is to double-check side before returning the player:
		TutorialMain.logger.info("Retrieving player from ClientProxy for message on side " + ctx.side);
		return (ctx.side.isClient() ? mc.thePlayer : super.getPlayerEntity(ctx));
	}

	@Override
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		return (ctx.side.isClient() ? mc : super.getThreadFromContext(ctx));
	}
}
