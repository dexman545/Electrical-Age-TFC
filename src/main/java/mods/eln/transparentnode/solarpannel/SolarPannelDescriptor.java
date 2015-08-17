package mods.eln.transparentnode.solarpannel;

import java.util.List;

import mods.eln.Eln;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.ghost.GhostGroup;
import mods.eln.misc.Direction;
import mods.eln.misc.FunctionTable;
import mods.eln.misc.IFunction;
import mods.eln.misc.Obj3D;
import mods.eln.misc.Obj3D.Obj3DPart;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;
import mods.eln.sim.DiodeProcess;
import mods.eln.sim.ElectricalLoad;
import mods.eln.wiki.Data;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import org.lwjgl.opengl.GL11;

public class SolarPannelDescriptor extends TransparentNodeDescriptor{

	boolean basicModel;
	private Obj3D obj;
	private Obj3DPart main;
	private Obj3DPart panneau;
	private Obj3DPart foot;
	public SolarPannelDescriptor(
			String name,
			Obj3D obj,CableRenderDescriptor cableRender,
			GhostGroup ghostGroup, int solarOffsetX,int solarOffsetY,int solarOffsetZ,
			//FunctionTable solarIfSBase,
			double electricalUmax,double electricalPmax,
			double electricalDropFactor,
			double alphaMin,double alphaMax
			
			) {
		super(name, SolarPannelElement.class,SolarPannelRender.class);
		this.ghostGroup = ghostGroup;

		electricalRs = 	electricalUmax*electricalUmax*electricalDropFactor
						/electricalPmax/2.0;
		this.electricalPmax = electricalPmax;
		this.solarOffsetX = solarOffsetX;
		this.solarOffsetY = solarOffsetY;
		this.solarOffsetZ = solarOffsetZ;
		this.alphaMax = alphaMax;
		this.alphaMin = alphaMin;
		basicModel = true;
		this.electricalUmax = electricalUmax;
		
		this.obj = obj;
		if(obj != null){
			main = obj.getPart("main");
			panneau = obj.getPart("panneau");
			foot = obj.getPart("foot");
		}

		this.cableRender = cableRender;
		
		canRotate = alphaMax != alphaMin;
	}
	
	
	public void setParent(net.minecraft.item.Item item, int damage)
	{
		super.setParent(item, damage);
		Data.addEnergy(newItemStack());
	}
	@Override
	public boolean use2DIcon() {
		return false;
	}
	CableRenderDescriptor cableRender;
	double electricalUmax;
	double electricalPmax;
	public SolarPannelDescriptor(
			String name,
			GhostGroup ghostGroup, int solarOffsetX,int solarOffsetY,int solarOffsetZ,
			FunctionTable diodeIfUBase,
			FunctionTable solarIfSBase,
			double electricalUmax,double electricalImax,
			double electricalDropFactor,
			double alphaMin,double alphaMax
			
			) {
		super(name, SolarPannelElement.class,SolarPannelRender.class);
		this.ghostGroup = ghostGroup;
		this.diodeIfU = diodeIfUBase.duplicate(electricalUmax,electricalImax);
		electricalRs = 	electricalUmax*electricalImax*electricalDropFactor
						/electricalImax/electricalImax/2.0;
	//	this.efficiency = efficiency;
		this.solarIfS = solarIfSBase.duplicate(1.0,electricalImax);
		this.solarOffsetX = solarOffsetX;
		this.solarOffsetY = solarOffsetY;
		this.solarOffsetZ = solarOffsetZ;
		this.alphaMax = alphaMax;
		this.alphaMin = alphaMin;
		basicModel = false;
		canRotate = alphaMax != alphaMin;
	}
	int solarOffsetX, solarOffsetY, solarOffsetZ;
	double alphaMin, alphaMax;
	//double efficiency;
	double electricalRs;
	IFunction diodeIfU;
	FunctionTable solarIfS;

	boolean canRotate;
	
	public void applyTo(ElectricalLoad load)
	{
		load.setRs(electricalRs);
	}
	

	
	public double alphaTrunk(double alpha)
	{
		if(alpha > alphaMax) return alphaMax;
		if(alpha < alphaMin) return alphaMin;
		return alpha;
	}
	
	
	void draw(float alpha,Direction front)
	{
		if(foot != null) foot.draw();
		if(panneau != null){
			GL11.glPushMatrix();
			panneau.draw(alpha,0f,0f,1f);
			GL11.glPopMatrix();
		}
		front.glRotateXnRef();
		if(main != null) main.draw();
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		
		return true;
	}
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		
		return true;
	}
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		
		draw((float) alphaMin,Direction.XN);
	}
	
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer,
			List list, boolean par4) {
		
		super.addInformation(itemStack, entityPlayer, list, par4);
		
		list.add("Produces power from daylight.");
		list.add(Utils.plotVolt("Voltage Max:", electricalUmax));
		list.add(Utils.plotPower("Power Max:", electricalPmax));
		if(canRotate) list.add("Can be oriented.");
	}
	
	@Override
	public void addCollisionBoxesToList(AxisAlignedBB par5AxisAlignedBB,
			List list, TransparentNodeEntity entity) {
		if(canRotate) {
			super.addCollisionBoxesToList(par5AxisAlignedBB, list, entity);
			return;
		}
		AxisAlignedBB bb = Blocks.stone.getCollisionBoundingBoxFromPool(entity.getWorldObj(), entity.xCoord, entity.yCoord, entity.zCoord);
		bb.maxY -= 0.5;
		if(par5AxisAlignedBB.intersectsWith(bb)) list.add(bb);
	}
}
