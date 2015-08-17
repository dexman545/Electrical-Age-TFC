package mods.eln.transparentnode.thermaldissipatoractive;

import java.util.List;

import mods.eln.Eln;
import mods.eln.misc.Obj3D;
import mods.eln.misc.Obj3D.Obj3DPart;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.mna.component.Resistor;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import mods.eln.wiki.Data;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ThermalDissipatorActiveDescriptor extends TransparentNodeDescriptor {
	
	double nominalP, nominalT;
	private Obj3D obj;
	private Obj3DPart main;
	private Obj3DPart rot;
	
	public ThermalDissipatorActiveDescriptor(
			String name, 
			Obj3D obj,
			double nominalElectricalU,double electricalNominalP,
			double nominalElectricalCoolingPower,
			ElectricalCableDescriptor cableDescriptor,
			double warmLimit,double coolLimit, 
			double nominalP, double nominalT,
			double nominalTao, double nominalConnectionDrop
			) {
		super(name, ThermalDissipatorActiveElement.class, ThermalDissipatorActiveRender.class);
		this.cableDescriptor = cableDescriptor;
		this.electricalNominalP = electricalNominalP;
		this.nominalElectricalU = nominalElectricalU;
		this.nominalElectricalCoolingPower = nominalElectricalCoolingPower;
		electricalRp = nominalElectricalU*nominalElectricalU / electricalNominalP;
		electricalToThermalRp = nominalT / nominalElectricalCoolingPower;
		thermalC = (nominalP + nominalElectricalCoolingPower) * nominalTao / nominalT;
		thermalRp = nominalT / nominalP;
		thermalRs = nominalConnectionDrop / (nominalP + nominalElectricalCoolingPower);
		Eln.simulator.checkThermalLoad(thermalRs, thermalRp, thermalC);
		this.coolLimit = coolLimit;
		this.warmLimit = warmLimit;
		this.nominalP = nominalP;
		this.nominalT = nominalT;
		this.obj = obj;
		if(obj != null){
			main = obj.getPart("main");
			rot = obj.getPart("rot");
		}
	}
	double warmLimit, coolLimit;
	double nominalElectricalU;
	double nominalElectricalCoolingPower;
	public void applyTo(ThermalLoad load)
	{
		load.set(thermalRs, thermalRp, thermalC);
	}
	public void setParent(net.minecraft.item.Item item, int damage)
	{
		super.setParent(item, damage);
		Data.addThermal(newItemStack());
	}
	
	public double thermalRs,thermalRp,thermalC;
	double electricalRp;
	double electricalToThermalRp;
	public double electricalNominalP;
	ElectricalCableDescriptor cableDescriptor;
	public void applyTo(ElectricalLoad load,Resistor r)
	{
		cableDescriptor.applyTo(load);
		r.setR(electricalRp);
	}
	
	
	void draw(float alpha)
	{
		if(main != null) main.draw();
		if(rot != null) rot.draw(alpha, 0f, 1f, 0f);
	}
	
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		
		return true;
	}
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		
		return true;
	}
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		draw(0f);
	}

	@Override
	public boolean use2DIcon() {
		return false;
	}
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer,
			List list, boolean par4) {
		
		super.addInformation(itemStack, entityPlayer, list, par4);
		list.add("Used for cooling the Turbine.");
		list.add(Utils.plotCelsius("Tmax :", warmLimit));
		list.add("Nominal usage ->");
		list.add(Utils.plotCelsius("  Temperature :", nominalT));
		list.add(Utils.plotPower("  Cooling :", nominalP));
		list.add(Utils.plotVolt("  Fan Voltage :", nominalElectricalU));
		list.add(Utils.plotPower("  Fan Electrical Power :", electricalNominalP));
		list.add(Utils.plotPower("  Fan Cooling Power :", nominalElectricalCoolingPower));

	}
}
