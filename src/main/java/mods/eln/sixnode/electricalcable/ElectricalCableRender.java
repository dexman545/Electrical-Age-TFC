package mods.eln.sixnode.electricalcable;

import mods.eln.cable.CableRender;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.misc.UtilsClient;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElementRender;
import mods.eln.node.six.SixNodeEntity;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.IOException;

public class ElectricalCableRender extends SixNodeElementRender {

	ElectricalCableDescriptor descriptor;

    int color = 0;

	public ElectricalCableRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
		super(tileEntity, side, descriptor);
		this.descriptor = (ElectricalCableDescriptor) descriptor;
	}

//	double voltage = 0, current = 0, temperature = 0;

	public boolean drawCableAuto() {
		return false;
	}
	
	@Override
	public void draw() {
		Minecraft.getMinecraft().mcProfiler.startSection("ECable");
		
		Utils.setGlColorFromDye(color);

		UtilsClient.bindTexture(descriptor.render.cableTexture);
		glListCall();
		
		GL11.glColor3f(1f, 1f, 1f);
		Minecraft.getMinecraft().mcProfiler.endSection();
	}

	@Override
	public void glListDraw() {
		CableRender.drawCable(descriptor.render, connectedSide, CableRender.connectionType(this, side));
		CableRender.drawNode(descriptor.render, connectedSide, CableRender.connectionType(this, side));
	}
	
	@Override
	public boolean glListEnable() {
		return true;	
	}
	
	@Override
	public void publishUnserialize(DataInputStream stream) {
		super.publishUnserialize(stream);
		try {
			Byte b;
			b = stream.readByte();
			color = (b >> 4) & 0xF;
		/*	voltage = stream.readShort() / NodeBase.networkSerializeUFactor;
			current = stream.readShort() / NodeBase.networkSerializeIFactor;
			temperature = stream.readShort() / NodeBase.networkSerializeTFactor;*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public CableRenderDescriptor getCableRender(LRDU lrdu) {
		return descriptor.render;
	}

	@Override
	public int getCableDry(LRDU lrdu) {
		return color;
	}
}
