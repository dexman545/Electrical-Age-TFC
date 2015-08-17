package mods.eln.sixnode.electricallightsensor;

import mods.eln.misc.Coordonate;
import mods.eln.misc.Utils;
import mods.eln.sim.IProcess;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class ElectricalLightSensorSlowProcess implements IProcess {

	ElectricalLightSensorElement element;

    double timeCounter = 0;
    static final double refreshPeriode = 0.2;
	
	public ElectricalLightSensorSlowProcess(ElectricalLightSensorElement element) {
		this.element = element;
	}

	@Override
	public void process(double time) {
		timeCounter += time;

		if (timeCounter > refreshPeriode) {
			timeCounter -= refreshPeriode;
			
			if (!element.sixNode.coordonate.getBlockExist()) return;
			Coordonate coord = element.sixNode.coordonate;
			//int light = coord.world().getSavedLightValue(EnumSkyBlock.Sky, coord.x, coord.y, coord.z) - coord.world().skylightSubtracted;
			//	Utils.println("Light : " + light);
			World world = coord.world();
			int light = 0;
			//if(element.descriptor.dayLightOnly) {
		        if ((!world.provider.hasNoSky)) {
		            int i1 = world.getSavedLightValue(EnumSkyBlock.Sky, coord.x, coord.y, coord.z) - world.skylightSubtracted;
		            i1 = Math.max(0, i1);
		            float f = world.getCelestialAngleRadians(1.0F);
	
		            if (f < (float)Math.PI) {
		                f += (0.0F - f) * 0.2F;
		            } else {
		                f += (((float)Math.PI * 2F) - f) * 0.2F;
		            }
	
		            i1 = Math.round((float)i1 * MathHelper.cos(f));
	
		            if (i1 < 0) {
		                i1 = 0;
		            }
	
		            if (i1 > 15) {
		                i1 = 15;
		            }
	
		            light = i1;
		        }
			//}
			if (!element.descriptor.dayLightOnly) {
				// light = Math.max(light, (int)(world.getBlockLightValue(coord.x, coord.y, coord.z)));
				//light = 0;
				light = Math.max(light, Utils.getLight(world,EnumSkyBlock.Block, coord.x, coord.y, coord.z));
			}
			element.outputGateProcess.setOutputNormalized(light / 15.0);
		}
	}
}
