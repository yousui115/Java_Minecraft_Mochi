package yousui115.mochi.util;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.mochi.Mochi;
import yousui115.mochi.item.ItemMochi;

public class MItems
{
    public static Item MOCHI;

    /**
     * ■
     */
    public static void create()
    {
        //■餅
        MOCHI = (new ItemMochi(2, 0.3f, false))
                    .setRegistryName(Mochi.MOD_ID, "mochi")
                    .setUnlocalizedName("mochi")
                    .setCreativeTab(CreativeTabs.FOOD)
                    .setHasSubtypes(true);
    }

    /**
     * ■
     */
    public static void register(RegistryEvent.Register<Item> event)
    {
      //■
        event.getRegistry().registerAll(MItems.MOCHI);
    }



    /**
     * ■アイテムのモデルを登録
     */
    @SideOnly(Side.CLIENT)
    public static void registerModel()
    {
        //TODO:現状、メタ値 = Enum値

        //■餅
        ResourceLocation[] rlMochi = new ResourceLocation[ItemMochi.typeMochi.length];
        for (int idx = 0; idx < ItemMochi.typeMochi.length; idx++)
        {
            rlMochi[idx] = new ResourceLocation(Mochi.MOD_ID, ItemMochi.typeMochi[idx].modelName);
        }

        ModelBakery.registerItemVariants(MItems.MOCHI, rlMochi);

        for (int idx = 0; idx < rlMochi.length; idx++)
        {
            ModelResourceLocation mrlMochi = new ModelResourceLocation(rlMochi[idx], "inventory");
            ModelLoader.setCustomModelResourceLocation(MItems.MOCHI, idx, mrlMochi);
        }

    }
}
