package mods.eln.sixnode.electricalredstoneinput;

import mods.eln.misc.Obj3D;
import mods.eln.misc.Obj3D.Obj3DPart;
import mods.eln.misc.Utils;
import mods.eln.misc.UtilsClient;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.wiki.Data;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ElectricalRedstoneInputDescriptor extends SixNodeDescriptor {

	public float[] pinDistance;

    Obj3D obj;
    Obj3DPart main, led;

	public ElectricalRedstoneInputDescriptor(String name, Obj3D obj) {
		super(name, ElectricalRedstoneInputElement.class, ElectricalRedstoneInputRender.class);
		//obj = Eln.instance.obj.getObj(objName);
		this.obj = obj;
		if (obj != null) {
			main = obj.getPart("main");
			led = obj.getPart("led");

			pinDistance = Utils.getSixNodePinDistance(main);
		}
	}

	void draw(int redstone) {
		//LRDU.Down.glRotateOnX();
		if (main != null) main.draw();
		
		float light = redstone / 15f;
		GL11.glColor4f(light, light, light, 1f);
		UtilsClient.drawLight(led);
	}

	@Override
	public boolean use2DIcon() {
		return false;
	}

	@Override
	public void setParent(Item item, int damage) {
		super.setParent(item, damage);
		Data.addSignal(newItemStack());
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		super.addInformation(itemStack, entityPlayer, list, par4);
		list.add("Converts redstone signal");
		list.add("into voltage signal.");
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
	public boolean shouldUseRenderHelperEln(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (type == ItemRenderType.INVENTORY) GL11.glScalef(2.8f, 2.8f, 2.8f);
		draw(15);
	}
}
