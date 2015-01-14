package tutorial.network.packet.bidirectional;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tutorial.network.packet.AbstractMessageHandler;

/**
 * 
 * Sets the player's attack time on either the client or the server.
 * 
 * EntityLivingBase#attackTime no longer exists.
 * 
 * Note that this will have no effect in normal Minecraft, but I use
 * it in my mods in combination with some events to prevent the player
 * from spamming the attack key.
 *
 */
@Deprecated
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
	 * to use {@link AbstractBiMessageHandler#handleMessage}, rather than writing the same code
	 * in both {@link AbstractMessageHandler#handleClientMessage} and {@link AbstractMessageHandler#handleServerMessage}.
	 * 
	 * Alternatively, one could implement {@link IMessageHandler} directly, but then we would
	 * have to check for the main thread again before processing the packet
	 *
	 */
	public static class Handler extends AbstractBiMessageHandler<AttackTimePacket> {
		@Override
		protected IMessage handleMessage(EntityPlayer player, AttackTimePacket msg, MessageContext ctx) {
			//player.attackTime = message.attackTime;
			return null;
		}
	}
}
