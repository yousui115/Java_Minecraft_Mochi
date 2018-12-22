package yousui115.mochi.proxy;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.mochi.client.render.RenderMochi;
import yousui115.mochi.entity.EntityMochi;
import yousui115.mochi.util.MItems;


@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    /**
     * ■モデルの登録
     */
    @Override
    public void registerItemModel()
    {
        //■アイテムのモデル　登録
        MItems.registerModel();
    }


    /**
     * ■Entity <-> Renderer 関連付け
     */
    @Override
    public void registerRenderer()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityMochi.class, new RenderMochi.Factory());
    }
}
