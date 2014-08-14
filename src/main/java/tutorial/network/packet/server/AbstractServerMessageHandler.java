package tutorial.network.packet.server;

import tutorial.network.packet.AbstractMessageHandler;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * 
 * For messages handled on the SERVER.
 * This is just a convenience class that will prevent the client-side message handling
 * method from appearing in our server message handler classes.
 * 
 */
public abstract class AbstractServerMessageHandler<T extends IMessage> extends AbstractMessageHandler<T> {
	// implementing a final version of the client message handler both prevents it from
	// appearing automatically and prevents us from ever accidentally overriding it
	public final IMessage handleClientMessage(EntityPlayer player, T message, MessageContext ctx) {
		return null;
	}
}
