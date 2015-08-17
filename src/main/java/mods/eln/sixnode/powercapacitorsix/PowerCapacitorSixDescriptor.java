package mods.eln.sixnode.powercapacitorsix;

import java.util.List;

import mods.eln.Eln;
import mods.eln.item.DielectricItem;
import mods.eln.misc.Obj3D;
import mods.eln.misc.Obj3D.Obj3DPart;
import mods.eln.misc.series.ISerie;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.wiki.Data;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class PowerCapacitorSixDescriptor extends SixNodeDescriptor {

	private Obj3D obj;
	
	private Obj3DPart CapacitorCore;
	private Obj3DPart CapacitorCables;
	private Obj3DPart Base;

    ISerie serie;
    public double dischargeTao;

	public PowerCapacitorSixDescriptor(String name,
                                       Obj3D obj,
                                       ISerie serie,
                                       double dischargeTao) {
		super(name, PowerCapacitorSixElement.class, PowerCapacitorSixRender.class);
		this.serie = serie;
		this.dischargeTao = dischargeTao;
		this.obj = obj;
		if (obj != null) {
			CapacitorCables = obj.getPart("CapacitorCables");
			CapacitorCore = obj.getPart("CapacitorCore");
			Base = obj.getPart("Base");
		}
	}

	public double getCValue(int cableCount, double nominalDielVoltage) {
		if (cableCount == 0) return 1e-6;
		double uTemp = nominalDielVoltage / Eln.LVU;
		return serie.getValue(cableCount - 1) / uTemp / uTemp;
	}

	@Override
	public boolean use2DIcon() {
		return false;
	}

	public double getCValue(IInventory inventory) {
		ItemStack core = inventory.getStackInSlot(PowerCapacitorSixContainer.redId);
		ItemStack diel = inventory.getStackInSlot(PowerCapacitorSixContainer.dielectricId);
		if (core == null || diel == null)
			return getCValue(0,0);
		else {
			return getCValue(core.stackSize,getUNominalValue(inventory));
		}
	}

	public double getUNominalValue(IInventory inventory) {
		ItemStack diel = inventory.getStackInSlot(PowerCapacitorSixContainer.dielectricId);
		if (diel == null)
			return 10000;
		else {
			DielectricItem desc = (DielectricItem) DielectricItem.getDescriptor(diel);
			return desc.uNominal * diel.stackSize;
		}
	}

	public void setParent(net.minecraft.item.Item item, int damage) {
		super.setParent(item, damage);
		Data.addEnergy(newItemStack());
	}

	void draw() {
		if (null != Base) Base.draw();
		if (null != CapacitorCables) CapacitorCables.draw();
		if (null != CapacitorCore) CapacitorCore.draw();
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		draw();
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		super.addInformation(itemStack, entityPlayer, list, par4);
	}
}
