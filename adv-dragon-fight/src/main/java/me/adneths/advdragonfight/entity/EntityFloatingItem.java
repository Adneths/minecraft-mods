package me.adneths.advdragonfight.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFloatingItem extends EntityItem
{

	public EntityFloatingItem(World worldIn)
	{
		super(worldIn);
		this.setSize(1, 1);
		this.setNoGravity(true);
	}

	public EntityFloatingItem(World worldIn, double x, double y, double z)
	{
		super(worldIn, x, y, z);
		this.setSize(1, 1);
		this.setNoGravity(true);
	}

	public EntityFloatingItem(World worldIn, double x, double y, double z, ItemStack stack)
	{
		super(worldIn, x, y, z, stack);
		this.setSize(1, 1);
		this.setNoGravity(true);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.motionX = 0;
		BlockPos pos = this.getPosition().down().down();
		this.motionY = this.world.getBlockState(pos).isSideSolid(this.world, pos, EnumFacing.UP) ? 0 : -0.04f;
		this.motionZ = 0;
	}

}
