package tutorial.network.packet.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tutorial.network.packet.AbstractMessageHandler;

/**
 * 
 * Handler for messages sent to the SERVER
 * Only allows implementation of {@link AbstractMessageHandler#handleServerMessage handleServerMessage}
 * 
 */
public abstract class AbstractServerMessageHandler<T extends IMessage> extends AbstractMessageHandler<T> {
	// implementing a final version of the client message handler both prevents it from
	// appearing automatically and prevents us from ever accidentally overriding it
	public final IMessage handleClientMessage(EntityPlayer player, T message, MessageContext ctx) {
		return null;
	}
}
