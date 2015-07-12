package tutorial;

import java.io.File;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;

import tutorial.entity.EntityThrowingRock;
import tutorial.item.ItemMagicBag;
import tutorial.item.ItemThrowingRock;
import tutorial.item.ItemUseMana;
import tutorial.item.ItemWabbajack;
import tutorial.item.ItemWizardArmor;
import tutorial.item.crafting.RecipesAll;
import tutorial.item.crafting.RecipesWizardArmorDyes;
import tutorial.network.PacketDispatcher;

@Mod(modid = TutorialMain.MODID, version = TutorialMain.VERSION)
public final class TutorialMain
{
	public static final String MODID = "tutorial";
	public static final String VERSION = "1.0";

	@Mod.Instance(MODID)
	public static TutorialMain instance;

	@SidedProxy(clientSide = "tutorial.ClientProxy", serverSide = "tutorial.CommonProxy")
	public static CommonProxy proxy;

	public static final Logger logger = LogManager.getLogger(MODID);

	/**
	 * Current recommended version of Networking is to use the SimpleNetworkWrapper class - don't forget to register each packet!
	 * Mine is commented out because I implemented it inside of a class; see {@link PacketDispatcher}
	 * If you don't need the class, simply create your instance here:
	 */
	//public static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

	/** This is used to keep track of GUIs that we make*/
	private static int modGuiIndex = 10;

	/** Custom GUI indices: */
	public static final int
	GUI_CUSTOM_INV = modGuiIndex++,
	GUI_ITEM_INV = modGuiIndex++;

	/** This is the starting index for all of our mod's item IDs */
	private static int modEntityIndex = 0;

	/** If true, Wizard Armor will be loaded */
	public static boolean wizardArmorFlag;

	// MISC ITEMS
	public static Item
	magicBag,
	useMana,
	throwingRock,
	wabbajack;

	// ARMOR ITEMS
	public static Item
	wizardHat,
	wizardRobe,
	wizardPants,
	wizardBoots;

	// ARMOR MATERIALS
	public static final ArmorMaterial armorWool = EnumHelper.addArmorMaterial("Wool", "FakeTexture", 5, new int[] {1,2,1,1}, 30);

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Beginning pre-initialization");
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getAbsolutePath() + "/Tutorial.cfg"));
		config.load();
		wizardArmorFlag = config.get(Configuration.CATEGORY_GENERAL, "WizardArmorFlag", true).getBoolean(true);
		config.save();

		// Initialize and register all blocks, items, and entities
		magicBag = GameRegistry.registerItem((new ItemMagicBag().setUnlocalizedName("magic_bag")), "magic_bag", null);
		useMana = GameRegistry.registerItem((new ItemUseMana().setUnlocalizedName("use_mana")), "use_mana", null);
		throwingRock = GameRegistry.registerItem((new ItemThrowingRock().setUnlocalizedName("throwing_rock")), "throwing_rock", null);
		EntityRegistry.registerModEntity(EntityThrowingRock.class, "Throwing Rock", ++modEntityIndex, this, 64, 10, true);
		wabbajack = GameRegistry.registerItem((new ItemWabbajack().setUnlocalizedName("wabbajack")), "wabbajack", null);

		if (wizardArmorFlag) {
			wizardHat = GameRegistry.registerItem((new ItemWizardArmor(armorWool, proxy.addArmor("wizard"), 0).setUnlocalizedName("wizard_helmet")), "wizard_helmet", null);
			wizardRobe = GameRegistry.registerItem((new ItemWizardArmor(armorWool, proxy.addArmor("wizard"), 1).setUnlocalizedName("wizard_chestplate")), "wizard_chestplate", null);
			wizardPants = GameRegistry.registerItem((new ItemWizardArmor(armorWool, proxy.addArmor("wizard"), 2).setUnlocalizedName("wizard_leggings")), "wizard_leggings", null);
			wizardBoots = GameRegistry.registerItem((new ItemWizardArmor(armorWool, proxy.addArmor("wizard"), 3).setUnlocalizedName("wizard_boots")), "wizard_boots", null);
		}

		// Remember to register your packets! This applies whether or not you used a
		// custom class or direct implementation of SimpleNetworkWrapper
		PacketDispatcher.registerPackets();
	}

	@Mod.EventHandler
	public void load(FMLInitializationEvent event) {
		// Register block, item, and entity renderers after they have been initialized and
		// registered in pre-init; however, Minecraft's RenderItem and ModelMesher instances
		// must also be ready, so we have to register renderers during init, not earlier
		proxy.registerRenderers();

		// Register our event listener:
		TutEventHandler events = new TutEventHandler();
		MinecraftForge.EVENT_BUS.register(events);

		// Register our Gui Handler
		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

		// Crafting recipes are best loaded during this stage of mod loading
		GameRegistry.addShapelessRecipe(new ItemStack(useMana), Items.diamond);
		if (wizardArmorFlag) {
			CraftingManager.getInstance().getRecipeList().add(new RecipesWizardArmorDyes());
			RecipesAll.instance().addArmorRecipes(CraftingManager.getInstance());
		}
	}

	@Mod.EventHandler
	public void postInitialise(FMLPostInitializationEvent event) {
		// this is generally a good place to modify recipes or otherwise interact with other mods
	}
}
