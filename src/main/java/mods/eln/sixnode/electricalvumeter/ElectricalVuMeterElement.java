package mods.eln.sixnode.electricalvumeter;

import mods.eln.Eln;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBase;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElement;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.nbt.NbtElectricalGateInput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataOutputStream;
import java.io.IOException;

public class ElectricalVuMeterElement extends SixNodeElement {

    public NbtElectricalGateInput inputGate = new NbtElectricalGateInput("inputGate", true);
    public ElectricalVuMeterSlowProcess slowProcess = new ElectricalVuMeterSlowProcess(this);
    LRDU front;
    ElectricalVuMeterDescriptor descriptor;

	public ElectricalVuMeterElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
		super(sixNode, side, descriptor);
		this.descriptor = (ElectricalVuMeterDescriptor) descriptor;
		front = LRDU.Down;
    	electricalLoadList.add(inputGate);
    	slowProcessList.add(slowProcess);
	}

	public static boolean canBePlacedOnSide(Direction side, int type) {
		return true;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
        byte value = nbt.getByte("front");
        front = LRDU.fromInt((value >> 0) & 0x3);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("front", (byte)((front.toInt() << 0)));
	}

	@Override
	public ElectricalLoad getElectricalLoad(LRDU lrdu) {
		if(front == lrdu) return inputGate;
		return null;
	}

	@Override
	public ThermalLoad getThermalLoad(LRDU lrdu) {
		return null;
	}

	@Override
	public int getConnectionMask(LRDU lrdu) {
		if (front == lrdu) return NodeBase.maskElectricalInputGate;
		return 0;
	}

	@Override
	public String multiMeterString() {
		return Utils.plotVolt("U:", inputGate.getU()) + Utils.plotAmpere("I:", inputGate.getCurrent());
	}

	@Override
	public String thermoMeterString() {
		return "";
	}

	@Override
	public void networkSerialize(DataOutputStream stream) {
		super.networkSerialize(stream);
		try {
			stream.writeByte((front.toInt() << 4));
	    	stream.writeFloat((float)(inputGate.getU() / Eln.instance.SVU));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize() {
	}

	@Override
	public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
		ItemStack currentItemStack = entityPlayer.getCurrentEquippedItem();

		if (Utils.isPlayerUsingWrench(entityPlayer)) {
			front = front.getNextClockwise();
			sixNode.reconnect();
			sixNode.setNeedPublish(true);
			return true;	
		}
		return false;
	}
}
