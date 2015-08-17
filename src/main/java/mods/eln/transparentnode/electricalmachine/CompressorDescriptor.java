package mods.eln.transparentnode.electricalmachine;

import org.lwjgl.opengl.GL11;

import mods.eln.misc.Direction;
import mods.eln.misc.Obj3D;
import mods.eln.misc.RcInterpolator;
import mods.eln.misc.Obj3D.Obj3DPart;
import mods.eln.misc.RecipesList;
import mods.eln.misc.UtilsClient;
import mods.eln.sim.ThermalLoadInitializer;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import net.minecraft.entity.item.EntityItem;

public class CompressorDescriptor extends ElectricalMachineDescriptor {

    float tyOn, tyOff;

    Obj3D obj;
    Obj3DPart main, move;
    
	public CompressorDescriptor(String name, 
                                Obj3D obj, 
                                double nominalU, double nominalP, 
                                double maximalU, ThermalLoadInitializer thermal, 
                                ElectricalCableDescriptor cable, RecipesList recipe) {
		super(name, nominalU, nominalP, maximalU, thermal, cable, recipe);
		
		this.obj = obj;
		if (obj != null) {
			main = obj.getPart("main");
			move = obj.getPart("move");
			if (move != null) {
				tyOn = move.getFloat("tyon");
				tyOff = move.getFloat("tyoff");
			}
		}		
	}

	class CompressorDescriptorHandle {
		RcInterpolator interpolator = new RcInterpolator(0.25f);
		float itemCounter = 0f;
	}
	
	@Override
	Object newDrawHandle() {
		return new CompressorDescriptorHandle();
	}

	@Override
	void draw(ElectricalMachineRender render, Object handleO, EntityItem inEntity, EntityItem outEntity, float powerFactor, float processState) {
		CompressorDescriptorHandle handle = (CompressorDescriptorHandle) handleO;

		UtilsClient.drawEntityItem(inEntity, -0.35f, 0.04f, 0.3f, handle.itemCounter, 1f);
		UtilsClient.drawEntityItem(outEntity, 0.35f, 0.04f, 0.3f, -handle.itemCounter + 139f, 1f);
		
		main.draw();
		GL11.glTranslatef(0f, tyOff + (float)Math.sqrt(handle.interpolator.get()) * (tyOn - tyOff), 0f);
		move.draw();
	}
	
	@Override
	void refresh(float deltaT, ElectricalMachineRender render, Object handleO, EntityItem inEntity, EntityItem outEntity, float powerFactor, float processState) {
		CompressorDescriptorHandle handle = (CompressorDescriptorHandle) handleO;
		handle.interpolator.setTarget(processState);
		handle.interpolator.step(deltaT);
		
		handle.itemCounter += deltaT * 90;
		while(handle.itemCounter >= 360f) handle.itemCounter -= 360;
	}
	
	@Override
	public boolean powerLrdu(Direction side, Direction front) {
		return side != front && side != front.getInverse();
	}
}
