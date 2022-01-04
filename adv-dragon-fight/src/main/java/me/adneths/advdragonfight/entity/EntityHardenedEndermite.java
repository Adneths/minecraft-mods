package me.adneths.advdragonfight.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityHardenedEndermite extends EntityEndermite
{

	public EntityHardenedEndermite(World worldIn)
	{
		super(worldIn);
	}
	
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.75D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }
	
	public boolean isEntityInvulnerable(DamageSource damage)
	{
		return super.isEntityInvulnerable(damage) || damage.isExplosion() || damage == DamageSource.FALL || damage == DamageSource.DRAGON_BREATH || damage == DamageSource.IN_FIRE|| damage == DamageSource.ON_FIRE;
	}

}
