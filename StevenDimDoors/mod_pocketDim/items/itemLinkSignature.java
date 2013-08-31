package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class itemLinkSignature extends Item
{

	public itemLinkSignature(int itemID)
	{
		super(itemID);
		this.setMaxStackSize(1);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		this.setMaxDamage(0);
		this.hasSubtypes = true;
		if (properties == null)
			properties = DDProperties.instance();
	}

	private static DDProperties properties = null;

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack par1ItemStack)
	{
		// adds effect if item has a link stored


		if(par1ItemStack.hasTagCompound())
		{
			if(par1ItemStack.stackTagCompound.getBoolean("isCreated"))
			{
				return true;
			}
		}
		return false;
	}


	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());

	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		int key;
		ILinkData linkData;
		int thisWorldID=par3World.provider.dimensionId;




		if(!par3World.isRemote)
		{		

			//par1ItemStack= par2EntityPlayer.getCurrentEquippedItem();
			Integer[] linkCoords =this.readFromNBT(par1ItemStack);



			//System.out.println(key);
			int offset = 2;
			int idBlock = par3World.getBlockId(par4, par5, par6);

			if(Block.blocksList.length>idBlock&&idBlock!=0)
			{
				if(Block.blocksList[idBlock].isBlockReplaceable(par3World, par4, par5, par6))
				{
					offset = 1;
				}
			}
			if(par3World.getBlockId(par4, par5, par6) == properties.DimensionalDoorID && par3World.getBlockId(par4, par5 + 1, par6) == properties.DimensionalDoorID)
			{
				offset = 1;
			}
			else
				if(par3World.getBlockId(par4, par5, par6)==properties.WarpDoorID&&par3World.getBlockId(par4, par5+1, par6)==properties.WarpDoorID)
				{
					offset = 1;
				}
				else
					if (par3World.getBlockId(par4, par5, par6)==properties.DimensionalDoorID&&par3World.getBlockId(par4, par5-1, par6)==properties.DimensionalDoorID)
					{
						offset = 0;
					}
					else
						if (par3World.getBlockId(par4, par5, par6) == properties.WarpDoorID && par3World.getBlockId(par4, par5-1, par6)==properties.WarpDoorID)
						{
							offset = 0;
						}

			int orientation = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;

			for(int count = 0;count<3;count++)
			{
				if(PocketManager.instance.getLinkDataFromCoords(par4, par5+count, par6,par3World)!=null)
				{
					int id= (par3World.getBlockId(par4, par5+count, par6));

					if(id == properties.DimensionalDoorID||id==properties.WarpDoorID||id== properties.UnstableDoorID)
					{
						orientation = PocketManager.instance.getLinkDataFromCoords(par4, par5+count, par6,par3World).linkOrientation;
					}
				}


			}

			if(par1ItemStack.getTagCompound()!=null)
			{
				if(par1ItemStack.getTagCompound().getBoolean("isCreated"))
				{
					// checks to see if the item has a link stored, if so, it creates it



					PocketManager.instance.createLink(par3World.provider.dimensionId, linkCoords[3], par4, par5+offset, par6, linkCoords[0], linkCoords[1], linkCoords[2],orientation);		
					PocketManager.instance.createLink(linkCoords[3], par3World.provider.dimensionId, linkCoords[0], linkCoords[1], linkCoords[2],par4, par5+offset, par6,linkCoords[4]);	



					--par1ItemStack.stackSize;
					par2EntityPlayer.sendChatToPlayer("Rift Created");
					par1ItemStack.stackTagCompound=null;
					par2EntityPlayer.worldObj.playSoundAtEntity(par2EntityPlayer,"mods.DimDoors.sfx.riftEnd", (float) .6, 1);
				}
			}
			else 
			{


				//otherwise, it creates the first half of the link. Next click will complete it. 
				key= PocketManager.instance.createUniqueInterDimLinkKey();
				this.writeToNBT(par1ItemStack, par4, par5+offset, par6,par3World.provider.dimensionId,orientation);
				par2EntityPlayer.sendChatToPlayer("Rift Signature Stored");
				par2EntityPlayer.worldObj.playSoundAtEntity(par2EntityPlayer,"mods.DimDoors.sfx.riftStart", (float) .6, 1);
			}	
			//dimHelper.instance.save();
		}


		return true;


	}

	@SideOnly(Side.CLIENT)

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{

		if(par1ItemStack.hasTagCompound())
		{
			if(par1ItemStack.stackTagCompound.getBoolean("isCreated"))
			{
				Integer[] coords = this.readFromNBT(par1ItemStack);

				par3List.add(String.valueOf("Leads to dim "+coords[3] +" with depth "+(PocketManager.instance.getDimDepth(coords[3]))));
				par3List.add("at x="+coords[0]+" y="+coords[1]+" z="+coords[2]);

			}

		}
		else
		{
			par3List.add("First click stores location,");
			par3List.add ("second click creates two rifts,");
			par3List.add("that link the first location");
			par3List.add("with the second location");


		}
	}

	public void writeToNBT(ItemStack itemStack,int x, int y, int z, int dimID,int orientation)
	{
		NBTTagCompound tag;

		if(itemStack.hasTagCompound())
		{
			tag = itemStack.getTagCompound();

		}
		else
		{
			tag= new NBTTagCompound();
		}

		tag.setInteger("linkX", x);
		tag.setInteger("linkY", y);
		tag.setInteger("linkZ", z);
		tag.setInteger("linkDimID", dimID);
		tag.setBoolean("isCreated", true);
		tag.setInteger("orientation", orientation);

		itemStack.setTagCompound(tag);

	}

	/**
	 * Read the stack fields from a NBT object.
	 */
	public Integer[] readFromNBT(ItemStack itemStack)
	{

		NBTTagCompound tag;
		Integer[] linkCoords = new Integer[5];
		if(itemStack.hasTagCompound())
		{
			tag = itemStack.getTagCompound();

			if(!tag.getBoolean("isCreated"))
			{
				return null;
			}
			linkCoords[0]=tag.getInteger("linkX");
			linkCoords[1]=tag.getInteger("linkY");
			linkCoords[2]=tag.getInteger("linkZ");
			linkCoords[3]=tag.getInteger("linkDimID");
			linkCoords[4]=tag.getInteger("orientation");



		}
		return linkCoords;

	}


	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) 
	{
		if(!par2World.isRemote)
		{
			/**
    		//creates the first half of the link on item creation
    		int key= dimHelper.instance.createUniqueInterDimLinkKey();
    		LinkData linkData= new LinkData(par2World.provider.dimensionId,MathHelper.floor_double(par3EntityPlayer.posX),MathHelper.floor_double(par3EntityPlayer.posY),MathHelper.floor_double(par3EntityPlayer.posZ));
    		System.out.println(key);

    		dimHelper.instance.interDimLinkList.put(key, linkData);
    		par1ItemStack.setItemDamage(key);
			 **/
		}
	}
}
