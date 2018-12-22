package yousui115.mochi;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import yousui115.mochi.entity.EntityMochi;
import yousui115.mochi.util.MItems;

@EventBusSubscriber
public class Register
{
    /**
     * ■エンティティの登録
     * @param event
     */
    @SubscribeEvent
    public static void registerEntity(final RegistryEvent.Register<EntityEntry> event)
    {
        //■餅
        event.getRegistry().register(
                EntityEntryBuilder.create()
                    .entity(EntityMochi.class)
                    .id(new ResourceLocation(Mochi.MOD_ID, "mochi"), 1)
                    .name("mochi")
                    .tracker(160, 5, false)
//                    .egg(0xffffff, 0xffffff)
//                    .spawn(EnumCreatureType.MONSTER, 10, 1, 1, ForgeRegistries.BIOMES.getValues())
                    .build()
            );

    }

    /**
     * ■アイテムの登録
     * @param event
     */
    @SubscribeEvent
    protected static void registerItem(RegistryEvent.Register<Item> event)
    {
        //■アイテムの生成と登録
        MItems.create();
        MItems.register(event);
    }

    /**
     * ■モデルの登録
     * @param event
     */
    @SubscribeEvent
    public static void registerItemModel(ModelRegistryEvent event)
    {
        Mochi.proxy.registerItemModel();
    }
}
