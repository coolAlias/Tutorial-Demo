package tutorial.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import tutorial.TutorialMain;
import tutorial.network.packet.AbstractMessageHandler;
import tutorial.network.packet.bidirectional.AbstractBiMessageHandler;
import tutorial.network.packet.bidirectional.AttackTimePacket;
import tutorial.network.packet.bidirectional.PlaySoundPacket;
import tutorial.network.packet.client.AbstractClientMessageHandler;
import tutorial.network.packet.client.SyncPlayerPropsMessage;
import tutorial.network.packet.server.AbstractServerMessageHandler;
import tutorial.network.packet.server.OpenGuiMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;


/**
 * 
 * This class will house the SimpleNetworkWrapper instance, which I will name 'dispatcher',
 * as well as give us a logical place from which to register our packets. These two things
 * could be done anywhere, however, even in your Main class, but I will be adding other
 * functionality (see below) that gives this class a bit more utility. 
 * 
 * While unnecessary, I'm going to turn this class into a 'wrapper' for SimpleNetworkWrapper
 * so that instead of writing "PacketDispatcher.dispatcher.{method}" I can simply write
 * "PacketDispatcher.{method}" All this does is make it quicker to type and slightly shorter;
 * if you do not care about that, then make the 'dispatcher' field public instead of private,
 * or, if you do not want to add a new class just for one field and one static method that
 * you could put anywhere, feel free to put them wherever.
 * 
 * For further convenience, I have also added two extra sendToAllAround methods: one which
 * takes an EntityPlayer and one which takes coordinates.
 *
 */
public class PacketDispatcher
{
	// a simple counter will allow us to get rid of 'magic' numbers used during packet registration
	private static byte packetId = 0;

	/**
	 * The SimpleNetworkWrapper instance is used both to register and send packets.
	 * Since I will be adding wrapper methods, this field is private, but you should
	 * make it public if you plan on using it directly.
	 */
	private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(TutorialMain.MOD_ID);

	/**
	 * Call this during pre-init or loading and register all of your packets (messages) here
	 */
	public static final void registerPackets() {
		// Using an incrementing field instead of hard-coded numerals, I don't need to think
		// about what number comes next or if I missed on should I ever rearrange the order
		// of registration (for instance, if you wanted to alphabetize them... yeah...)
		// It's even easier if you create a convenient 'registerMessage' method:
		registerMessage(OpenGuiMessage.Handler.class, OpenGuiMessage.class);
		registerMessage(SyncPlayerPropsMessage.Handler.class, SyncPlayerPropsMessage.class);

		// If you don't want to make a 'registerMessage' method, you can do it directly:
		//PacketDispatcher.dispatcher.registerMessage(OpenGuiMessage.OpenGuiMessageHandler.class, OpenGuiMessage.class, packetId++, Side.SERVER);
		//PacketDispatcher.dispatcher.registerMessage(SyncPlayerPropsMessage.SyncPlayerPropsMessageHandler.class, SyncPlayerPropsMessage.class, packetId++, Side.CLIENT);

		/** The following two packets are not used in this demo, but have been used in my other mods */
		/** I include them here simply for the sake of demonstrating packets that can be sent to both sides */
		// Bi-directional packets (each side handled differently, implementing AbstractBiMessageHandler)
		registerMessage(PlaySoundPacket.Handler.class, PlaySoundPacket.class);

		// Bi-directional packets using standard IMessageHandler implementation (handled identically on both sides)
		// Note how this packet requires a separate method, since there is no way to determine side
		// based on the handler class
		registerBiMessage(AttackTimePacket.Handler.class, AttackTimePacket.class);
	}

	/**
	 * Registers a message and message handler on the designated side;
	 * used for standard IMessage + IMessageHandler implementations
	 */
	private static final <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, Class<REQ> messageClass, Side side) {
		PacketDispatcher.dispatcher.registerMessage(handlerClass, messageClass, packetId++, side);
	}

	/**
	 * Registers a message and message handler on both sides; used mainly
	 * for standard IMessage + IMessageHandler implementations and ideal
	 * for messages that are handled identically on either side
	 */
	private static final <REQ extends IMessage, REPLY extends IMessage> void registerBiMessage(Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, Class<REQ> messageClass) {
		PacketDispatcher.dispatcher.registerMessage(handlerClass, messageClass, packetId, Side.CLIENT);
		PacketDispatcher.dispatcher.registerMessage(handlerClass, messageClass, packetId++, Side.SERVER);
	}

	/**
	 * Registers a message and message handler, automatically determining Side(s) based on the handler class
	 * @param handlerClass	Must extend one of {@link AbstractClientMessageHandler}, {@link AbstractServerMessageHandler}, or {@link AbstractBiMessageHandler}
	 */
	private static final <REQ extends IMessage> void registerMessage(Class<? extends AbstractMessageHandler<REQ>> handlerClass, Class<REQ> messageClass) {
		if (AbstractClientMessageHandler.class.isAssignableFrom(handlerClass)) {
			registerMessage(handlerClass, messageClass, Side.CLIENT);
		} else if (AbstractServerMessageHandler.class.isAssignableFrom(handlerClass)) {
			registerMessage(handlerClass, messageClass, Side.SERVER);
		} else if (AbstractBiMessageHandler.class.isAssignableFrom(handlerClass)) {
			registerBiMessage(handlerClass, messageClass);
		} else {
			throw new IllegalArgumentException("Cannot determine on which Side(s) to register " + handlerClass.getName() + " - unrecognized handler class!");
		}
	}

	//========================================================//
	// The following methods are the 'wrapper' methods; again,
	// this just makes sending a message slightly more compact
	// and is purely a matter of stylistic preference
	//========================================================//

	/**
	 * Send this message to the specified player.
	 * See {@link SimpleNetworkWrapper#sendTo(IMessage, EntityPlayerMP)}
	 */
	public static final void sendTo(IMessage message, EntityPlayerMP player) {
		PacketDispatcher.dispatcher.sendTo(message, player);
	}

	/**
	 * Send this message to everyone within a certain range of a point.
	 * See {@link SimpleNetworkWrapper#sendToDimension(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		PacketDispatcher.dispatcher.sendToAllAround(message, point);
	}

	/**
	 * Sends a message to everyone within a certain range of the coordinates in the same dimension.
	 */
	public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
		PacketDispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
	}

	/**
	 * Sends a message to everyone within a certain range of the player provided.
	 */
	public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
		PacketDispatcher.sendToAllAround(message, player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, range);
	}

	/**
	 * Send this message to everyone within the supplied dimension.
	 * See {@link SimpleNetworkWrapper#sendToDimension(IMessage, int)}
	 */
	public static final void sendToDimension(IMessage message, int dimensionId) {
		PacketDispatcher.dispatcher.sendToDimension(message, dimensionId);
	}

	/**
	 * Send this message to the server.
	 * See {@link SimpleNetworkWrapper#sendToServer(IMessage)}
	 */
	public static final void sendToServer(IMessage message) {
		PacketDispatcher.dispatcher.sendToServer(message);
	}
}
