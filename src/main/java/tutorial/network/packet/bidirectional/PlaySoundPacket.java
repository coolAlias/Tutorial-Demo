package tutorial.network.packet.bidirectional;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * 
 * Plays a sound on either the client (only that one player hears the sound)
 * or server side (everyone nearby can hear the sound).
 * 
 * This is useful if, for example, the player levels up on the server - you
 * may not want everyone to hear the level up sound, so you send this packet
 * to the client.
 * 
 * Another example, the player is performing some action on the client side,
 * such as pressing a key or fiddling in a GUI, that makes a sound which everyone
 * nearby should hear - simply send this packet to the server and voilá.
 *
 */
public class PlaySoundPacket implements IMessage
{
	private String sound;

	private float volume;

	private float pitch;

	/** Coordinates at which to play the sound; used only on the server side
	 * (yes, that is indeed slightly wasteful when sending this packet to the client...) */
	private double x, y, z;

	public PlaySoundPacket() {}

	public PlaySoundPacket(String sound, float volume, float pitch, double x, double y, double z) {
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Use only when sending to the SERVER to use the entity's coordinates as the center;
	 * if sent to the client, the position coordinates will be ignored.
	 */
	public PlaySoundPacket(String sound, float volume, float pitch, Entity entity) {
		this(sound, volume, pitch, entity.posX, entity.posY, entity.posZ);
	}

	/**
	 * Use only when sending to the CLIENT - the sound will play at the player's position
	 */
	public PlaySoundPacket(String sound, float volume, float pitch) {
		this(sound, volume, pitch, 0, 0, 0);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		sound = ByteBufUtils.readUTF8String(buffer);
		volume = buffer.readFloat();
		pitch = buffer.readFloat();
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, sound);
		buffer.writeFloat(volume);
		buffer.writeFloat(pitch);
		buffer.writeDouble(x);
		buffer.writeDouble(y);
		buffer.writeDouble(z);
	}

	/**
	 * 
	 * Here, I use {@link AbstractBiMessageHandler} instead of {@link IMessageHandler} because
	 * the message is handled slightly differently depending on side, but it could just as well
	 * be implemented using IMessageHandler#onMessage:
	 * 
	 *	@Override
	 * 	public IMessage onMessage(ActivateSkillPacket message, MessageContext ctx) {
	 *		EntityPlayer player = TutorialMain.proxy.getPlayerEntity(ctx);
	 *		if (ctx.side.isClient()) {
	 *			player.playSound(message.sound, message.volume, message.pitch);
	 *		} else {
	 *			player.worldObj.playSoundEffect(message.x, message.y, message.z, message.sound, message.volume, message.pitch);
	 *		}
	 *		return null;
	 *	}
	 *
	 */
	public static class Handler extends AbstractBiMessageHandler<PlaySoundPacket> {
		@Override
		public IMessage handleClientMessage(EntityPlayer player, PlaySoundPacket message, MessageContext ctx) {
			// this method ONLY works on the client - if you use it on the server, no sound will play
			player.playSound(message.sound, message.volume, message.pitch);
			return null;
		}

		@Override
		public IMessage handleServerMessage(EntityPlayer player, PlaySoundPacket message, MessageContext ctx) {
			// this method ONLY works on the server - if you use it on the client, no sound will play
			player.worldObj.playSoundEffect(message.x, message.y, message.z, message.sound, message.volume, message.pitch);
			return null;
		}
	}
}
