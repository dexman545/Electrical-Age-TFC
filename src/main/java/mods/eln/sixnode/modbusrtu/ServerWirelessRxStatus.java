package mods.eln.sixnode.modbusrtu;

import mods.eln.misc.Utils;
import mods.eln.sixnode.wirelesssignal.aggregator.BiggerAggregator;
import net.minecraft.nbt.NBTTagCompound;

public class ServerWirelessRxStatus extends WirelessRxStatus implements IModbusSlot {

	private ModbusRtuElement rtu;

    BiggerAggregator aggregator = new BiggerAggregator();

    short getInputRegister_1;

    public ServerWirelessRxStatus(String name, int id, boolean connected, int uuid, ModbusRtuElement rtu) {
		super(name, id, connected, uuid);
		this.rtu = rtu;
		rtu.mapping.add(this);
	}

	public ServerWirelessRxStatus(NBTTagCompound nbt, String str, ModbusRtuElement rtu) {
		super();
		readFromNBT(nbt, str);
		this.rtu = rtu;
		rtu.mapping.add(this);
	}

	void delete() {
		rtu.mapping.remove(this);
	}
	
	boolean isConnected() {
		return null != rtu.txSet.get(name);
	}
	
	double readWireless() {
		if(!isConnected()) return 0;
		return aggregator.aggregate(rtu.txSet.get(name));
	}

	@Override
	public int getOffset() {
		return id;
	}

	@Override
	public int getSize() {
		return 4;
	}

	@Override
	public boolean getCoil(int id) {
		return false;
	}

	@Override
	public short getHoldingRegister(int id) {
		return 0;
	}

	@Override
	public boolean getInput(int id) {
		switch(id) {
            case 0:
                return isConnected();
            case 1:
                return readWireless() >= 0.5;
		}
		return false;
	}
	
	@Override
	public short getInputRegister(int id) {
		switch(id){
            case 0:
                return (short)(isConnected() ? 1 : 0);
            case 1:
                float v = (float) readWireless();
                getInputRegister_1 = Utils.modbusToShort(v, 1);
                return Utils.modbusToShort(v, 0);
            case 2:
                return getInputRegister_1;
            case 3:
                return (short)(65535.0 * readWireless());
		}
		return 0;
	}

	@Override
	public void setCoil(int id, boolean value) {
	}

	@Override
	public void setHoldingRegister(int id, short value) {
	}

	@Override
	public void setInput(int id, boolean value) {
	}

	@Override
	public void setInputRegister(int id, short value) {
	}

	@Override
	public void writeCoil(int id, boolean value) {
	}

	@Override
	public void writeHoldingRegister(int id, short value) {
	}
}
