package yousui115.mochi.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import objmodel.AdvancedModelLoader;
import objmodel.IModelCustom;
import yousui115.mochi.Mochi;
import yousui115.mochi.entity.EntityMochi;
import yousui115.mochi.util.MUtils;

@SideOnly(Side.CLIENT)
public class RenderMochi extends Render<EntityMochi>
{
    private static ResourceLocation texture = new ResourceLocation(Mochi.MOD_ID, "textures/model/mochi_tex.png");
    private static final IModelCustom OBJ_MOCHI_RAW = AdvancedModelLoader.loadModel(new ResourceLocation(Mochi.MOD_ID, "textures/model/mochi_raw.obj"));
    private static final IModelCustom OBJ_MOCHI_BAKED = AdvancedModelLoader.loadModel(new ResourceLocation(Mochi.MOD_ID, "textures/model/mochi_baked.obj"));
    private static final IModelCustom OBJ_MOCHI_BALLOON1 = AdvancedModelLoader.loadModel(new ResourceLocation(Mochi.MOD_ID, "textures/model/mochi_balloon1.obj"));
    private static final IModelCustom OBJ_MOCHI_BALLOON2 = AdvancedModelLoader.loadModel(new ResourceLocation(Mochi.MOD_ID, "textures/model/mochi_balloon2.obj"));

    public RenderMochi(RenderManager renderManager)
    {
        super(renderManager);
    }

    /**
     * ■描画処理
     */
    @Override
    public void doRender(EntityMochi mochiIn, double xIn, double yIn, double zIn, float yawIn, float partialTicksIn)
    {
//        GL11.glPushMatrix();
        GlStateManager.pushMatrix();

//        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GlStateManager.enableRescaleNormal();

        //移動
        GlStateManager.translate(xIn, yIn, zIn);



        float pY = mochiIn.stateM.getOffsetPosY();
        GlStateManager.translate(0f, pY, 0f);

        float yaw = mochiIn.rotationYaw;
        GlStateManager.rotate(yawIn, 0f, 1f, 0f);

        float rX = mochiIn.stateM.getOffsetRotX();
        GlStateManager.rotate(rX, 1f, 0f, 0f);



        //縮小
        float sc = mochiIn.stateM.getScale();
        GlStateManager.scale(sc, sc, sc);

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

//        OBJ_MOCHI_RAW.renderAll();
        float rate = (float)mochiIn.stateM.rate();
        IModelCustom before = OBJ_MOCHI_RAW;
        IModelCustom after = OBJ_MOCHI_BAKED;
        switch (mochiIn.stateM.getType())
        {
            case ROT:
                before = OBJ_MOCHI_BAKED;
                after = OBJ_MOCHI_BAKED;
                break;

            case BALLOON1:
                before = OBJ_MOCHI_BAKED;
                after = OBJ_MOCHI_BALLOON1;
                break;

            case STAY:
                before = OBJ_MOCHI_BALLOON1;
                after = OBJ_MOCHI_BALLOON1;
                break;

            case BALLOON2:
                before = OBJ_MOCHI_BALLOON1;
                after = OBJ_MOCHI_BALLOON2;
                break;

            case BALLOON3:
                before = OBJ_MOCHI_BALLOON2;
                after = OBJ_MOCHI_BALLOON1;
                break;

            case RAW:
            default:
                //そのまま
                break;
        }
        MUtils.drawOBJ(before, after, rate);

//        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GlStateManager.disableRescaleNormal();

//        GL11.glPopMatrix();
        GlStateManager.popMatrix();

    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMochi entity)
    {
        return null;
    }


    //============================================================


    /**
     * ■Factory
     */
    @SideOnly(Side.CLIENT)
    public static class Factory implements IRenderFactory<EntityMochi>
    {
        @Override
        public Render<? super EntityMochi> createRenderFor(RenderManager manager)
        {
            return new RenderMochi(manager);
        }
    }
}
