package tutorial.network.packet.bidirectional;

import io.netty.buffer.ByteBuf;
import tutorial.TutorialMain;
import tutorial.network.packet.AbstractMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * 
 * Sets the player's attack time on either the client or the server.
 * 
 * Note that this will have no effect in normal Minecraft, but I use
 * it in my mods in combination with some events to prevent the player
 * from spamming the attack key.
 *
 */
public class AttackTimePacket implements IMessage
{
	private int attackTime;

	public AttackTimePacket() {}

	public AttackTimePacket(int attackTime) {
		this.attackTime = attackTime;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.attackTime = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(attackTime);
	}

	/**
	 * 
	 * Since the handler implementation on both sides is identical, it is simplest
	 * to use {@link IMessageHandler#onMessage}, rather than writing the same code
	 * in both {@link AbstractMessageHandler#handleClientMessage} and {@link AbstractMessageHandler#handleServerMessage}.
	 *
	 */
	public static class Handler implements IMessageHandler<AttackTimePacket, IMessage> {
		@Override
		public IMessage onMessage(AttackTimePacket message, MessageContext ctx) {
			TutorialMain.proxy.getPlayerEntity(ctx).attackTime = message.attackTime;
			return null;
		}
	}
}
