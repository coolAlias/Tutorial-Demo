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
import tutorial.entity.EntityThrowingRock;
import tutorial.item.ItemThrowingRock;
import tutorial.item.ItemUseMana;
import tutorial.item.ItemWabbajack;
import tutorial.item.ItemWizardArmor;
import tutorial.item.crafting.RecipesAll;
import tutorial.item.crafting.RecipesWizardArmorDyes;
import tutorial.network.PacketPipeline;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "tutorial", name = "Tutorial", version = "1.7.2-1.0.0")
public final class TutorialMain
{
	@Instance("tutorial")
	public static TutorialMain instance = new TutorialMain();

	@SidedProxy(clientSide = "tutorial.ClientProxy", serverSide = "tutorial.CommonProxy")
	public static CommonProxy proxy;

	/** Updated PacketHandler from Forge wiki tutorial: http://www.minecraftforge.net/wiki/Netty_Packet_Handling */
	public static final PacketPipeline packetPipeline = new PacketPipeline();

	/** This is used to keep track of GUIs that we make*/
	private static int modGuiIndex = 0;

	/** Custom GUI indices: */
	public static final int GUI_CUSTOM_INV = modGuiIndex++;

	/** This is the starting index for all of our mod's item IDs */
	private static int modEntityIndex = 0;

	/** If true, Wizard Armor will be loaded */
	public static boolean wizardArmorFlag;

	// MISC ITEMS
	public static Item
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
	public static final ArmorMaterial armorWool = EnumHelper.addArmorMaterial("Wool", 5, new int[] {1,2,1,1}, 30);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getAbsolutePath() + "/Tutorial.cfg"));
		config.load();
		wizardArmorFlag = config.get(Configuration.CATEGORY_GENERAL, "WizardArmorFlag", true).getBoolean(true);
		config.save();
		
		useMana = new ItemUseMana().setUnlocalizedName("use_mana");
		GameRegistry.registerItem(useMana, useMana.getUnlocalizedName());
		throwingRock = new ItemThrowingRock().setUnlocalizedName("throwingRock");
		GameRegistry.registerItem(throwingRock, throwingRock.getUnlocalizedName());
		EntityRegistry.registerModEntity(EntityThrowingRock.class, "Throwing Rock", ++modEntityIndex, this, 64, 10, true);
		wabbajack = new ItemWabbajack().setUnlocalizedName("wabbajack");
		GameRegistry.registerItem(wabbajack, wabbajack.getUnlocalizedName());

		if (wizardArmorFlag) {
			wizardHat = new ItemWizardArmor(armorWool, proxy.addArmor("wizard"), 0).setUnlocalizedName("wizard_hat");
			wizardRobe = new ItemWizardArmor(armorWool, proxy.addArmor("wizard"), 1).setUnlocalizedName("wizard_robe");
			wizardPants = new ItemWizardArmor(armorWool, proxy.addArmor("wizard"), 2).setUnlocalizedName("wizard_pants");
			wizardBoots = new ItemWizardArmor(armorWool, proxy.addArmor("wizard"), 3).setUnlocalizedName("wizard_boots");
			GameRegistry.registerItem(wizardHat, wizardHat.getUnlocalizedName());
			GameRegistry.registerItem(wizardRobe, wizardRobe.getUnlocalizedName());
			GameRegistry.registerItem(wizardPants, wizardPants.getUnlocalizedName());
			GameRegistry.registerItem(wizardBoots, wizardBoots.getUnlocalizedName());
		}
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		packetPipeline.initialise();
		proxy.registerRenderers();
		MinecraftForge.EVENT_BUS.register(new TutEventHandler());
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new CommonProxy());
	}

	@EventHandler
	public void postInitialise(FMLPostInitializationEvent event) {
		packetPipeline.postInitialise();
		
		// we'll add recipes last, to make sure all items and blocks are ready to go
		GameRegistry.addShapelessRecipe(new ItemStack(useMana), Items.diamond);
		if (wizardArmorFlag) {
			CraftingManager.getInstance().getRecipeList().add(new RecipesWizardArmorDyes());
			RecipesAll.instance().addArmorRecipes(CraftingManager.getInstance());
		}
	}
}