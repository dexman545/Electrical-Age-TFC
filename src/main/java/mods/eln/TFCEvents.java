package mods.eln;

import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.Constant.Global;
import com.bioxx.tfc.api.Crafting.AnvilManager;
import com.bioxx.tfc.api.Crafting.AnvilRecipe;
import com.bioxx.tfc.api.Crafting.AnvilReq;
import com.bioxx.tfc.api.Crafting.PlanRecipe;
import com.bioxx.tfc.api.Enums.RuleEnum;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

public class TFCEvents {
	int searchDist = 10;
	boolean HasLoadedPlans;
	
	
	public boolean IsATreeBlock(Block block) {
		return block == TFCBlocks.logNatural || block == TFCBlocks.logNatural2;
	}

	@SubscribeEvent
	public void OnBlockHarvest(BlockEvent.BreakEvent event) {
		if (IsATreeBlock(event.block) && event.getPlayer() != null && event.getPlayer().getHeldItem() != null) {
			Item helditem = event.getPlayer().getHeldItem().getItem();
			Item axe = Eln.findItemStack("Portable Electrical Axe", 1)
					.getItem();

			if (helditem == axe) {
				event.setCanceled(true);
				BreakTFCLog(event);
			}
		}
	}

	public void BreakTFCLog(BlockEvent.BreakEvent event) {
		World world = event.world;
		EntityPlayer player = event.getPlayer();
		Block block = event.block;
		int blockx = event.x;
		int blocky = event.y;
		int blockz = event.z;
		int blockmeta = event.blockMetadata;

		ItemStack stack = player.getCurrentEquippedItem();

		boolean[][][] checkArray = new boolean[searchDist * 2 + 1][256][searchDist * 2 + 1];
		scanLogs(world, blockx, blocky, blockz, blockmeta, checkArray,
				(byte) 0, (byte) 0, (byte) 0, stack, player);

		Item axe = Eln.findItemStack("Portable Electrical Axe", 1).getItem();
		if (!axe.onBlockDestroyed(stack, world, block, blockx, blocky, blockz,
				player))
			world.setBlock(blockx, blocky, blockz, block, blockmeta, 0x2);
	}

	// From minecraft block code
	protected void dropBlockAsItem(World world, int x, int y, int z,
			ItemStack is) {
		if (!world.isRemote
				&& world.getGameRules().getGameRuleBooleanValue("doTileDrops")
				&& !world.restoringBlockSnapshots) // do not drop items while
													// restoring blockstates,
													// prevents item dupe
		{
			float f = 0.7F;
			double d0 = (double) (world.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			double d1 = (double) (world.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			double d2 = (double) (world.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(world, (double) x + d0,
					(double) y + d1, (double) z + d2, is);
			entityitem.delayBeforeCanPickup = 10;
			world.spawnEntityInWorld(entityitem);
		}
	}

	// From TFC's block
	private void scanLogs(World world, int i, int j, int k, int l,
			boolean[][][] checkArray, byte x, byte y, byte z, ItemStack stack,
			EntityPlayer player) {
		Block block = world.getBlock(i, j, k);
		if (y >= 0 && j + y < 256) {
			int offsetX = 0;
			int offsetY = 0;
			int offsetZ = 0;
			checkArray[x + searchDist][y][z + searchDist] = true;

			for (offsetX = -3; offsetX <= 3; offsetX++) {
				for (offsetZ = -3; offsetZ <= 3; offsetZ++) {
					for (offsetY = 0; offsetY <= 2; offsetY++) {
						if (Math.abs(x + offsetX) <= searchDist
								&& j + y + offsetY < 256
								&& Math.abs(z + offsetZ) <= searchDist) {
							if (checkOut(world, i + x + offsetX, j + y
									+ offsetY, k + z + offsetZ, l)
									&& !(offsetX == 0 && offsetY == 0 && offsetZ == 0)
									&& !checkArray[x + offsetX + searchDist][y
											+ offsetY][z + offsetZ + searchDist])
								scanLogs(world, i, j, k, l, checkArray,
										(byte) (x + offsetX),
										(byte) (y + offsetY),
										(byte) (z + offsetZ), stack, player);
						}
					}
				}
			}

			if (stack != null) {
				Item axe = Eln.findItemStack("Portable Electrical Axe", 1)
						.getItem();
				if (axe.onBlockDestroyed(stack, world, block, i, j, k, player)) {
					world.setBlock(i + x, j + y, k + z, Blocks.air, 0, 0x2);
					dropBlockAsItem(world, i + x, j + y, k + z, new ItemStack(
							TFCItems.logs, 1, block.damageDropped(l)));
					notifyLeaves(world, i + x, j + y, k + z);
				}
			} else {
				world.setBlockToAir(i, j, k);
				dropBlockAsItem(world, i, j, k, new ItemStack(TFCItems.logs, 1,
						block.damageDropped(l)));
				notifyLeaves(world, i + x, j + y, k + z);
			}
		}
	}

	private boolean checkOut(World world, int x, int y, int z, int meta) {
		if (IsATreeBlock(world.getBlock(x, y, z))
				&& world.getBlockMetadata(x, y, z) == meta)
			return true;
		return false;
	}

	private void notifyLeaves(World world, int x, int y, int z) {
		world.notifyBlockOfNeighborChange(x + 1, y, z, Blocks.air);
		world.notifyBlockOfNeighborChange(x - 1, y, z, Blocks.air);
		world.notifyBlockOfNeighborChange(x, y, z + 1, Blocks.air);
		world.notifyBlockOfNeighborChange(x, y, z - 1, Blocks.air);
		world.notifyBlockOfNeighborChange(x, y + 1, z, Blocks.air);
		world.notifyBlockOfNeighborChange(x, y - 1, z, Blocks.air);
	}
}
