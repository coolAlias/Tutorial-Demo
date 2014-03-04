package tutorial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.common.MinecraftForge;
import tutorial.client.KeyHandler;
import tutorial.client.gui.GuiManaBar;
import tutorial.entity.EntityThrowingRock;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityThrowingRock.class, new RenderSnowball(TutorialMain.throwingRock));
		// can register other client-side only things here, too:
		
		// The RenderGameOverlayEvent is in the MinecraftForge package, so we will
		// register our mana bar overlay to that event bus:
		MinecraftForge.EVENT_BUS.register(new GuiManaBar(Minecraft.getMinecraft()));
		
		// KeyInputEvent is in the FML package, meaning it's posted to the FML event bus
		// rather than the regular Forge event bus:
		FMLCommonHandler.instance().bus().register(new KeyHandler());
	}
	
	@Override
	public int addArmor(String armor) {
		return RenderingRegistry.addNewArmourRendererPrefix(armor);
	}
}
