package mods.eln.misc;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTTReady2 {
	public abstract void readFromNBT(NBTTagCompound nbt);
	public abstract void writeToNBT(NBTTagCompound nbt);
}
