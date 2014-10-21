package enviromine.blocks.tiles;

import enviromine.blocks.BlockFreezer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityFreezer extends TileEntity implements IInventory
{
	int tick = 0;
	int interval = 30;
	ItemStack[] items = new ItemStack[27];
	long lastCheck = -1;
	
    public float field_145972_a;
    public float field_145975_i;
    public int numPlayersUsing;
    private int field_145974_k;
	
	public TileEntityFreezer()
	{
	}
	
	/**
	 * Automatically adjust the use-by date on food items stored within the chest so they rot at half speed
	 */
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		// Chest Code
		
        if (++this.field_145974_k % 20 * 4 == 0)
        {
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, Blocks.ender_chest, 1, this.numPlayersUsing);
        }

        this.field_145975_i = this.field_145972_a;
        float f = 0.1F;
        double d1;

        if (this.numPlayersUsing > 0 && this.field_145972_a == 0.0F)
        {
            double d0 = (double)this.xCoord + 0.5D;
            d1 = (double)this.zCoord + 0.5D;
            this.worldObj.playSoundEffect(d0, (double)this.yCoord + 0.5D, d1, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.numPlayersUsing == 0 && this.field_145972_a > 0.0F || this.numPlayersUsing > 0 && this.field_145972_a < 1.0F)
        {
            float f2 = this.field_145972_a;

            if (this.numPlayersUsing > 0)
            {
                this.field_145972_a += f;
            }
            else
            {
                this.field_145972_a -= f;
            }

            if (this.field_145972_a > 1.0F)
            {
                this.field_145972_a = 1.0F;
            }

            float f1 = 0.5F;

            if (this.field_145972_a < f1 && f2 >= f1)
            {
                d1 = (double)this.xCoord + 0.5D;
                double d2 = (double)this.zCoord + 0.5D;
                this.worldObj.playSoundEffect(d1, (double)this.yCoord + 0.5D, d2, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.field_145972_a < 0.0F)
            {
                this.field_145972_a = 0.0F;
            }
        }
        
        // Esky Code
		
		if(this.getWorldObj() == null)
		{
			return;
		}
		
		if(lastCheck <= -1)
		{
			lastCheck = worldObj.getTotalWorldTime();
		}
		
		if(tick >= interval && !this.worldObj.isRemote)
		{
			tick = 0;
			
			long time = worldObj.getTotalWorldTime() - lastCheck;
			lastCheck = worldObj.getTotalWorldTime();
			
			for(int i = 0; i < this.getSizeInventory(); i++)
			{
				ItemStack stack = this.getStackInSlot(i);
				
				if(stack != null && stack.getItem() instanceof ItemFood)
				{
					if(stack.getTagCompound() == null)
					{
						stack.setTagCompound(new NBTTagCompound());
					}
					NBTTagCompound tags = stack.getTagCompound();
					
					if(tags.hasKey("EM_ROT_DATE"))
					{
						tags.setLong("EM_ROT_DATE", tags.getLong("EM_ROT_DATE") + time);
						tags.setLong("EM_ROT_TIME", tags.getLong("EM_ROT_TIME") + time);
					}
				}
			}
			
			this.markDirty();
		} else
		{
			tick++;
		}
	}

	@Override
	public int getSizeInventory()
	{
		return 27;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return items[slot];
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_)
	{
        if (this.items[p_70298_1_] != null)
        {
            ItemStack itemstack;

            if (this.items[p_70298_1_].stackSize <= p_70298_2_)
            {
                itemstack = this.items[p_70298_1_];
                this.items[p_70298_1_] = null;
                this.markDirty();
                return itemstack;
            }
            else
            {
                itemstack = this.items[p_70298_1_].splitStack(p_70298_2_);

                if (this.items[p_70298_1_].stackSize == 0)
                {
                    this.items[p_70298_1_] = null;
                }

                this.markDirty();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_)
	{
        if (this.items[p_70304_1_] != null)
        {
            ItemStack itemstack = this.items[p_70304_1_];
            this.items[p_70304_1_] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
        this.items[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
	}

	@Override
	public String getInventoryName()
	{
        return "container.freezer";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty()
	{
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

    /**
     * Called when a client event is received with the event number and argument, see World.sendClientEvent
     */
    public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_)
    {
        if (p_145842_1_ == 1)
        {
            this.numPlayersUsing = p_145842_2_;
            return true;
        }
        else
        {
            return super.receiveClientEvent(p_145842_1_, p_145842_2_);
        }
    }

	@Override
	public void openInventory()
	{
        if (this.numPlayersUsing < 0)
        {
            this.numPlayersUsing = 0;
        }

        ++this.numPlayersUsing;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
	}

	@Override
	public void closeInventory()
	{
        if (this.getBlockType() instanceof BlockFreezer)
        {
            --this.numPlayersUsing;
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
        }
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		return true;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tags)
	{
        super.readFromNBT(tags);
        
        if(tags.hasKey("RotCheck"))
        {
        	this.lastCheck = tags.getLong("RotCheck");
        } else
        {
        	this.lastCheck = -1;
        }
        
        NBTTagList nbttaglist = tags.getTagList("Items", 10);
        this.items = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.items.length)
            {
                this.items[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tags)
	{
		super.writeToNBT(tags);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.items.length; ++i)
        {
            if (this.items[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.items[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        tags.setTag("Items", nbttaglist);
        tags.setLong("RotCheck", this.lastCheck);
	}
	
}
