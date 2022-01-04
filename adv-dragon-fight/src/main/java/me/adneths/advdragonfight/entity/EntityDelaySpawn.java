package me.adneths.advdragonfight.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityDelaySpawn extends Entity
{

	private int maxTicks = 20;
	private int ticks;
	private Entity toSpawn;
	private List<Particle> particles;

	public EntityDelaySpawn(World worldIn)
	{
		super(worldIn);
		this.setSize(0.01f, 0.01f);
		this.setNoGravity(true);
		particles = new ArrayList<Particle>();
		
		this.maxTicks = 150;
		if(worldIn.isRemote)
		{
			this.addParticle(EnumParticleTypes.ENCHANTMENT_TABLE, 0, 0, 0, 1, 0.5, 0.5, 0.5);
			this.addParticle(EnumParticleTypes.ENCHANTMENT_TABLE, 0, 0, 0, 1, 0.5, 0.5, 0.5);
			this.addParticle(EnumParticleTypes.DRAGON_BREATH, 0, 0, 0, 2, 0.2, 0.2, 0.2);
		}
	}
	
	public EntityDelaySpawn(World worldIn, int ticks, Entity toSpawn)
	{
		this(worldIn);
		this.maxTicks = ticks;
		this.toSpawn = toSpawn;
	}
	
	public void addParticle(EnumParticleTypes type, double xSpeed,
			double ySpeed, double zSpeed, double radius,
			double xSpeedRange, double ySpeedRange, double zSpeedRange)
	{
		this.particles.add(new Particle(this, type, xSpeed, ySpeed, zSpeed, radius, xSpeedRange, ySpeedRange, zSpeedRange));
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if(this.world.isRemote)
		{
			for (Particle p : particles)
				for(int i = 0; i < 25*ticks/maxTicks + 1; i++)
					p.spawnParticle(this.world, this.posX, this.posY, this.posZ);
		}
		if (ticks++ > maxTicks)
		{
			if(toSpawn != null)
				this.world.spawnEntity(toSpawn);
			this.setDead();
		}
	}

	@Override
	protected void entityInit()
	{
		
	}

	class Particle
	{
		private static final float PI = (float) Math.PI;
		
		EntityDelaySpawn entity;
		EnumParticleTypes type;
		double vx, vy, vz, vxr, vyr, vzr;
		double radius;

		protected Particle(EntityDelaySpawn e, EnumParticleTypes type, double vx,
				double vy, double vz, double radius, double vxr, double vyr, double vzr)
		{
			entity = e;
			this.type = type;
			this.vx = vx;
			this.vy = vy;
			this.vz = vz;
			this.vxr = vxr;
			this.vyr = vyr;
			this.vzr = vzr;
			this.radius = radius;
		}

		protected void spawnParticle(World worldIn, double x, double y, double z)
		{
			float theta = entity.rand.nextFloat()*2*PI;
			float phi = entity.rand.nextFloat()*2*PI;
			double yr = MathHelper.sin(phi)*radius;
			double xr = MathHelper.cos(theta)*MathHelper.cos(phi)*radius;
			double zr = MathHelper.sin(theta)*MathHelper.cos(phi)*radius;
			worldIn.spawnParticle(type, x + xr, y + yr, z + zr,
					vx + (entity.rand.nextDouble()-0.5) * vxr,
					vy + (entity.rand.nextDouble()-0.5) * vyr, 
					vz + (entity.rand.nextDouble()-0.5) * vzr);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
		
	}

}
