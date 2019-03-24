package ecomod.core;

import java.util.Collections;
import java.util.List;

public class EMConsts
{
	//Mod data
	public static final String modid = "ecomod";
	
	public static final String name = "Climate Connection Mod";
	
	/**	
	 * MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH <br> 
	 * 
	 * 	https://mcforge.readthedocs.io/en/latest/conventions/versioning/
	 * 
	 */
	public static final String mcversion = "1.12.2";
	public static final String version = mcversion + "-1.0.0.0";
	
	public static final String deps = "";
	
	public static final String json = "https://raw.githubusercontent.com/CyanoKobalamyne/MinecraftClimateConnectionMod/1.12/versions.json";
	
	public static final String githubURL = "https://github.com/CyanoKobalamyne/MinecraftClimateConnectionMod";
	
	public static final String issues = "https://github.com/CyanoKobalamyne/MinecraftClimateConnectionMod/issues";
	
	public static final String projectURL = "https://github.com/CyanoKobalamyne/MinecraftClimateConnectionWorld";
	
	
	public static final String contributors = "CyanoKobalamyne, Artem226 (original author)";
	
	public static final List<String> authors = Collections.singletonList("CyanoKobalamyne");
	
	//Proxies
	
	public static final String common_proxy = "ecomod.common.proxy.ComProxy";
	public static final String client_proxy = "ecomod.client.proxy.CliProxy";
	
	// Consts and global variables
	
	public static final int analyzer_gui_id = 0;
	
	public static boolean asm_transformer_inited;
	
	public static boolean common_caps_compat$IWorker;
}
