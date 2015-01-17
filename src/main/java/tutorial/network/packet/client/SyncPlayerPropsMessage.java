package tutorial.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tutorial.TutorialMain;
import tutorial.entity.ExtendedPlayer;

/**
 * 
 * A packet to send ALL data stored in your extended properties to the client.
 * This is handy if you only need to send your data once per game session or
 * all of your data needs to be synchronized together; it's also handy while
 * first starting, since you only need one packet for everything - however,
 * you should NOT use such a packet in your final product!!!
 * 
 * Each packet should handle one thing and one thing only, in order to minimize
 * network traffic as much as possible. There is no point sending 20+ fields'
 * worth of data when you just need the current mana amount; conversely, it's
 * foolish to send 20 packets for all the data when the player first loads, when
 * you could send it all in one single packet.
 * 
 * TL;DR - make separate packets for each piece of data, and one big packet for
 * those times when you need to send everything.
 *
 */
public class SyncPlayerPropsMessage implements IMessage
//remember - the IMessageHandler will be implemented as a static inner class
{
	// Previously, we've been writing each field in our properties one at a time,
	// but that is really annoying, and we've already done it in the save and load
	// NBT methods anyway, so here's a slick way to efficiently send all of your
	// extended data, and no matter how much you add or remove, you'll never have
	// to change the packet / synchronization of your data.

	// this will store our ExtendedPlayer data, allowing us to easily read and write
	private NBTTagCompound data;

	// The basic, no-argument constructor MUST be included to use the new automated handling
	public SyncPlayerPropsMessage() {}

	// We need to initialize our data, so provide a suitable constructor:
	public SyncPlayerPropsMessage(EntityPlayer player) {
		// create a new tag compound
		data = new NBTTagCompound();
		// and save our player's data into it
		ExtendedPlayer.get(player).saveNBTData(data);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		// luckily, ByteBufUtils provides an easy way to read the NBT
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		// ByteBufUtils provides a convenient method for writing the compound
		ByteBufUtils.writeTag(buffer, data);
	}

	// Remember: this class MUST be static or you will crash
	public static class Handler extends AbstractClientMessageHandler<SyncPlayerPropsMessage> {
		// the fruits of our labor: we immediately know from the method name that we are handling
		// a message on the client side, and we have our EntityPlayer right there ready for use. Awesome.
		@Override
		@SideOnly(Side.CLIENT)
		protected void handleClientMessage(EntityPlayer player, SyncPlayerPropsMessage message, MessageContext ctx) {
			// now we can just load the NBTTagCompound data directly; one and done, folks
			if (ExtendedPlayer.get(player) == null) {
				// this should never be the case if you registered your properties and waited
				// for the main world thread before processing your packet
				TutorialMain.logger.warn("Client extended properties were NULL when SyncPlayerPropsMessage received");
			} else {
				TutorialMain.logger.info("Synchronizing extended properties data on CLIENT");
				ExtendedPlayer.get(player).loadNBTData(message.data);
			}
		}

		// Note here that we don't (and can't) implement the handleServerMessage method
		// since we extended AbstractClientMessage. This is exactly what we want.
	}
	/**
	 *
	 * 'VANILLA' VERSION of the Message Handler
	 * Straight implementation without any of my personal 'improvements' :P 
	 *
	 */
	/*
	public static class Handler implements IMessageHandler<SyncPlayerPropsMessage, IMessage> {
		@Override
		public IMessage onMessage(SyncPlayerPropsMessage message, MessageContext ctx) {
			EntityPlayer player = TutorialMain.proxy.getPlayerEntity(ctx);
			ExtendedPlayer.get(player).loadNBTData(message.data);
			return null;
		}
	}*/
}
