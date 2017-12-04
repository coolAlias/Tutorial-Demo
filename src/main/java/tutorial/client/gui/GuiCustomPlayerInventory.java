package tutorial.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import tutorial.client.KeyHandler;
import tutorial.inventory.ContainerCustomPlayer;
import tutorial.inventory.InventoryCustomPlayer;

@SideOnly(Side.CLIENT)
public class GuiCustomPlayerInventory extends GuiContainer
{
	/** x size of the inventory window in pixels. Defined as float, passed as int */
	private float xSize_lo;

	/** y size of the inventory window in pixels. Defined as float, passed as int. */
	private float ySize_lo;

	/** Normally I use '(ModInfo.MOD_ID, "textures/...")', but it can be done this way as well */
	private static final ResourceLocation iconLocation = new ResourceLocation("tutorial:textures/gui/custom_inventory.png");

	/** Could use IInventory type to be more generic, but this way will save an import... */
	private final InventoryCustomPlayer inventory;

	public GuiCustomPlayerInventory(EntityPlayer player, InventoryPlayer inventoryPlayer, InventoryCustomPlayer inventoryCustom) {
		super(new ContainerCustomPlayer(player, inventoryPlayer, inventoryCustom));
		// if you need the player for something later on, store it in a local variable here as well
		this.inventory = inventoryCustom;
	}

	@Override
	protected void keyTyped(char c, int keyCode) throws IOException {
		super.keyTyped(c, keyCode);
		// We made our keybinding array public and static, so we can access it here
		if (keyCode == KeyHandler.keys[KeyHandler.CUSTOM_INV].getKeyCode()) {
			mc.thePlayer.closeScreen();
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		xSize_lo = mouseX;
		ySize_lo = mouseY;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// This method will simply draw inventory's name on the screen - you could do without it entirely
		// if that's not important to you, since we are overriding the default inventory rather than
		// creating a specific type of inventory

		String s = inventory.hasCustomName() ? inventory.getName() : I18n.format(inventory.getName());
		// with the name "Custom Inventory", the 'Cu' will be drawn in the first slot
		fontRendererObj.drawString(s, xSize - fontRendererObj.getStringWidth(s), 12, 4210752);
		// this just adds "Inventory" above the player's inventory below
		fontRendererObj.drawString(I18n.format("container.inventory"), 80, ySize - 96, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(iconLocation);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		GuiInventory.drawEntityOnScreen(guiLeft + 51, guiTop + 75, 30, guiLeft + 51 - xSize_lo, guiTop + 25 - ySize_lo, mc.thePlayer);
	}
}
