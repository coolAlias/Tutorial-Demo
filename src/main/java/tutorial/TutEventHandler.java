package tutorial;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import tutorial.entity.ExtendedPlayer;
import tutorial.network.PacketDispatcher;
import tutorial.network.packet.client.SyncPlayerPropsMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TutEventHandler
{
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer) {
			if (ExtendedPlayer.get((EntityPlayer) event.entity) == null)
				ExtendedPlayer.register((EntityPlayer) event.entity);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
			PacketDispatcher.sendTo(new SyncPlayerPropsMessage((EntityPlayer) event.entity), (EntityPlayerMP) event.entity);
		}
	}

	@SubscribeEvent
	public void onClonePlayer(PlayerEvent.Clone event) {
		/*
		// Easy way to clone extended player data: write to then read from NBT
		NBTTagCompound compound = new NBTTagCompound();
		ExtendedPlayer.get(event.original).saveNBTData(compound);
		ExtendedPlayer.get(event.entityPlayer).loadNBTData(compound);
		*/
		// Efficient way: implement copy methods to avoid disk I/O and NBT overhead
		ExtendedPlayer.get(event.entityPlayer).copy(ExtendedPlayer.get(event.original));
	}

	@SubscribeEvent
	public void onLivingFallEvent(LivingFallEvent event) {
		if (event.entity instanceof EntityPlayer) {
			ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) event.entity);
			if (event.distance > 3.0F && props.getCurrentMana() > 0) {
				System.out.println("[EVENT] Fall distance: " + event.distance);
				System.out.println("[EVENT] Current mana: " + props.getCurrentMana());
				float reduceby = props.getCurrentMana() < (event.distance - 3.0F) ? props.getCurrentMana() : (event.distance - 3.0F);
				event.distance -= reduceby;
				props.consumeMana((int) reduceby);
				System.out.println("[EVENT] Adjusted fall distance: " + event.distance);
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;
			ExtendedPlayer.get(player).onUpdate();
			if (player.isPlayerFullyAsleep()) {
				System.out.println("[TUT MANA] After a full night's rest, you feel refreshed!");
				ExtendedPlayer.get(player).replenishMana();
			}
		}
	}
}