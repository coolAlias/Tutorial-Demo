package tutorial.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import tutorial.inventory.InventoryCustomPlayer;
import tutorial.network.PacketDispatcher;
import tutorial.network.packet.client.SyncPlayerPropsMessage;

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

	/**
	 * Copies additional player data from the given ExtendedPlayer instance
	 * Avoids NBT disk I/O overhead when cloning a player after respawn
	 */
	public void copy(ExtendedPlayer props) {
		inventory.copy(props.inventory);
		player.getDataWatcher().updateObject(MANA_WATCHER, props.getCurrentMana());
		maxMana = props.maxMana;
		manaRegenTimer = props.manaRegenTimer;
	}

	@Override
	public final void saveNBTData(NBTTagCompound compound) {
		// We store all of our data nested in a single tag;
		// this way, we never have to worry about conflicting with other
		// mods that may also be writing to the player's tag compound
		NBTTagCompound properties = new NBTTagCompound();
		
		// Write everything to our new tag:
		inventory.writeToNBT(properties);
		properties.setInteger("CurrentMana", player.getDataWatcher().getWatchableObjectInt(MANA_WATCHER));
		properties.setInteger("MaxMana", maxMana);
		properties.setInteger("ManaRegenTimer", manaRegenTimer);
		
		// Finally, set the tag with our unique identifier:
		compound.setTag(EXT_PROP_NAME, properties);
	}

	@Override
	public final void loadNBTData(NBTTagCompound compound) {
		// Pretty much the reverse of saveNBTData - get our
		// unique tag and then load everything from it:
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		inventory.readFromNBT(properties);
		player.getDataWatcher().updateObject(MANA_WATCHER, properties.getInteger("CurrentMana"));
		maxMana = properties.getInteger("MaxMana");
		manaRegenTimer = properties.getInteger("ManaRegenTimer");
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
		// the data each time max mana changes...
		
		PacketDispatcher.sendTo(new SyncPlayerPropsMessage(player), (EntityPlayerMP) player);
	}
}