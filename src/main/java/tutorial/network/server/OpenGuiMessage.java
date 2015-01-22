package tutorial.network.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import tutorial.TutorialMain;
import tutorial.network.AbstractMessage.AbstractServerMessage;
import cpw.mods.fml.relauncher.Side;

/**
 * 
 * A simple message telling the server that the client wants to open a GUI.
 * 
 */
public class OpenGuiMessage extends AbstractServerMessage<OpenGuiMessage>
{
	// this will store the id of the gui to open
	private int id;

	// The basic, no-argument constructor MUST be included to use the new automated handling
	public OpenGuiMessage() {}

	// if there are any class fields, be sure to provide a constructor that allows
	// for them to be initialized, and use that constructor when sending the packet
	public OpenGuiMessage(int id) {
		this.id = id;
	}

	@Override
	protected void read(PacketBuffer buffer) {
		// basic Input/Output operations, very much like DataInputStream
		id = buffer.readInt();
	}

	@Override
	protected void write(PacketBuffer buffer) {
		// basic Input/Output operations, very much like DataOutputStream
		buffer.writeInt(id);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// using the message instance gives access to 'this.id'
		player.openGui(TutorialMain.instance, this.id, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	}

	/**
	 *
	 * 'VANILLA' VERSION of the Message Handler
	 * Straight implementation without any of my personal 'improvements' :P 
	 *
	 */
	/*
	public static class Handler implements IMessageHandler<OpenGuiMessage, IMessage> {
		@Override
		public IMessage onMessage(OpenGuiMessage message, MessageContext ctx) {
			// You could use ctx.getServerHandler().playerEntity directly, but using the
			// the proxy method everywhere keeps you safe from mundane mistakes
			EntityPlayer player = TutorialMain.proxy.getPlayerEntity(ctx);

			// because we sent the gui's id with the packet, we can handle all cases with one line:
			player.openGui(TutorialMain.instance, message.id, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
			return null;
		}
	}
	 */
}
