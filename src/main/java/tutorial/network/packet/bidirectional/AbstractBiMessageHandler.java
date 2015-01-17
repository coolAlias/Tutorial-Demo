package tutorial.network.packet.bidirectional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tutorial.network.packet.AbstractMessageHandler;

/**
 * Handler for messages which can be sent to both sides.
 * 
 * If a message is handled identically on both sides, just override {@link #process};
 * otherwise, override both {@link #handleClientMessage} and {@link #handleServerMessage}
 */
public abstract class AbstractBiMessageHandler<T extends IMessage> extends AbstractMessageHandler<T>
{
	/**
	 * Called by both handleClientMessage and handleServerMessage unless they are overridden.
	 * Most useful for messages with identical handling on either side
	 */
	protected void handleMessage(EntityPlayer player, T msg, MessageContext ctx) {}

	@Override
	protected void handleClientMessage(EntityPlayer player, T msg, MessageContext ctx) {
		handleMessage(player, msg, ctx);
	}

	@Override
	protected void handleServerMessage(EntityPlayer player, T msg, MessageContext ctx) {
		handleMessage(player, msg, ctx);
	}
}
