package mods.eln.item;

import mods.eln.generic.GenericItemUsingDamageDescriptor;
import mods.eln.wiki.Data;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class BrushDescriptor  extends GenericItemUsingDamageDescriptor {

	public BrushDescriptor(String name) {
		super( name);
	}

	@Override
	public String getName(ItemStack stack) {
		int color = getColor(stack),life = getLife(stack);
		if(color == 15 && life == 0)
			return "Empty " + super.getName(stack);
		return super.getName(stack);
	}

	@Override
	public void setParent(Item item, int damage) {
		super.setParent(item, damage);
		Data.addWiring(newItemStack());
	}

	public int getColor(ItemStack stack) {
		return stack.getItemDamage() & 0xF;
	}

	public int getLife(ItemStack stack) {
		return stack.getTagCompound().getInteger("life");
	}

	public void setColor(ItemStack stack,int color) {
		stack.setItemDamage((stack.getItemDamage() & ~0xF) | color);
	}

	public void setLife(ItemStack stack,int life) {
		stack.getTagCompound().setInteger("life", life);
	}
	
	@Override
	public NBTTagCompound getDefaultNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("life", 32);
		return nbt;
	}
	
	@Override
	public ItemStack newItemStack(int size) {
		return super.newItemStack(size);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		super.addInformation(itemStack, entityPlayer, list, par4);
		
		list.add("Life : " + itemStack.getTagCompound().getInteger("life"));
	}
	
	public boolean use(ItemStack stack) {
		int life = stack.getTagCompound().getInteger("life");
		if(life != 0) {
			if(--life == 0)
				setColor(stack, 15);
			stack.getTagCompound().setInteger("life", life);
			return true;
		}
		return false;
	}
}
