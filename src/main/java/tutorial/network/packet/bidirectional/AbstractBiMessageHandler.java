package tutorial.network.packet.bidirectional;

import tutorial.network.PacketDispatcher;
import tutorial.network.packet.AbstractMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;

/**
 * For messages which require different handling on each Side;
 * if the message is handled identically regardless of Side,
 * it is better to implement {@link IMessageHandler} directly
 * and register using {@link PacketDispatcher#registerBiMessage}
 */
public abstract class AbstractBiMessageHandler<T extends IMessage> extends AbstractMessageHandler<T> {

}
