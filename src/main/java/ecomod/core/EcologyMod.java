package ecomod.core;

import java.io.File;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.IPollutionGetter;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.TEPollutionConfig;
import ecomod.common.pollution.TEPollutionConfig.TEPollution;
import ecomod.common.pollution.handlers.PollutionHandler;
import ecomod.common.proxy.ComProxy;
import ecomod.core.stuff.EMCommands;
import ecomod.core.stuff.EMConfig;
import ecomod.core.stuff.MainRegistry;
import ecomod.network.EMPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;


@Mod(modid = EMConsts.modid, name = EMConsts.name, version = EMConsts.version, dependencies = EMConsts.deps, updateJSON = EMConsts.json, canBeDeactivated = false)
public class EcologyMod
{
	@Instance(EMConsts.modid)
	public static EcologyMod instance;
	
	public static Logger log;
	
	@SidedProxy(modId=EMConsts.modid, clientSide=EMConsts.client_proxy, serverSide=EMConsts.common_proxy)
	public static ComProxy proxy;
	
	public static PollutionHandler ph;
	
	public TEPollutionConfig tepc;
	
	static
	{
		FluidRegistry.enableUniversalBucket();
	}
	
	//ModEventHandlers
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		log = LogManager.getLogger(EMConsts.name);
		
		log.info("Preinitialization");
		
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		
		new File(event.getModConfigurationDirectory().getAbsolutePath()+"/"+EMConsts.modid).mkdirs();
		
		EMConfig.config = cfg;
		
		EMConfig.sync();
		
		setupMeta(event.getModMetadata());
		
		if(proxy == null)
		{
			log.fatal("Unable to load proxies!!");
			Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(new NullPointerException("Unable to load either common or client proxy!!!"), "Unable to load either common or client proxy!!!"));
			return;
		}
		
		MainRegistry.doPreInit();
		
		ph = new PollutionHandler();
		
		EcomodAPI.pollution_getter = (IPollutionGetter)ph;
		
		MinecraftForge.EVENT_BUS.register(ph);
		MinecraftForge.TERRAIN_GEN_BUS.register(ph);
		
		proxy.doPreInit();
		
		tepc = new TEPollutionConfig();
		
		tepc.load(event.getModConfigurationDirectory().getAbsolutePath());
		
		//EMConfig.setupEffects(event.getModConfigurationDirectory().getAbsolutePath());
		//EMConfig.setupSources(event.getModConfigurationDirectory().getAbsolutePath());
		
		PollutionSourcesConfig psc = new PollutionSourcesConfig();
		psc.load(event.getModConfigurationDirectory().getAbsolutePath());
		psc.pushToApi();
		
		PollutionEffectsConfig pec = new PollutionEffectsConfig();
		pec.load(event.getModConfigurationDirectory().getAbsolutePath());
		pec.pushToApi();
		
		EMPacketHandler.init();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		log.info("Initialization");
		
		MainRegistry.doInit();
		
		proxy.doInit();
		
		/*
		log.info(GameData.getTileEntityRegistry().getKeys().size());
		
		for(ResourceLocation rl : GameData.getTileEntityRegistry().getKeys())
		{
			log.info(rl.toString());
		}*/
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		log.info("Postinitialization");
		
		MainRegistry.doPostInit();
	}
	
	
	@EventHandler
	public void onIMC(IMCEvent event)
	{	
		for(IMCMessage m : event.getMessages())
		{
			if(m.key.toLowerCase().contentEquals("tepc_add") && m.isStringMessage())
			{
				TEPollution tep = TEPollution.fromJson(m.getStringValue());
				
				if(tep == null)
				{
					log.warn(m.getSender()+" tried to add TEPollution as "+m.getStringValue()+" but it has incorrect JSON syntax!");
				}
				else
				{
					if(tepc.hasTile(new ResourceLocation(tep.getId())))
					{
						log.warn("Mod ["+m.getSender()+"] replaced "+tepc.getTEP(tep.getId()).toString()+" in TEPC with "+tep.toString());
						tepc.data.remove(tepc.getTEP(tep.getId()));
					}
					else
					{
						log.info("Added "+tep.toString()+" to TEPC by "+m.getSender());
					}
					
					tepc.data.add(tep);
				}
			}
			
			if(m.key.toLowerCase().contentEquals("blacklist_item") && m.isStringMessage())
			{
				log.info("Mod ["+m.getSender()+"] blacklisted "+m.getStringValue());
				EMConfig.item_blacklist.add(m.getStringValue());
			}
		}
	}
	
	@EventHandler
	public void onServerStart(FMLServerStartingEvent event)
	{
		EMCommands.onServerStart(event);
	}
	
	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event)
	{
		ph.onServerStopping();
	}
	
	
	//Utils
	
	private static void setupMeta(ModMetadata meta)
	{
		meta.autogenerated=false;
		meta.credits = "Artem226";
		meta.authorList = Arrays.asList(new String[]{"Artem226"});
		meta.modId = EMConsts.modid;
		meta.name = EMConsts.name;
		meta.logoFile = "emlogo.png";
		meta.updateJSON = EMConsts.json;
		meta.url = EMConsts.projectURL;
		meta.version = EMConsts.version;
	}
}