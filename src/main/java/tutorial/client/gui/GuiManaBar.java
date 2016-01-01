package tutorial.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tutorial.entity.ExtendedPlayer;

@SideOnly(Side.CLIENT)
public class GuiManaBar extends Gui
{
	private Minecraft mc;

	private static final ResourceLocation texture = new ResourceLocation("tutorial", "textures/gui/mana_bar.png");

	public GuiManaBar(Minecraft mc) {
		super();
		this.mc = mc;
	}

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void onRenderExperienceBar(RenderGameOverlayEvent.Post event) {
		if (event.type != ElementType.EXPERIENCE) {
			return;
		}

		ExtendedPlayer props = ExtendedPlayer.get(this.mc.thePlayer);
		if (props == null || props.getMaxMana() == 0) {
			return;
		}

		int xPos = 2;
		int yPos = 2;
		this.mc.getTextureManager().bindTexture(texture);

		// Add this block of code before you draw the section of your texture containing transparency
		GlStateManager.pushAttrib();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		// alpha test and blend needed due to vanilla or Forge rendering bug
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		// Here we draw the background bar which contains a transparent section; note the new size
		drawTexturedModalRect(xPos, yPos, 0, 0, 56, 9);
		// You can keep drawing without changing anything
		int manabarwidth = (int)(((float) props.getCurrentMana() / props.getMaxMana()) * 49);
		drawTexturedModalRect(xPos + 3, yPos + 3, 0, 9, manabarwidth, 3);
		String s = "Mana " + props.getCurrentMana() + "/" + props.getMaxMana();
		yPos += 10;
		this.mc.fontRendererObj.drawString(s, xPos + 1, yPos, 0);
		this.mc.fontRendererObj.drawString(s, xPos - 1, yPos, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos + 1, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos - 1, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos, 8453920);
		GlStateManager.popAttrib();
	}
}