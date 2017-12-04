package tutorial.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 *
 * Generic render factory for entities that use RenderSnowball as their renderer
 *
 */
public class ThrowableRenderFactory<T extends Entity> implements IRenderFactory<T>
{
	private final Item item;

	/**
	 * @param item The item that will be used during rendering of the entity
	 */
	public ThrowableRenderFactory(Item item) {
		this.item = item;
	}

	@Override
	public Render<? super T> createRenderFor(RenderManager manager) {
		return new RenderSnowball<T>(manager, this.item, Minecraft.getMinecraft().getRenderItem());
	}
}
