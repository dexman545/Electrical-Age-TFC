package mods.eln.node.simple;

import mods.eln.misc.Coordonate;
import mods.eln.misc.DescriptorBase;
import mods.eln.misc.Direction;
import mods.eln.misc.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class SimpleNodeBlock extends BlockContainer {



	protected SimpleNodeBlock(Material material) {
		super(material);
	}

	String descriptorKey;
	public SimpleNodeBlock setDescriptorKey(String descriptorKey) {
		this.descriptorKey = descriptorKey;
		return this;
	}
	
	public SimpleNodeBlock setDescriptor(DescriptorBase descriptor) {
		this.descriptorKey = descriptor.descriptorKey;
		return this;
	}

	
	Direction getFrontForPlacement(EntityLivingBase e){
		return Utils.entityLivingViewDirection(e).getInverse();
	}

	/*@Override
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase e, ItemStack stack) {
		if(w.isRemote == false){
			SimpleNode node = newNode();
			node.setDescriptorKey(descriptorKey);
			node.onBlockPlacedBy(new Coordonate(x,y,z,w), getFrontForPlacement(e), e, stack);
		}
	}*/
	
	protected abstract SimpleNode newNode();


	SimpleNode getNode(World world, int x, int y, int z) {
		SimpleNodeEntity entity = (SimpleNodeEntity) world.getTileEntity(x, y, z);
		if (entity != null) {
			return entity.getNode();
		}
		return null;
	}

	public SimpleNodeEntity getEntity(World world, int x, int y, int z) {
		SimpleNodeEntity entity = (SimpleNodeEntity) world.getTileEntity(x, y, z);
		return entity;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer entityPlayer, int x, int y, int z)
	{
		if (!world.isRemote) {
			SimpleNode node = getNode(world, x, y, z);
			if (node != null) {
				node.removedByPlayer = (EntityPlayerMP) entityPlayer;
			}
		}
		return super.removedByPlayer(world, entityPlayer, x, y, z);

	}

	// client server
	/*onblockplaced
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, Direction front, EntityLivingBase entityLiving, int metadata)
	{
		SimpleNodeEntity tileEntity = (SimpleNodeEntity) world.getTileEntity(x, y, z);
		tileEntity.onBlockPlacedBy(front, entityLiving, metadata);
	}*/

	// server
	@Override
	public void onBlockAdded(World par1World, int x, int y, int z)
	{
		if (par1World.isRemote == false)
		{
			SimpleNodeEntity entity = (SimpleNodeEntity) par1World.getTileEntity(x, y, z);
			entity.onBlockAdded();
		}
	}

	// server
	@Override
	public void breakBlock(World par1World, int x, int y, int z, Block par5, int par6)
	{
		SimpleNodeEntity entity = (SimpleNodeEntity) par1World.getTileEntity(x, y, z);
		entity.onBreakBlock();
		super.breakBlock(par1World, x, y, z, par5, par6);

	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		if (Utils.isRemote(world) == false)
		{
			SimpleNodeEntity entity = (SimpleNodeEntity) world.getTileEntity(x, y, z);
			entity.onNeighborBlockChange();
		}
	}

	// client server
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float vx, float vy, float vz)
	{
		SimpleNodeEntity entity = (SimpleNodeEntity) world.getTileEntity(x, y, z);
		return entity.onBlockActivated(entityPlayer, Direction.fromIntMinecraftSide(side), vx, vy, vz);
	}

}
