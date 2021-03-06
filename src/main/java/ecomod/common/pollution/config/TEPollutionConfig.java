package ecomod.common.pollution.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import ecomod.api.pollution.ITEPollutionConfig;
import ecomod.api.pollution.PollutionData;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TEPollutionConfig implements ITEPollutionConfig
{
	public List<TEPollution> data;
	
	public String version = EMConsts.version.substring(0, 3);
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public TEPollutionConfig()
	{
		data = new ArrayList<>();
	}
	
	public boolean hasTile(ResourceLocation key)
	{
		return getTEP(key) != null;
	}
	
	public boolean hasTile(TileEntity te)
	{
		return hasTile(TileEntity.getKey(te.getClass()));
	}
	
	public TEPollution getTEP(String str)
	{
		for(TEPollution tep : data)
		{
			if(tep.getId().contentEquals(str))	
			{
				return tep;
			}
		}
		
		return null;
	}
	
	public TEPollution getTEP(ResourceLocation resloc)
	{
		return getTEP(resloc.toString());
	}
	
	public TEPollution getTEP(TileEntity te)
	{
		return getTEP(TileEntity.getKey(te.getClass()));
	}
	
	//IO
	public static TEPollutionConfig get(String urlstr)
	{
		EcologyMod.log.info("Getting TEPC from "+urlstr);
		
		urlstr = EMUtils.parseMINECRAFTURL(urlstr);
		
		String json;
		
		try
		{
			URL url = new URL(urlstr);
			
			json = EMUtils.getString(url);
			
			return fromJson(json);
		}
		catch (MalformedURLException e)
		{
			EcologyMod.log.error("The URL of TEPollutionConfig is incorrect! Go to the mod config file ("+EMConsts.modid+".cfg) and fix it! (see https://en.wikipedia.org/wiki/URL#Syntax) Don't forget to restart Minecraft later.");
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
		}
		catch (IOException e)
		{
			//TODO add message
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
		}
		catch (JsonSyntaxException e)
		{
			//TODO add message
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public boolean save(String cfg_path)
	{
		File f = new File(cfg_path);
		
		EcologyMod.log.info("Saving TEPollutionConfig.json");
		
		TEPC t = new TEPC(version, data.toArray(new TEPollution[data.size()]));
		
		String json = gson.toJson(t, TEPC.class);
		
		
		try
		{	
			if(f.isDirectory())
			{
				f.delete();
				//throw new IOException("File 'TEPollutionConfig.json' is a directory! Please, delete it and restart Minecraft!");
			}
			
			if(!f.exists())
			{
				f.createNewFile();
			}
			
			if(f.canWrite())
			{
				FileUtils.writeStringToFile(f, json, Charset.defaultCharset());
				return true;
			}
			else
			{
				throw new IOException("The TEPC file is not writable!!!");
			}
		}
		catch(IOException e)
		{
			EcologyMod.log.error("Unable to write TEPC!");
			EcologyMod.log.error(e.toString());
			
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean loadFromFile(String cfg_path)
	{
		EcologyMod.log.info("Trying to load TEPC from file");
		
		String json;
		
		TEPC t;
		
		try
		{
			File f = new File(cfg_path);
			
			if(f.isDirectory())
			{
				f.delete();
				return false;
			}
			
			if(!f.exists())
				return false;
			
			if(f.canRead())
			{
				json = FileUtils.readFileToString(f, Charset.defaultCharset());
				
				if(json == null)
					return false;
			}
			else
			{
				throw new IOException("The file is not readable!!!");
			}
		}
		catch (IOException e)
		{
			EcologyMod.log.error(e.toString());
			
			e.printStackTrace();
			
			return false;
		}
		
		try
		{
			t = gson.fromJson(json, TEPC.class);
		}
		catch(JsonSyntaxException e)
		{
			EcologyMod.log.error("TEPollutionConfig.json has incorrect JSON syntax!!! Please, fix it! (see https://en.wikipedia.org/wiki/JSON)");
			e.printStackTrace();
			return false;
		}
		
		if(t == null)
			return false;
		
		this.version = t.getVersion();
		this.data = new ArrayList<>(Arrays.asList(t.getCfg()));
		
		return true;
	}
	
	public String path = "";
	
	public void load(String cfg_path, String url, boolean keep_entries, boolean force_update)
	{
		EcologyMod.log.info("Loading TEPC");
		
		path = cfg_path;
		
		boolean loaded_from_file = loadFromFile(cfg_path);
		
		TEPollutionConfig tepc = get(url);
		
		if(tepc == null)
		{
			if(!loaded_from_file)
			{
				//Crash MC
				throw new NullPointerException("Impossible to load the TEPC for the first time! Look for the reason in the log! If TEPC is located remotely make sure you have connection to the resource! URL ("+url+ ')');
			}
		}
		else
		{
			if(loaded_from_file)
			{
				if(force_update || !version.equals(tepc.version))
				{
					for(TEPollution tep : data)
					{
						if(tep.keep_entry != null && tep.keep_entry == true)
						{
							tepc.removeTilePollution(new ResourceLocation(tep.getId()));
							tepc.data.add(tep);
						}
					}
				
					if(keep_entries)
					{
						EMUtils.mergeLists(tepc.data, data);
					}
					
					data = tepc.data;
					version = tepc.version;
				}
			}
			else
			{
				data = tepc.data;
				version = tepc.version;
			}
		}
		
		EcologyMod.log.info("[TEPC]Loaded "+data.size()+" entries");
		
		if(!save(cfg_path))
		{
			EcologyMod.log.error("Unable to save TEPC as TEPollutionConfig.json in "+cfg_path+"! It will very likely have serious problems later!!");
			EcologyMod.log.error("Look for the reason in the log!");
		}
	}
	
	public static TEPollutionConfig fromJson(String json) throws JsonSyntaxException
	{
		TEPC t = gson.fromJson(json, TEPC.class);
		
		TEPollutionConfig tepc = new TEPollutionConfig();
		
		tepc.data = new ArrayList<>(Arrays.asList(t.getCfg()));
		tepc.version = t.getVersion();
		
		return tepc;
	}
	
	public String toJson()
	{
		return gson.toJson(new TEPC(version, data.toArray(new TEPollution[data.size()])), TEPC.class);
	}
	
	public static class TEPollution
	{
		private String id;
		
		private PollutionData emission;
		
		private Boolean keep_entry = null;
		
		public TEPollution()
		{
			
		}
		
		public TEPollution(String te_id, PollutionData data)
		{
			id = te_id;
			emission = data;
		}
		
		public String getId()
		{
			return id;
		}
		
		public PollutionData getEmission()
		{
			return emission.clone();
		}
		
		public String toString()
		{
			return "{\"id\" = \""+id+"\", \"emission\" : "+emission.toString()+ '}';
		}
		
		public static TEPollution fromJson(String json)
		{
			Gson gson = new GsonBuilder().create();
			
			try
			{
				return gson.fromJson(json, TEPollution.class);
			}
			catch (JsonSyntaxException e)
			{
				return null;
			}
			
		}
	}
	
	private static class TEPC
	{
		private String version;
		
		private TEPollution[] config;
		
		public TEPC()
		{
			
		}
		
		public TEPC(String v, TEPollution[] p)
		{
			version = v;
			config = p;
		}
		
		public TEPollution[] getCfg()
		{
			return config;
		}
		
		public String getVersion()
		{
			return version;
		}
	}

	@Override
	public boolean containsTile(ResourceLocation id) {
		return hasTile(id);
	}

	@Override
	public PollutionData getTilePollution(ResourceLocation id) {
		return hasTile(id) ? getTEP(id).getEmission() : PollutionData.getEmpty();
	}

	@Override
	public boolean removeTilePollution(ResourceLocation id) {
		if(hasTile(id))
		{
			data.remove(getTEP(id));
			return true;
		}
		return false;
	}

	@Override
	public boolean addTilePollution(ResourceLocation id, PollutionData emission, boolean override) {
		if(hasTile(id))
		{
			if(!override)
			{
				return false;
			}
			data.remove(getTEP(id));
		}
		
		return data.add(new TEPollution(id.toString(), emission));
	}

	@Override
	public String getVersion() {
		return version;
	}
}
