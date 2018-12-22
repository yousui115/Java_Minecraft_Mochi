package yousui115.mochi.entity;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yousui115.mochi.item.ItemMochi;
import yousui115.mochi.item.ItemMochi.TypeMochi;
import yousui115.mochi.util.MItems;

public class EntityMochi extends Entity
{
    //でーたぱらめーたー
    protected static final DataParameter<Integer> TICK_COOK = EntityDataManager.<Integer>createKey(EntityMochi.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> TICK_COOK_MAX = EntityDataManager.<Integer>createKey(EntityMochi.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> STATE_COOK = EntityDataManager.<Integer>createKey(EntityMochi.class, DataSerializers.VARINT);

    //■
    public State stateM;

    /**
     * ■こんすとらくた
     * @param worldIn
     */
    public EntityMochi(World worldIn)
    {
        super(worldIn);
    }

    public EntityMochi(World worldIn, double xIn, double yIn, double zIn, float hitXIn, float hitZIn)
    {
        this(worldIn);

        xIn += (double)hitXIn;
        zIn += (double)hitZIn;

        //■位置、回転角度の調整
        setLocationAndAngles(xIn, yIn, zIn, world.rand.nextInt(180), 0.0f);
    }

    @Override
    protected void entityInit()
    {
        //■初期ステート
        stateM = TypeState.RAW.newState(this);

        //■サイズ設定
        setSize(0.25F, 0.1F);

        //■データ (ﾉ∀`)ｳｫｯﾁｬｰ
        initDataManager();

        setDataManagerLocal();
    }

    /**
     * ■データマネージャー 初期化処理
     */
    public void initDataManager()
    {
        this.dataManager.register(TICK_COOK, Integer.valueOf(0));
        this.dataManager.register(TICK_COOK_MAX, Integer.valueOf(0));
        this.dataManager.register(STATE_COOK, Integer.valueOf(0));
    }


    /**
     * ■生存可能条件の確認
     * @return
     */
    public boolean canStay()
    {
        int nX = MathHelper.floor(posX);
        int nY = MathHelper.floor(posY);
        int nZ = MathHelper.floor(posZ);

        //■接触してるEntityをリストアップしてチェック
        List<Entity> list = world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(),
                                                                new Predicate<Entity>()
                                                                {
                                                                    @Override
                                                                    public boolean apply(Entity input) {
                                                                        return input instanceof EntityMochi;
                                                                }});

        //■先着あり
        if (list.size() != 0) { return false; }

        //■設置場所にブロックがあったらfalse
        BlockPos pos = new BlockPos(nX, nY, nZ);
        Block block = world.getBlockState(pos).getBlock();
        if (Block.isEqualTo(block, Blocks.AIR) == false) { return false; }

        //■設置場所の下のブロックがマグマブロックじゃない とfalse
        pos = new BlockPos(nX, nY - 1, nZ);
        block = world.getBlockState(pos).getBlock();
        if (Block.isEqualTo(block, Blocks.MAGMA) == false) { return false; }

        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            if (!this.isDead && !this.world.isRemote)
            {
                this.setDead();
                this.markVelocityChanged();
            }

            return true;
        }
    }

    /**
     * ■メイン処理
     */
    @Override
    public void onUpdate()
    {
        super.onUpdate();

        //■クライアント かつ ジャーマネが更新されてる
        if (world.isRemote && dataManager.isDirty())
        {
            getDataManagerLocal();
        }

        //■存在できるかチェック
        if (canStay() == false)
        {
            this.setDead();
            return;
        }

        //■更新処理
        stateM.update();

        //■状態遷移チェック
        if (world.isRemote == false)
        {
            State tmp = stateM.nextState();
            boolean flag = tmp != stateM;
            stateM = tmp;
            if (flag == true)
            {
                setDataManagerLocal();
            }
        }

    }


    /**
     * ■当たり判定が仕事をするか否か
     */
    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * ■プレイヤーに右クリックされた。
     */
    @Override
    public boolean processInitialInteract(EntityPlayer playerIn, EnumHand handIn)
    {
        //■右クリックされた、という情報を受け取る
        stateM.interact();

        return true;
    }

    @Override
    public void setDead()
    {
        super.setDead();

        if (world.isRemote == false)
        {
            ItemMochi.TypeMochi type = stateM.dropMochi();
            ItemStack stack = new ItemStack(MItems.MOCHI, 1, type.ordinal());
            EntityItem entityI = new EntityItem(world, posX, posY, posZ, stack);
            world.spawnEntity(entityI);
        }
    }

    /**
     * ■
     */
    public void getDataManagerLocal()
    {
        //■状態
        stateM = TypeState.values()[this.getStateCook()].newState(this);

        //■調理時間
        stateM.setTickCook(this.getTickCook());

        stateM.setTickCookMax(this.getTickCookMax());

        dataManager.setClean();
    }
    /**
     * ■
     */
    public void setDataManagerLocal()
    {
        //■状態
        this.setStateCook();

        //■調理時間
        this.setTickCook();

        this.setTickCookMax();
    }

    /**
     *
     * @return
     */
    public int getStateCook()
    {
        return dataManager.get(STATE_COOK);
    }
    public void setStateCook()
    {
        dataManager.set(STATE_COOK, stateM.getType().ordinal());
        dataManager.setDirty(STATE_COOK);
    }

    /**
     *
     * @return
     */
    public int getTickCook()
    {
        return dataManager.get(TICK_COOK);
    }
    public void setTickCook()
    {
        dataManager.set(TICK_COOK, stateM.getTickCook());
        dataManager.setDirty(TICK_COOK);
    }

    /**
    *
    * @return
    */
    public int getTickCookMax()
    {
        return dataManager.get(TICK_COOK_MAX);
    }
    public void setTickCookMax()
    {
        dataManager.set(TICK_COOK_MAX, stateM.getTickCookMax());
        dataManager.setDirty(TICK_COOK_MAX);
    }


    @Override
    protected void readEntityFromNBT(NBTTagCompound nbtIn)
    {
        //■状態
        int stateNo = nbtIn.getInteger("state");
        if (stateNo >= TypeState.values().length) { stateNo = 0; }
        stateM = TypeState.values()[stateNo].newState(this);

        //■調理時間
        stateM.setTickCook(nbtIn.getInteger("tickCook"));

        //■調理時間
        stateM.setTickCookMax(nbtIn.getInteger("tickCookMax"));

        //Dirty
        setDataManagerLocal();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbtIn)
    {
        //■状態
        nbtIn.setInteger("state", stateM.getType().ordinal());

        //■調理時間
        nbtIn.setInteger("tickCook", stateM.getTickCook());

        //■調理時間
        nbtIn.setInteger("tickCookMax", stateM.getTickCookMax());
    }


    //===================
    //
    public enum TypeState
    {
        RAW,
        ROT,
        BALLOON1,
        STAY,
        BALLOON2,
        BALLOON3;

        public State newState(EntityMochi mochiIn)
        {
            State state = null;
            switch(this)
            {
                case ROT:
                    state = mochiIn.new RotState(mochiIn);
                    break;

                case BALLOON1:
                    state = mochiIn.new Balloon1State(mochiIn);
                    break;

                case STAY:
                    state = mochiIn.new StayState(mochiIn);
                    break;

                case BALLOON2:
                    state = mochiIn.new Balloon2State(mochiIn);
                    break;

                case BALLOON3:
                    state = mochiIn.new Balloon3State(mochiIn);
                    break;

                case RAW:
                default:
                    state = mochiIn.new RawState(mochiIn);
                    break;
            }
            return state;
        }
    }

    //===================
    //
    public abstract class State
    {
        //■餅
        protected final EntityMochi mochi;
        //■調理時間（最大）
        protected int tickCookMax;
        public void setTickCookMax(int tickMaxIn) { tickCookMax = tickMaxIn <= 0 ? 1 : tickMaxIn; }
        public int getTickCookMax() { return tickCookMax; }
        //■調理時間（現在）
        protected int tickCook = 0;
        public void setTickCook(int tickIn) { tickCook = tickIn; }
        public int getTickCook() { return tickCook; }


        //■コンストラクタ
        public State(EntityMochi mochiIn, int tickMaxIn)
        {
            mochi = mochiIn;
            tickCookMax = tickMaxIn;

            //DEBUG
//            System.out.println(this.getClass().getName());
        }

        //■更新
        public void update()
        {
            if (isTimeOut() == false) { tickCook++; }
        }
        //■状態遷移チェック
        public abstract State nextState();

        //■最大経過時間に到達したか否か
        public boolean isTimeOut() { return tickCook >= tickCookMax; }
        //■経過時間（比率）
        public float rate() { return (float)tickCook / (float)tickCookMax; }
        //■右クリックされた
        public void interact() {}
        //■
        public abstract TypeState getType();
        //■
        public ItemMochi.TypeMochi dropMochi()
        {
            return TypeMochi.RAW;
        }

        //■
        public float getOffsetRotX() { return 0f; }
        public float getOffsetPosY() { return 0.025f; }
        public float getScale() { return 0.06f; }
    }

    //===================
    //
    public class RawState extends State
    {
        private boolean clicked = false;

        //■
        public RawState(EntityMochi mochiIn) { super(mochiIn, 200); }

        @Override
        public void update()
        {
            if (tickCook < tickCookMax)
            {
                super.update();
            }
        }

        @Override
        public State nextState()
        {
            return isTimeOut() && clicked == true ? TypeState.ROT.newState(mochi) : this;
        }

        @Override
        public void interact() { clicked = isTimeOut() ? true : false; }

        @Override
        public TypeState getType() { return TypeState.RAW; }

//        @Override
//        public float getOffsetRotX() { return 0f; }
        @Override
        public float getOffsetPosY() { return super.getOffsetPosY() - 0.01f * (1.0f - rate()); }

        @Override
        public float getScale() { return super.getScale() - 0.01f * (1f - rate()); }
    }

    //===================
    //
    public class RotState extends State
    {

        public RotState(EntityMochi mochiIn) { super(mochiIn, 40); }

        @Override
        public State nextState()
        {
            return isTimeOut() ? TypeState.BALLOON1.newState(mochi) : this;
        }

        @Override
        public TypeState getType() { return TypeState.ROT; }

//        @Override
//        public Vec3d getRenderOffset()
//        {
//            double rX = 0f;
//            double pY = MathHelper.sin(rate() * (float)Math.PI) * 0.5f + 0.05f;
//            return new Vec3d(rX, pY, 0d);
//        }
        @Override
        public float getOffsetRotX() { return 180f * rate(); }
        @Override
        public float getOffsetPosY() { return MathHelper.sin(rate() * (float)Math.PI) * 0.5f + super.getOffsetPosY(); }
    }

    //===================
    //
    public class Balloon1State extends State
    {
        public Balloon1State(EntityMochi mochiIn)
        {
            super(mochiIn, mochiIn.rand.nextInt(800) + 200);
        }

        @Override
        public State nextState()
        {
            return isTimeOut() ? TypeState.STAY.newState(mochi) : this;
        }

        @Override
        public TypeState getType() { return TypeState.BALLOON1; }

//        @Override
//        public Vec3d getRenderOffset()
//        {
//            return new Vec3d(0d, 0d, 0d);
//        }
        @Override
        public float getOffsetRotX() { return 180f; }
//        @Override
//        public float getOffsetPosY() { return super.getOffsetPosY(); }

    }

    //===================
    //
    public class StayState extends State
    {
        public StayState(EntityMochi mochiIn)
        {
            super(mochiIn, mochiIn.rand.nextInt(600) + 200);
        }

        @Override
        public State nextState()
        {
            return isTimeOut() ? TypeState.BALLOON2.newState(mochi) : this;
        }

//        @Override
//        public void interact()
//        {
//            mochi.setDead();
//        }

        @Override
        public TypeState getType() { return TypeState.STAY; }

        @Override
        public ItemMochi.TypeMochi dropMochi()
        {
            return TypeMochi.BAKED;
        }

        @Override
        public float getOffsetRotX() { return 180f; }
    }

    //===================
    //
    public class Balloon2State extends State
    {

        public Balloon2State(EntityMochi mochiIn) { super(mochiIn, 200); }

        @Override
        public State nextState()
        {
            return isTimeOut() ? TypeState.BALLOON3.newState(mochi) : this;
        }

//        @Override
//        public void interact()
//        {
//            mochi.setDead();
//        }

        @Override
        public TypeState getType() { return TypeState.BALLOON2; }

        @Override
        public ItemMochi.TypeMochi dropMochi()
        {
            return TypeMochi.BAKED;
        }

//        @Override
//        public Vec3d getRenderOffset()
//        {
//            return new Vec3d(0d, 0d, 0d);
//        }

        @Override
        public float getOffsetRotX() { return 180f; }
//        @Override
//        public float getOffsetPosY() { return MathHelper.sin(rate() * (float)Math.PI) * 0.5f + super.getOffsetPosY(); }

    }


    //===================
    //
    public class Balloon3State extends State
    {

        public Balloon3State(EntityMochi mochiIn) { super(mochiIn, 20); }

        @Override
        public State nextState()
        {
            return isTimeOut() ? TypeState.STAY.newState(mochi) : this;
        }

//        @Override
//        public void interact()
//        {
//            mochi.setDead();
//        }

        @Override
        public TypeState getType() { return TypeState.BALLOON3; }

        @Override
        public ItemMochi.TypeMochi dropMochi()
        {
            return TypeMochi.BAKED;
        }

//        @Override
//        public Vec3d getRenderOffset()
//        {
//            return new Vec3d(0d, 0d, 0d);
//        }

        @Override
        public float getOffsetRotX() { return 180f; }
//        @Override
//        public float getOffsetPosY() { return MathHelper.sin(rate() * (float)Math.PI) * 0.5f + super.getOffsetPosY(); }

    }


}
