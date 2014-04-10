package tutorial.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import tutorial.CommonProxy;
import tutorial.TutorialMain;
import tutorial.inventory.InventoryCustomPlayer;
import tutorial.network.packet.SyncPlayerPropsPacket;

public class ExtendedPlayer implements IExtendedEntityProperties
{
	public final static String EXT_PROP_NAME = "ExtendedPlayer";

	private final EntityPlayer player;

	/** Custom inventory slots will be stored here - be sure to save to NBT! */
	public final InventoryCustomPlayer inventory = new InventoryCustomPlayer();
	
	private int maxMana, manaRegenTimer;

	public static final int MANA_WATCHER = 20;

	public ExtendedPlayer(EntityPlayer player) {
		this.player = player;
		this.maxMana = 50;
		this.manaRegenTimer = 0;
		this.player.getDataWatcher().addObject(MANA_WATCHER, this.maxMana);
	}

	/**
	 * Used to register these extended properties for the player during EntityConstructing event
	 */
	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer(player));
	}

	/**
	 * Returns ExtendedPlayer properties for player
	 */
	public static final ExtendedPlayer get(EntityPlayer player) {
		return (ExtendedPlayer) player.getExtendedProperties(EXT_PROP_NAME);
	}

	@Override
	public final void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		// Write custom inventory to NBT
		inventory.writeToNBT(properties);
		properties.setInteger("CurrentMana", player.getDataWatcher().getWatchableObjectInt(MANA_WATCHER));
		properties.setInteger("MaxMana", maxMana);
		properties.setInteger("ManaRegenTimer", manaRegenTimer);
		compound.setTag(EXT_PROP_NAME, properties);
	}

	@Override
	public final void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		// Read custom inventory from NBT
		inventory.readFromNBT(properties);
		player.getDataWatcher().updateObject(MANA_WATCHER, properties.getInteger("CurrentMana"));
		maxMana = properties.getInteger("MaxMana");
		manaRegenTimer = properties.getInteger("ManaRegenTimer");
		System.out.println("[TUT PROPS] Mana from NBT: " + player.getDataWatcher().getWatchableObjectInt(MANA_WATCHER) + "/" + this.maxMana);
	}

	@Override
	public void init(Entity entity, World world) {}
	
	/**
	 * Updates anything that needs to be updated each tick
	 * NOT called automatically, so you must call it yourself from LivingUpdateEvent or a TickHandler
	 */
	public void onUpdate() {
		// only want to update the timer and regen mana on the server:
		if (!player.worldObj.isRemote) {
			if (updateManaTimer()) {
				regenMana(1);
			}
		}
	}
	
	private boolean updateManaTimer() {
		if (manaRegenTimer > 0) {
			--manaRegenTimer;
		}
		if (manaRegenTimer == 0) {
			manaRegenTimer = getCurrentMana() < getMaxMana() ? 100 : 0;
			return true;
		}
		
		return false;
	}
	
	public final void regenMana(int amount) {
		setCurrentMana(getCurrentMana() + amount);
	}

	/**
	 * Returns true if the amount of mana was consumed or false
	 * if the player's current mana was insufficient
	 */
	public final boolean consumeMana(int amount) {
		boolean sufficient = amount <= getCurrentMana();
		setCurrentMana(getCurrentMana() - amount);
		return sufficient;
	}

	/**
	 * Simple method sets current mana to max mana
	 */
	public final void replenishMana() {
		this.player.getDataWatcher().updateObject(MANA_WATCHER, this.maxMana);
	}

	/**
	 * Returns current mana amount
	 */
	public final int getCurrentMana() {
		return player.getDataWatcher().getWatchableObjectInt(MANA_WATCHER);
	}

	/**
	 * Sets current mana to amount or maxMana, whichever is lesser
	 */
	public final void setCurrentMana(int amount) {
		player.getDataWatcher().updateObject(MANA_WATCHER, amount > 0 ? (amount < maxMana ? amount : maxMana) : 0);
	}

	/**
	 * Returns max mana amount
	 */
	public final int getMaxMana() {
		return maxMana;
	}

	/**
	 * Sets max mana to amount or 0 if amount is less than 0
	 */
	public final void setMaxMana(int amount) {
		maxMana = (amount > 0 ? amount : 0);
		// if your extended properties contains a lot of data, it would be better
		// to make an individual packet for maxMana, rather than sending all of
		// the data each time max mana changes... just remember to register any
		// new packets you create to the PacketPipeline, or your game will crash
		TutorialMain.packetPipeline.sendTo(new SyncPlayerPropsPacket(player), (EntityPlayerMP) player);
	}

	/**
	 * Makes it look nicer in the methods save/loadProxyData
	 */
	private static final String getSaveKey(EntityPlayer player) {
		return player.getCommandSenderName() + ":" + EXT_PROP_NAME;
	}

	/**
	 * Does everything I did in onLivingDeathEvent and it's static,
	 * so you now only need to use the following in the above event:
	 * ExtendedPlayer.saveProxyData((EntityPlayer) event.entity));
	 */
	public static final void saveProxyData(EntityPlayer player) {
		NBTTagCompound savedData = new NBTTagCompound();
		ExtendedPlayer.get(player).saveNBTData(savedData);
		CommonProxy.storeEntityData(getSaveKey(player), savedData);
	}

	/**
	 * This cleans up the onEntityJoinWorld event by replacing most of the code
	 * with a single line: ExtendedPlayer.loadProxyData((EntityPlayer) event.entity));
	 */
	public static final void loadProxyData(EntityPlayer player) {
		ExtendedPlayer playerData = ExtendedPlayer.get(player);
		NBTTagCompound savedData = CommonProxy.getEntityData(getSaveKey(player));
		if (savedData != null) { playerData.loadNBTData(savedData); }
		// data can by synced just by sending the appropriate packet, as everything
		// is handled internally by the packet class
		TutorialMain.packetPipeline.sendTo(new SyncPlayerPropsPacket(player), (EntityPlayerMP) player);
	}
}