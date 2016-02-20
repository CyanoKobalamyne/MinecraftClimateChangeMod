package ccpm.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import DummyCore.Utils.DrawUtils;
import ccpm.api.IRespirator;
import ccpm.core.CCPM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CCPMRenderHandler {
	
	public static final ResourceLocation respTexture = new ResourceLocation(CCPM.MODID+":textures/hud/respHud.png");

	public CCPMRenderHandler() {
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void fogColor(EntityViewRenderEvent.FogColors event)
	{
		//System.out.println("red"+event.red+"green"+event.green+"blue"+event.blue);
		if(isPlayerInSmog(Minecraft.getMinecraft().thePlayer))
		{
			//FMLLog.info("Coloring fog");
			event.red = 0.61F;
			event.green = 0.54F;
			event.blue = 0.54F;
		}
	}
	private boolean b = false;
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void fogRender(EntityViewRenderEvent.RenderFogEvent event)
	{
		if(isPlayerInSmog(Minecraft.getMinecraft().thePlayer))
		{
			//FMLLog.info("Rendering fog");
			GlStateManager.setFogStart(3.4F); // 0.7
			GlStateManager.setFogEnd(4.5F);
			if(!b)
				b = true;
		}
		else
		{
			if(b)
			{
				GlStateManager.setFogDensity(1);
				//GL11.glFogi(GL11.GL_FOG_MODE, 2048);
				b=false;
			}
		}
	}
	
	
	
	boolean isPlayerInSmog(EntityPlayer p)
	{
		return p.isPotionActive(CCPM.smog);
	}
	/*
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderHud(RenderGameOverlayEvent.Pre event)
	{
		//FMLLog.info("renderHud called");
		// Resolution of the Minecraft
		ScaledResolution scRes = event.resolution;
		
		if(event.type != ElementType.EXPERIENCE)
			return;
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		
		
		//TODO Add the glass cracking by the respirator damage
		ItemStack respStack = player.getEquipmentInSlot(4);
		if(respStack != null && respStack.getItem() instanceof IRespirator)
		{
		//FMLLog.info("CKP CKP CKP");
		IRespirator resp = (IRespirator) respStack.getItem();
		
		if(resp.renderHud())
		{
			//FMLLog.info("Rendering Hud");
			//The magic of tessellator is starting
			int h = scRes.getScaledHeight();
			int w = scRes.getScaledWidth();

			Tessellator tess = Tessellator.instance;
			//Bind the texture
			DrawUtils.bindTexture(respTexture.getResourceDomain(), respTexture.getResourcePath());
			
			tess.startDrawingQuads();
			
			tess.addVertexWithUV(0, h, -90D, 0, 1);
			
			tess.addVertexWithUV(w, h, -90D, 1, 1);
			
			tess.addVertexWithUV(w, 0, -90D, 1, 0);
			
			tess.addVertexWithUV(0, 0, -90D, 0, 0);
			
			tess.draw();
		}
	}
	}
	*/
}
