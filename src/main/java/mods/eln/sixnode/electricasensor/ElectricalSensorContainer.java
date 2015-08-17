package mods.eln.sixnode.electricasensor;

import mods.eln.gui.ISlotSkin.SlotSkin;
import mods.eln.misc.BasicContainer;
import mods.eln.node.six.SixNodeItemSlot;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ElectricalSensorContainer extends BasicContainer {

	public static final int cableSlotId = 0;
	
	public ElectricalSensorContainer(EntityPlayer player, IInventory inventory,ElectricalSensorDescriptor d) {
		super(player, inventory, new Slot[]{
				new SixNodeItemSlot(inventory, cableSlotId, 152, d.voltageOnly ? 14 : 62, 1, new Class[]{ElectricalCableDescriptor.class}, SlotSkin.medium, new String[]{"Electrical Cable slot"})
			});
	}
}
