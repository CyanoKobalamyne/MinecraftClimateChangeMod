package ecomod.core.stuff;

import ecomod.api.EcomodItems;
import ecomod.common.items.ItemCore;
import ecomod.common.items.ItemCraftIngredient;
import ecomod.common.items.ItemRespirator;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EMItems 
{
	public static ArmorMaterial RESPIRATOR_MATERIAL = null;
	
	public static void doPreInit()
	{
		if(!EcomodItems.inited)
		{
			EcomodItems.inited = true;
			
			EcomodItems.CORE = new ItemCore().setUnlocalizedName(EMConsts.modid+".core");
			
			EcomodItems.CRAFT_INGREDIENT = new ItemCraftIngredient().setUnlocalizedName(EMConsts.modid+".craft_ingredient");
			
			RESPIRATOR_MATERIAL = EnumHelper.addArmorMaterial("ecomod_respirator", EMConsts.modid+":respirator", 13, new int[]{2, 2, 3, 1}, 7, SoundEvents.ENTITY_PLAYER_BREATH, 0.0F);
			
			RESPIRATOR_MATERIAL.setRepairItem(new ItemStack(Items.LEATHER,1,0));
			
			if(RESPIRATOR_MATERIAL == null)
			{
				EcologyMod.log.error("Unable to register Respirator Armor Material!!!");
			}
			
			EcomodItems.RESPIRATOR = new ItemRespirator().setUnlocalizedName(EMConsts.modid+".respirator");
			
			regItem(EcomodItems.CORE, "core", false, EMUtils.resloc("filter_core"), EMUtils.resloc("advanced_filter_core"), EMUtils.resloc("analyzer_core"));
			
			regItem(EcomodItems.CRAFT_INGREDIENT, "craft_ingredient", false, EMUtils.resloc("piston_array"), EMUtils.resloc("vent"));
			
			regItem(EcomodItems.RESPIRATOR, "respirator", true, EMUtils.resloc("respirator"));
		}
	}
	
	public static void doInit()
	{
		EcologyMod.proxy.registerItemModel(EcomodItems.CORE, 0, "filter_core");
		EcologyMod.proxy.registerItemModel(EcomodItems.CORE, 1, "advanced_filter_core");
		EcologyMod.proxy.registerItemModel(EcomodItems.CORE, 2, "analyzer_core");
		
		EcologyMod.proxy.registerItemModel(EcomodItems.CRAFT_INGREDIENT, 0, "piston_array");
		EcologyMod.proxy.registerItemModel(EcomodItems.CRAFT_INGREDIENT, 1, "vent");
	}
	
	public static void regItem(Item item, String name, boolean model, ResourceLocation... variants)
	{
		ResourceLocation resloc = EMUtils.resloc(name);
		
		item.setRegistryName(resloc);
		
		GameRegistry.register(item);
		
		if(variants != null && variants.length > 0)
			ModelBakery.registerItemVariants(item, variants);
		else
			ModelBakery.registerItemVariants(item, EMUtils.resloc(name));
		
		if(model)
		{
			EcologyMod.proxy.putItemToBeRegistred(item);
		}
	}
}