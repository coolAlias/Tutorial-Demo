package tutorial.network.bidirectional;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import tutorial.network.AbstractMessage;

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
public class PlaySoundPacket extends AbstractMessage<PlaySoundPacket>
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
	protected void read(PacketBuffer buffer) throws IOException {
		sound = ByteBufUtils.readUTF8String(buffer);
		volume = buffer.readFloat();
		pitch = buffer.readFloat();
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		ByteBufUtils.writeUTF8String(buffer, sound);
		buffer.writeFloat(volume);
		buffer.writeFloat(pitch);
		buffer.writeDouble(x);
		buffer.writeDouble(y);
		buffer.writeDouble(z);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// Since this packet is handled differently, we need to check side
		if (side.isClient()) {
			// Plays a sound only the client can hear
			player.playSound(sound, volume, pitch);
		} else {
			// Plays a sound that everyone nearby can hear
			player.worldObj.playSoundEffect(x, y, z, sound, volume, pitch);
		}
	}
}
