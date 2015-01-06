package tutorial;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import tutorial.entity.ExtendedPlayer;
import tutorial.network.PacketDispatcher;
import tutorial.network.packet.client.SyncPlayerPropsMessage;

public class TutEventHandler
{
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer) {
			if (ExtendedPlayer.get((EntityPlayer) event.entity) == null) {
				TutorialMain.logger.info("Registering extended properties for player");
				ExtendedPlayer.register((EntityPlayer) event.entity);
			}
		}
	}

	/**
	 * This event is on the FML bus
	 */
	@SubscribeEvent
	public void onPlayerLogIn(PlayerLoggedInEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			// we need this because the client player will be null the first time a packet is sent from EntityJoinWorldEvent
			TutorialMain.logger.info("Player logged in, sending extended properties to client");
			PacketDispatcher.sendTo(new SyncPlayerPropsMessage(event.player), (EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public void onJoinWorld(EntityJoinWorldEvent event) {
		// If you have any non-DataWatcher fields in your extended properties that
		// need to be synced to the client, you must send a packet each time the
		// player joins the world; this takes care of dying, changing dimensions, etc.
		if (event.entity instanceof EntityPlayerMP) {
			TutorialMain.logger.info("Player joined world, sending extended properties to client");
			PacketDispatcher.sendTo(new SyncPlayerPropsMessage((EntityPlayer) event.entity), (EntityPlayerMP) event.entity);
		}
	}

	@SubscribeEvent
	public void onClonePlayer(PlayerEvent.Clone event) {
		TutorialMain.logger.info("Cloning player extended properties");
		ExtendedPlayer.get(event.entityPlayer).copy(ExtendedPlayer.get(event.original));
	}

	@SubscribeEvent
	public void onLivingFallEvent(LivingFallEvent event) {
		if (event.entity instanceof EntityPlayer) {
			ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) event.entity);
			if (event.distance > 3.0F && props.getCurrentMana() > 0) {
				TutorialMain.logger.info("Fall distance: " + event.distance);
				TutorialMain.logger.info("Current mana: " + props.getCurrentMana());
				float reduceby = props.getCurrentMana() < (event.distance - 3.0F) ? props.getCurrentMana() : (event.distance - 3.0F);
				event.distance -= reduceby;
				props.consumeMana((int) reduceby);
				TutorialMain.logger.info("Adjusted fall distance: " + event.distance);
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;
			ExtendedPlayer.get(player).onUpdate();
			if (player.isPlayerFullyAsleep()) {
				player.addChatMessage(new ChatComponentText("After a full night's rest, you feel refreshed!"));
				ExtendedPlayer.get(player).replenishMana();
			}
		}
	}
}