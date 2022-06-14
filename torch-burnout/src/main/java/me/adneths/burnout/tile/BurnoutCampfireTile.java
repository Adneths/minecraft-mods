package me.adneths.burnout.tile;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import me.adneths.burnout.init.ModTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BurnoutCampfireTile extends BurnoutableTile implements IClearable {

	private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
	 
	private final int[] cookingTimes = new int[4];
	 
	private final int[] cookingTotalTimes = new int[4];
	
	public BurnoutCampfireTile()
	{
		super(ModTileEntity.CAMPFIRE);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		boolean flag = (getBlockState().get(CampfireBlock.LIT)).booleanValue();
		boolean flag1 = this.world.isRemote;
		if (flag1) {
			if (flag)
				addParticles(); 
		} else if (flag) {
			cookAndDrop();
		} else {
			for (int i = 0; i < this.inventory.size(); i++) {
				if (this.cookingTimes[i] > 0)
					this.cookingTimes[i] = MathHelper.clamp(this.cookingTimes[i] - 2, 0, this.cookingTotalTimes[i]); 
			} 
		} 
	}
	
	private void cookAndDrop() {
	    for (int i = 0; i < this.inventory.size(); i++) {
	      ItemStack itemstack = (ItemStack)this.inventory.get(i);
	      if (!itemstack.isEmpty()) {
	        this.cookingTimes[i] = this.cookingTimes[i] + 1;
	        if (this.cookingTimes[i] >= this.cookingTotalTimes[i]) {
	          Inventory inventory = new Inventory(new ItemStack[] { itemstack });
	          ItemStack itemstack1 = this.world.getRecipeManager().getRecipe(IRecipeType.CAMPFIRE_COOKING, inventory, this.world).map((campfireRecipe) -> {
	              return campfireRecipe.getCraftingResult(inventory);
	          	}).orElse(itemstack);
	          BlockPos blockpos = getPos();
	          InventoryHelper.spawnItemStack(this.world, blockpos.getX(), blockpos.getY(), blockpos.getZ(), itemstack1);
	          this.inventory.set(i, ItemStack.EMPTY);
	          inventoryChanged();
	        } 
	      } 
	    } 
	  }
	  
	  private void addParticles() {
	    World world = getWorld();
	    if (world != null) {
	      BlockPos blockpos = getPos();
	      Random random = world.rand;
	      if (random.nextFloat() < 0.11F)
	        for (int i = 0; i < random.nextInt(2) + 2; i++)
	          CampfireBlock.spawnSmokeParticles(world, blockpos, (getBlockState().get(CampfireBlock.SIGNAL_FIRE)).booleanValue(), false);  
	      int l = (getBlockState().get(CampfireBlock.FACING)).getHorizontalIndex();
	      for (int j = 0; j < this.inventory.size(); j++) {
	        if (!((ItemStack)this.inventory.get(j)).isEmpty() && random.nextFloat() < 0.2F) {
	          Direction direction = Direction.byHorizontalIndex(Math.floorMod(j + l, 4));
	          double d0 = blockpos.getX() + 0.5D - (direction.getXOffset() * 0.3125F) + (direction.rotateY().getXOffset() * 0.3125F);
	          double d1 = blockpos.getY() + 0.5D;
	          double d2 = blockpos.getZ() + 0.5D - (direction.getZOffset() * 0.3125F) + (direction.rotateY().getZOffset() * 0.3125F);
	          for (int k = 0; k < 4; k++)
	            world.addParticle((IParticleData)ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 5.0E-4D, 0.0D); 
	        } 
	      } 
	    } 
	  }
	  
	  public NonNullList<ItemStack> getInventory() {
	    return this.inventory;
	  }
	  
	  public void read(BlockState state, CompoundNBT nbt) {
	    super.read(state, nbt);
	    this.inventory.clear();
	    ItemStackHelper.loadAllItems(nbt, this.inventory);
	    if (nbt.contains("CookingTimes", 11)) {
	      int[] aint = nbt.getIntArray("CookingTimes");
	      System.arraycopy(aint, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, aint.length));
	    } 
	    if (nbt.contains("CookingTotalTimes", 11)) {
	      int[] aint1 = nbt.getIntArray("CookingTotalTimes");
	      System.arraycopy(aint1, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, aint1.length));
	    } 
	  }
	  
	  public CompoundNBT write(CompoundNBT compound) {
	    writeItems(compound);
	    compound.putIntArray("CookingTimes", this.cookingTimes);
	    compound.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
	    return compound;
	  }
	  
	  private CompoundNBT writeItems(CompoundNBT compound) {
	    super.write(compound);
	    ItemStackHelper.saveAllItems(compound, this.inventory, true);
	    return compound;
	  }
	  
	  @Nullable
	  public SUpdateTileEntityPacket getUpdatePacket() {
	    return new SUpdateTileEntityPacket(this.pos, 13, getUpdateTag());
	  }
	  
	  public CompoundNBT getUpdateTag() {
	    return writeItems(new CompoundNBT());
	  }
	  
	  @Override
	  public void handleUpdateTag(BlockState state, CompoundNBT nbt)
	  {
		  super.handleUpdateTag(state, nbt);
		  this.inventory.clear();
		  ItemStackHelper.loadAllItems(nbt, this.inventory);
	  }
	  
	  @Override
	  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
	      super.onDataPacket(net, pkt);
		  CompoundNBT tag = pkt.getNbtCompound();
		  this.inventory.clear();
		  ItemStackHelper.loadAllItems(tag, this.inventory);
	  }
	  
	  public Optional<CampfireCookingRecipe> findMatchingRecipe(ItemStack itemStackIn) {
	    return this.inventory.stream().noneMatch(ItemStack::isEmpty) ? Optional.<CampfireCookingRecipe>empty() : this.world.getRecipeManager().getRecipe(IRecipeType.CAMPFIRE_COOKING, (IInventory)new Inventory(new ItemStack[] { itemStackIn } ), this.world);
	  }
	  
	  public boolean addItem(ItemStack itemStackIn, int cookTime) {
	    for (int i = 0; i < this.inventory.size(); i++) {
	      ItemStack itemstack = (ItemStack)this.inventory.get(i);
	      if (itemstack.isEmpty()) {
	        this.cookingTotalTimes[i] = cookTime;
	        this.cookingTimes[i] = 0;
	        this.inventory.set(i, itemStackIn.split(1));
	        inventoryChanged();
	        return true;
	      } 
	    } 
	    return false;
	  }
	  
	  private void inventoryChanged() {
	    markDirty();
	    getWorld().notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
	  }
	  
	  public void clear() {
	    this.inventory.clear();
	  }
	  
	  public void dropAllItems() {
	    if (this.world != null) {
	      if (!this.world.isRemote)
	        InventoryHelper.dropItems(this.world, getPos(), getInventory()); 
	      inventoryChanged();
	    } 
	  }
}
