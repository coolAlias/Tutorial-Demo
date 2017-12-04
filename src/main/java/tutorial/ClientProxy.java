package tutorial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tutorial.client.KeyHandler;
import tutorial.client.gui.GuiManaBar;
import tutorial.client.render.ThrowableRenderFactory;
import tutorial.entity.EntityThrowingRock;
import tutorial.item.BaseModItem;

public class ClientProxy extends CommonProxy
{
	private final Minecraft mc = Minecraft.getMinecraft();

	@Override
	public void preInit() {
		// For my BaseModItems, I let the Item itself handle registration:
		((BaseModItem) TutorialMain.magicBag).registerResources();
		((BaseModItem) TutorialMain.throwingRock).registerResources();
		((BaseModItem) TutorialMain.useMana).registerResources();
		((BaseModItem) TutorialMain.wabbajack).registerResources();

		// If render registration isn't implemented in the class, it has to be done manually:
		if (TutorialMain.wizardArmorFlag) {
			// None of these have subtypes, so can be registered with just one damage value
			registerItemRenderer(TutorialMain.wizardHat);
			registerItemRenderer(TutorialMain.wizardRobe);
			registerItemRenderer(TutorialMain.wizardPants);
			registerItemRenderer(TutorialMain.wizardBoots);
		}

		RenderingRegistry.registerEntityRenderingHandler(EntityThrowingRock.class, new ThrowableRenderFactory<EntityThrowingRock>(TutorialMain.throwingRock));

		// can register other client-side only things here, too:
		// Register our various event handlers - there is only one event bus now
		MinecraftForge.EVENT_BUS.register(new GuiManaBar(mc));
		MinecraftForge.EVENT_BUS.register(new KeyHandler(mc));
	}

	/**
	 * Registers an item with no subtypes using the unlocalized name as the texture name
	 */
	private void registerItemRenderer(Item item) {
		registerItemRenderer(item, 0);
	}

	/**
	 * Registers a specific item subtype using the unlocalized name as the texture name
	 * @param meta	Always 0 if only one type, otherwise the subtype's metadata value
	 */
	private void registerItemRenderer(Item item, int meta) {
		String name = item.getUnlocalizedName();
		name = TutorialMain.MODID + ":" + name.substring(name.lastIndexOf(".") + 1);
		registerItemRenderer(item, name, meta);
	}

	/**
	 * Registers a specific item subtype using the specified texture name
	 * @param name	Exact name of the texture file to be used, including the "modid:" prefix
	 * @param meta	Always 0 if only one type, otherwise the subtype's metadata value
	 */
	private void registerItemRenderer(Item item, String name, int meta) {
		TutorialMain.logger.info("Registering renderer for " + name);
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, "inventory"));
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
		// your packets will not work as expected in single player because the
		// integrated server also uses this proxy

		// Solution is to double-check side before returning the player:
		TutorialMain.logger.info("Retrieving player from ClientProxy for message on side " + ctx.side);
		return (ctx.side.isClient() ? mc.thePlayer : super.getPlayerEntity(ctx));
	}

	@Override
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		return (ctx.side.isClient() ? mc : super.getThreadFromContext(ctx));
	}
}
