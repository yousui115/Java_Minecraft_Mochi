package yousui115.mochi.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.mochi.entity.EntityMochi;
import yousui115.mochi.util.MItems;

public class ItemMochi extends ItemFood
{
    //■たいぷ
    public static final TypeMochi typeMochi[] = TypeMochi.values();

    /**
     * ■こんすとらくた
     */
    public ItemMochi(int amount, float saturation, boolean isWolfFood)
    {
        super(amount, saturation, false);
    }

    /**
     * ■クリエイティブモードの取り出し
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!this.isInCreativeTab(tab)) { return; }

        for (TypeMochi mochi : typeMochi)
        {
            items.add(new ItemStack(this, 1, mochi.ordinal()));
        }
    }


    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int meta = MathHelper.clamp(stack.getMetadata(), 0, typeMochi.length - 1);
        return super.getUnlocalizedName(stack) + "." + typeMochi[meta].itemName;
    }


    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        int posY = pos.getY() + 1;
        Block block = worldIn.getBlockState(pos).getBlock();

        //■上面クリックのみ有効
        if (facing != EnumFacing.UP) { return EnumActionResult.FAIL; }

        //■中腰のみ
        if (player.isSneaking() == false) { return EnumActionResult.FAIL; }

        //■メインハンドのみ
        if (hand != EnumHand.MAIN_HAND) { return EnumActionResult.FAIL; }

        //■焼いてない餅のみ
        ItemStack stack = player.getHeldItem(hand);
        if (stack == null ||
            stack.getItem() != MItems.MOCHI ||
            stack.getMetadata() != TypeMochi.RAW.ordinal())
        {
            return EnumActionResult.FAIL;
        }

        //■Entity生成
        EntityMochi mochi = new EntityMochi(worldIn, pos.getX(), posY, pos.getZ(), hitX, hitZ);

        //■生存可能条件の確認
        if(mochi.canStay() == false) { return EnumActionResult.FAIL; }

        if(!worldIn.isRemote)
        {
            //■顕現
            worldIn.spawnEntity(mochi);
        }

        //■プレイヤーが持ってるItemMochiを一つ減らす。
        stack.shrink(1);

        return EnumActionResult.SUCCESS;
    }

    @Override
    public int getHealAmount(ItemStack stack)
    {
        return super.getHealAmount(stack) * (stack.getMetadata() == TypeMochi.RAW.ordinal() ? 1 : 2);
    }

    @Override
    public float getSaturationModifier(ItemStack stack)
    {
        return super.getSaturationModifier(stack) * (stack.getMetadata() == TypeMochi.RAW.ordinal() ? 1 : 2);
    }


    //================================================================


    /**
     * ■餅の種類
     */
    public enum TypeMochi
    {
        RAW("raw", "mochi_raw"),
        BAKED("baked", "mochi_baked");

        public String itemName;
        public String modelName;

        private TypeMochi(String itemNameIn, String modelNameIn)
        {
            itemName = itemNameIn;
            modelName = modelNameIn;
        }
    }
}
