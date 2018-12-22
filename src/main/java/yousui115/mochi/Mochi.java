package yousui115.mochi;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import yousui115.mochi.proxy.CommonProxy;

@Mod(modid = Mochi.MOD_ID, name = Mochi.MOD_NAME, version = Mochi.MOD_VERSION, useMetadata = true)
public class Mochi
{
    public static final String MOD_ID = "yousui115.mochi";
    public static final String MOD_NAME = "Mochi";
    public static final String MOD_VERSION = "M1122_F2768_v1";

    //■インスタント
    @Mod.Instance(MOD_ID)
    public static Mochi INSTANCE;

    //■クライアント側とサーバー側で異なるインスタンスを生成
    @SidedProxy(clientSide = MOD_ID + ".proxy.ClientProxy", serverSide = MOD_ID + ".proxy.CommonProxy")
    public static CommonProxy proxy;

    //■ろがー
    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        //■Entity <-> Render 関連付け
        proxy.registerRenderer();

        //TODO:Fix
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
}
