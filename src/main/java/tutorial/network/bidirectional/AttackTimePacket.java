package tutorial.network.bidirectional;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import tutorial.network.AbstractMessage;
import cpw.mods.fml.relauncher.Side;

/**
 * 
 * Sets the player's attack time on either the client or the server.
 * 
 * Note that this will have no effect in normal Minecraft, but I use
 * it in my mods in combination with some events to prevent the player
 * from spamming the attack key.
 *
 */
public class AttackTimePacket extends AbstractMessage<AttackTimePacket>
{
	private int attackTime;

	public AttackTimePacket() {}

	public AttackTimePacket(int attackTime) {
		this.attackTime = attackTime;
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		this.attackTime = buffer.readInt();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeInt(attackTime);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// Handled identically on both sides, so we don't need to check which side we're on
		player.attackTime = this.attackTime;
	}
}
