package tutorial.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import tutorial.entity.ExtendedPlayer;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	public void onRenderExperienceBar(RenderGameOverlayEvent.Post event)
	{
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
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		// Here we draw the background bar which contains a transparent section; note the new size
		drawTexturedModalRect(xPos, yPos, 0, 0, 56, 9);
		// You can keep drawing without changing anything
		int manabarwidth = (int)(((float) props.getCurrentMana() / props.getMaxMana()) * 49);
		drawTexturedModalRect(xPos + 3, yPos + 3, 0, 9, manabarwidth, 3);
		String s = "Mana " + props.getCurrentMana() + "/" + props.getMaxMana();
		yPos += 10;
		this.mc.fontRenderer.drawString(s, xPos + 1, yPos, 0);
		this.mc.fontRenderer.drawString(s, xPos - 1, yPos, 0);
		this.mc.fontRenderer.drawString(s, xPos, yPos + 1, 0);
		this.mc.fontRenderer.drawString(s, xPos, yPos - 1, 0);
		this.mc.fontRenderer.drawString(s, xPos, yPos, 8453920);
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
	}
}