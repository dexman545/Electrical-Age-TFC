package mods.eln.misc;

import net.minecraft.nbt.NBTTagCompound;

public class RcRcInterpolator implements INBTTReady {

	float c1, c2, target;
	float tao1Inv, tao2Inv;

	public RcRcInterpolator(float tao1,float tao2) {
		tao1Inv = 1 / tao1;
		tao2Inv = 1 / tao2;
		c1 = 0;
		c2 = 0;
		target = 0;
	}

	public void step(float deltaT) {
		c1 += (target - c1) * tao1Inv * deltaT;
		c2 += (c1 - c2) * tao2Inv * deltaT;
		
	}

	/*public void stepGraphic()
	{
		step(FrameTime.get());
	}*/

	public float get() {
		return c2;
	}

	public void setTarget(float value) {
		target = value;
	}

	public void setValue(float value) {
		c2 = value;
		c1 = value;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String str) {
		c1 = nbt.getFloat(str + "c1");
		c2 = nbt.getFloat(str + "c2");
		target = nbt.getFloat(str + "target");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String str) {
		nbt.setFloat(str + "c1", c1);
		nbt.setFloat(str + "c2", c2);
		nbt.setFloat(str + "target", target);
	}
}
