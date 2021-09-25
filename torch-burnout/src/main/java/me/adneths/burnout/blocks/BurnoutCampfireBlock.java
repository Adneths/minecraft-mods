package me.adneths.burnout.blocks;

import java.util.Optional;

import me.adneths.burnout.init.ModTileEntity;
import me.adneths.burnout.tile.BurnoutCampfireTile;
import me.adneths.burnout.util.FuelDurations;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BurnoutCampfireBlock extends CampfireBlock implements BurnoutForgeBlock {

	private String translationKey;
	
	public BurnoutCampfireBlock(boolean smokey, int fireDamage, Properties properties) {
		super(smokey, fireDamage, properties);
		this.setDefaultState(this.getDefaultState().with(BlockStateProperties.LIT, false));
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return ModTileEntity.CAMPFIRE.create();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		ActionResultType ret = this.universalOnBlockActivated(state, worldIn, pos, player, handIn, hit);
		if(ret==ActionResultType.PASS)
		{
		    TileEntity tileentity = worldIn.getTileEntity(pos);
		    if (tileentity instanceof BurnoutCampfireTile) {
		    	BurnoutCampfireTile campfiretileentity = (BurnoutCampfireTile)tileentity;
		    	ItemStack itemstack = player.getHeldItem(handIn);
		    	Optional<CampfireCookingRecipe> optional = campfiretileentity.findMatchingRecipe(itemstack);
		    	if (optional.isPresent()) {
		    		if (!worldIn.isRemote && campfiretileentity.addItem(player.abilities.isCreativeMode ? itemstack.copy() : itemstack, ((CampfireCookingRecipe)optional.get()).getCookTime())) {
		    			player.addStat(Stats.INTERACT_WITH_CAMPFIRE);
		    			worldIn.notifyBlockUpdate(pos, state, state, 2);
		    			return ActionResultType.SUCCESS;
		    		}
		    		return ActionResultType.CONSUME;
		    	} 
		    }
		    return ActionResultType.PASS;
		}
		else
			worldIn.setBlockState(pos, state.getBlockState().with(LIT, false));
		return ret;
	}
	
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
	    if (!state.isIn(newState.getBlock())) {
	      TileEntity tileentity = worldIn.getTileEntity(pos);
	      if (tileentity instanceof BurnoutCampfireTile)
	        InventoryHelper.dropItems(worldIn, pos, ((BurnoutCampfireTile)tileentity).getInventory()); 
	      super.onReplaced(state, worldIn, pos, newState, isMoving);
	    } 
	}
	
	@Override
	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
	}

	@Override
	public int maxFuel() {
		return FuelDurations.Max.CAMPFIRE;
	}

	@Override
	public int refillAmount() {
		return FuelDurations.Refill.CAMPFIRE;
	}

	@Override
	public boolean isRefillItem(Item item) {
		return item.equals(Items.COAL)||item.equals(Items.CHARCOAL);
	}
	
	@Override
	public String getTranslationKey()
	{
		return this.translationKey;
	}

	@Override
	public Block setTranslationKey(String namespace, String key) {
		this.translationKey = namespace+":"+key;
		return this;
	}

}
