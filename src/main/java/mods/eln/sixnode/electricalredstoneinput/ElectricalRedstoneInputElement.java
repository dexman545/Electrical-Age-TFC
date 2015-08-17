package mods.eln.sixnode.electricalredstoneinput;

import java.io.DataOutputStream;
import java.io.IOException;

import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBase;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElement;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.nbt.NbtElectricalGateOutput;
import mods.eln.sim.nbt.NbtElectricalGateOutputProcess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ElectricalRedstoneInputElement extends SixNodeElement {

	ElectricalRedstoneInputDescriptor descriptor;

    public NbtElectricalGateOutput outputGate = new NbtElectricalGateOutput("outputGate");
    public NbtElectricalGateOutputProcess outputGateProcess = new NbtElectricalGateOutputProcess("outputGateProcess", outputGate);
    public ElectricalRedstoneInputSlowProcess slowProcess = new ElectricalRedstoneInputSlowProcess(this);

    boolean warm = false;

	public ElectricalRedstoneInputElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
		super(sixNode, side, descriptor);
		front = LRDU.Left;
    	electricalLoadList.add(outputGate);
    	electricalComponentList.add(outputGateProcess);
    	slowProcessList.add(slowProcess);
    	this.descriptor = (ElectricalRedstoneInputDescriptor) descriptor;
	}

	@Override
	public boolean canConnectRedstone() {
		return true;
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
		if (front == lrdu.left()) return outputGate;
		return null;
	}

	@Override
	public ThermalLoad getThermalLoad(LRDU lrdu) {
		return null;
	}

	@Override
	public int getConnectionMask(LRDU lrdu) {
		if (front == lrdu.left()) return NodeBase.maskElectricalOutputGate;
		return 0;
	}

	@Override
	public String multiMeterString() {
		return Utils.plotVolt("U:", outputGate.getU()) + Utils.plotAmpere("I:", outputGate.getCurrent());
	}

	@Override
	public String thermoMeterString() {
		return "";
	}

	@Override
	public void networkSerialize(DataOutputStream stream) {
		super.networkSerialize(stream);
		try {
			stream.writeByte(slowProcess.oldSignal);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setWarm(boolean value) {
		if (warm != value) {
			needPublish();
		}
		warm = value;
	}
	
	@Override
	public void initialize() {
	}

	@Override
	public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
		ItemStack currentItemStack = entityPlayer.getCurrentEquippedItem();
		if (currentItemStack != null) {
			Item item = currentItemStack.getItem();
			/*if (item== Eln.toolsSetItem) {
				colorCare = colorCare ^ 1;
				entityPlayer.addChatMessage("Wire color care " + colorCare);
				sixNode.reconnect();
			}
			if (item == Eln.brushItem) {
				if (currentItemStack.getItemDamage() < BrushItem.maximalUse) {
					color = currentItemStack.getItemDamage() & 0xF;
					
					currentItemStack.setItemDamage(currentItemStack.getItemDamage() + 16);
					
					sixNode.reconnect();
				} else {
					entityPlayer.addChatMessage("Brush is empty");
				}
			}*/
		}
		//front = LRDU.fromInt((front.toInt() + 1) & 3);
		if (Utils.isPlayerUsingWrench(entityPlayer)) {
			front = front.getNextClockwise();
			sixNode.reconnect();
			sixNode.setNeedPublish(true);
			return true;	
		}
		return false;
	}
}
