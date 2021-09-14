package net.mehvahdjukaar.randomium;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Randomium.MOD_ID)
public class Randomium {
    public static final String MOD_ID = "randomium";

    private static final Logger LOGGER = LogManager.getLogger();

    //yes
    private static Random RAND = new Random();

    //api?
    //public static final RegistryObject<EntityType<RisingBlockEntity>> RISING_BLOCK =
    //        RegistryObject.of(new ResourceLocation(MOD_ID+":rising_block"), ForgeRegistries.ENTITIES);
    @ObjectHolder("randomium:randomium")
    public static final Item RANDOMIUM_ITEM = null;
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final RegistryObject<Block> RANDOMIUM = BLOCKS.register(Randomium.class.getSimpleName().toLowerCase() + "_ore", () ->
            new Block(AbstractBlock.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops().strength(4.0F, 3.0F)
                    .harvestTool(ToolType.PICKAXE).harvestLevel(2)) {

                @Override
                public void spawnAfterBreak(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
                    //mod compat???
                    if (state.getBlock() != RANDOMIUM.get()) {
                        this.getRandomDrops(state, world, stack, null).forEach((d) -> popResource(world, pos, d));
                    }
                }

                @Override
                public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
                    return getRandomDrops(state, builder.getLevel(), builder.getOptionalParameter(LootParameters.TOOL),
                            builder.getOptionalParameter(LootParameters.THIS_ENTITY));
                }

                public List<ItemStack> getRandomDrops(BlockState state, World world, @Nullable ItemStack tool, @Nullable Entity entity) {

                    ItemStack loot;
                    float percentage = 0.5f;
                    if (entity instanceof LivingEntity) {
                        LivingEntity le = ((LivingEntity) entity);
                        if (le.hasEffect(Effects.LUCK)) {
                            percentage += le.getEffect(Effects.LUCK).getAmplifier();
                        }
                        if (le.hasEffect(Effects.UNLUCK)) {
                            percentage -= le.getEffect(Effects.UNLUCK).getAmplifier();
                        }
                        if (tool != null) {
                            int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
                            percentage += 0.2f * fortune;
                        }
                    }

                    //world rng is better
                    if (world.random.nextFloat() * 100 <= percentage) {

                        loot = new ItemStack(RANDOMIUM_ITEM);
                    } else if (tool != null && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) != 0)
                        loot = new ItemStack(this.asItem());
                    else {
                        List<ItemStack> l = LOOT.get(world.random.nextInt(LOOT.size()));
                        loot = l.get(world.random.nextInt(l.size()));
                    }
                    return Collections.singletonList(loot);
                }

                @Override
                public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
                    return silktouch == 0 ? MathHelper.nextInt(RANDOM, 0, 6) : 0;
                }

                @Override
                public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
                    return SOUNDS.get(RANDOM.nextInt(SOUNDS.size()));
                }

                @Override
                public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
                    if (world instanceof World) {
                        return ((World) world).random.nextInt(10);
                    }
                    return 6;
                }

                public Random getBlockRandom(BlockPos pos) {
                    return new Random(this.getBlockSeed(pos));
                }

                public Long getBlockSeed(BlockPos pos) {
                    return MathHelper.getSeed(pos);
                }

                @Override
                public boolean hasAnalogOutputSignal(BlockState state) {
                    return true;
                }

                @Override
                public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
                    return this.getBlockRandom(pos).nextInt(16);
                }

                public int getGravityType(BlockPos pos) {
                    long seed = this.getBlockSeed(pos);
                    if (seed % 3 == 0) return 1;
                    //else if (seed % 3 == 0) return 2;
                    return 0;
                }

                @Override
                public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
                }

                private double total = 0;
                private final NavigableMap<Double, Direction> map = new TreeMap<Double, Direction>() {{
                    put(total += 2d, Direction.UP);
                    put(total += 30, Direction.DOWN);
                    put(total += 10, Direction.NORTH);
                    put(total += 10, Direction.SOUTH);
                    put(total += 10, Direction.EAST);
                    put(total += 10, Direction.WEST);
                    put(total += 8, null);
                }};

                @Override
                public void attack(BlockState state, World world, BlockPos pos, PlayerEntity entity) {
                    if (!world.isClientSide && world.random.nextFloat() < 0.9) {

                        Direction dir = map.higherEntry(world.random.nextDouble() * total).getValue();

                        if (dir == null) {
                            this.teleport(state, (ServerWorld) world, pos);
                        } else {
                            this.move(state, world, pos, dir);
                        }
                    }
                }

                @Override
                public boolean triggerEvent(BlockState state, World world, BlockPos end, int eventID, int eventParam) {
                    if (eventID == 0) {
                        Random random = world.random;
                        int dx = (eventParam & 255) - 64;
                        int dy = (eventParam >> 8 & 255) - 64;
                        int dz = (eventParam >> 16 & 255) - 64;
                        BlockPos start = new BlockPos(end.getX() - dx, end.getY() - dy, end.getZ() - dz);
                        for (int j = 0; j < 64; ++j) {
                            double d0 = random.nextDouble();
                            float f = (random.nextFloat() - 0.5F) * 0.2F;
                            float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                            float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                            double d1 = MathHelper.lerp(d0, end.getX(), start.getX()) + (random.nextDouble() - 0.5D) + 0.5D;
                            double d2 = MathHelper.lerp(d0, end.getY(), start.getY()) + random.nextDouble() - 0.5D;
                            double d3 = MathHelper.lerp(d0, end.getZ(), start.getZ()) + (random.nextDouble() - 0.5D) + 0.5D;
                            Minecraft.getInstance().level.addParticle(ParticleTypes.PORTAL, d1, d2, d3, f, f1, f2);
                        }
                        return true;
                    }
                    return super.triggerEvent(state, world, end, eventID, eventParam);
                }

                private void teleport(BlockState state, ServerWorld world, BlockPos pos) {
                    final int range = 7;
                    for (int i = 0; i < 1000; ++i) {
                        BlockPos blockpos = pos.offset(world.random.nextInt(range) - world.random.nextInt(range), world.random.nextInt(range / 2) - world.random.nextInt(range / 2), world.random.nextInt(range) - world.random.nextInt(range));
                        if (world.getBlockState(blockpos).isAir()) {

                            int dx = (byte) (blockpos.getX() - pos.getX()) + 64;
                            int dy = (byte) (blockpos.getY() - pos.getY()) + 64;
                            int dz = (byte) (blockpos.getZ() - pos.getZ()) + 64;

                            world.setBlock(blockpos, state, 2);
                            world.removeBlock(pos, false);


                            world.blockEvent(blockpos, this, 0, (dz & 255) << 16 | (dy & 255) << 8 | dx & 255);


                            return;
                        }
                    }

                }

                public void move(BlockState state, World world, BlockPos pos, Direction dir) {

                    if (FallingBlock.isFree(world.getBlockState(pos.relative(dir)))) {
                        MovingBlockEntity entity = new MovingBlockEntity(world, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, state);
                        entity.setGravityDirection(dir);
                        world.addFreshEntity(entity);
                    }

                }
            });

    public static ForgeConfigSpec.IntValue SPAWN_PER_CHUNK;

    public Randomium() {

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SPAWN_PER_CHUNK = builder.defineInRange("spawn_attempts_per_chunk", 4, 0, 50);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build());

        // Register the setup method for modloading
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(EventPriority.LOWEST, this::setup);
        bus.addGenericListener(Item.class, this::registerItems);
        bus.addGenericListener(EntityType.class, this::registerEntities);
        bus.addGenericListener(IRecipeSerializer.class, this::registerRecipes);
        bus.addListener(this::doClientStuff);


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        BLOCKS.register(bus);
    }

    public static final List<List<ItemStack>> LOOT = new ArrayList<>();

    public static final List<Block> EMULATE = new ArrayList<>();

    public static final List<SoundType> SOUNDS = new ArrayList<>();

    public Tags.IOptionalNamedTag<Item> BLACKLIST = ItemTags.createOptional(new ResourceLocation(MOD_ID, "blacklist"));

    @SubscribeEvent
    public void onTagLoad(TagsUpdatedEvent event) {
        if (LOOT.isEmpty()) {
            ForgeRegistries.ITEMS.getValues().stream().filter(i -> !i.is(BLACKLIST) && !i.getRegistryName().getPath().contains("creative")).forEach(i -> {
                NonNullList<ItemStack> temp = NonNullList.create();
                try {
                    Arrays.stream(ItemGroup.TABS).forEach(t -> i.fillItemCategory(t, temp));
                    if (!temp.isEmpty()) LOOT.add(temp);
                } catch (Exception ignored) {
                }
            });
            //lucky
            LOOT.add(Collections.singletonList(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LUCK)));
        }
    }

    private void setup(final FMLCommonSetupEvent event) {

        //yay for one liners
        ForgeRegistries.BLOCKS.getValues().stream().map(b -> b.getSoundType(b.defaultBlockState()))
                .filter(s -> !SOUNDS.contains(s)).forEach(SOUNDS::add);
        EMULATE.addAll(ForgeRegistries.BLOCKS.getValues());


        RANDOMIUM_ORE = new Feature<OreFeatureConfig>(OreFeatureConfig.CODEC) {
            //copy pasted from MCreator. cry about it >:)
            @Override
            public boolean place(ISeedReader reader, ChunkGenerator generator, Random random, BlockPos pos, OreFeatureConfig config) {
                if (reader.getLevel().dimensionType().natural()) {
                    if (config.target.test(reader.getBlockState(pos), random)) {
                        reader.setBlock(pos, config.state, 2);
                        return true;
                    }
                }
                return false;
            }

        }.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, RANDOMIUM.get().defaultBlockState(), 1))
                .range(128).squared().count(SPAWN_PER_CHUNK.get());

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(MOD_ID, "randomium_ore"), RANDOMIUM_ORE);
    }

    public static ConfiguredFeature<?, ?> RANDOMIUM_ORE;

    @SubscribeEvent
    public void addFeatureToBiomes(BiomeLoadingEvent event) {
        Biome.Category c = event.getCategory();
        if (c == Biome.Category.NETHER || c == Biome.Category.THEEND) return;
        event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> RANDOMIUM_ORE);
    }

    public void registerRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new SpecialRecipeSerializer<>(r -> new SpecialRecipe(r) {

            private boolean isRandomium(ItemStack stack) {
                return stack.getItem() == RANDOMIUM_ITEM;
            }

            private boolean isValid(ItemStack stack) {
                CompoundNBT tag = stack.getTag();
                if (tag != null) {
                    if (stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) return false;
                    String s = tag.toString();
                    //can never be too careful
                    if (s.contains("Items:[") || s.contains(RANDOMIUM_ITEM.getRegistryName().toString())) return false;
                }
                return !stack.getItem().is(BLACKLIST);
            }

            @Override
            public boolean matches(CraftingInventory inv, World worldIn) {

                ItemStack itemstack = null;
                ItemStack itemstack1 = null;

                for (int i = 0; i < inv.getContainerSize(); ++i) {
                    ItemStack stack = inv.getItem(i);
                    if (!stack.isEmpty()) {

                        if (isRandomium(stack)) {
                            if (itemstack1 != null) {
                                return false;
                            }
                            itemstack1 = stack;
                        } else if (isValid(stack)) {

                            if (itemstack != null) {
                                return false;
                            }
                            itemstack = stack;
                        }
                    }

                }
                return itemstack != null && itemstack1 != null;
            }

            @Override
            public ItemStack assemble(CraftingInventory inv) {
                for (int i = 0; i < inv.getContainerSize(); ++i) {
                    ItemStack stack = inv.getItem(i);
                    if (!stack.isEmpty() && !isRandomium(stack)) {
                        ItemStack s = stack.copy();
                        s.setCount(1);
                        return s;
                    }
                }
                return ItemStack.EMPTY;
            }

            @Override
            public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
                NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

                for (int i = 0; i < nonnulllist.size(); ++i) {
                    ItemStack itemstack = inv.getItem(i).copy();
                    if (!isRandomium(itemstack)) {
                        itemstack.setCount(1);
                        nonnulllist.set(i, itemstack);
                    }

                }
                return nonnulllist;
            }

            @Override
            public boolean canCraftInDimensions(int width, int height) {
                return width * height >= 2;
            }

            @Override
            public IRecipeSerializer<?> getSerializer() {
                return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(MOD_ID, "randomium_clone"));
            }
        }).setRegistryName("randomium_clone"));

//RISING_BLOCK.getId().getPath()
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.trace(RAND.nextInt(1000) + "!");
        // do something when the server starts
    }

    private void registerItems(final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new Item(new Item.Properties()
                .tab(ItemGroup.TAB_BUILDING_BLOCKS).rarity(Rarity.EPIC)) {

            @Override
            public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag tooltipFlag) {
                text.add((new TranslationTextComponent("message.randomium.description")).withStyle(TextFormatting.DARK_PURPLE));
                super.appendHoverText(stack, world, text, tooltipFlag);
            }
        }.setRegistryName(MOD_ID + ":" + MOD_ID));
        event.getRegistry().register(new BlockItem(RANDOMIUM.get(), new Item.Properties()
                .tab(ItemGroup.TAB_BUILDING_BLOCKS)).setRegistryName(MOD_ID + ":" + MOD_ID + "_ore"));

    }

    private void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(EntityType.Builder.<MovingBlockEntity>of(MovingBlockEntity::new,
                EntityClassification.MISC)
                .sized(0.98F, 0.98F)
                .clientTrackingRange(10)
                .updateInterval(20)
                .setCustomClientFactory(MovingBlockEntity::new)
                .build("moving_block").setRegistryName("moving_block"));

    }

    @ObjectHolder("randomium:moving_block")
    public static final EntityType<MovingBlockEntity> MOVING_BLOCK_ENTITY_TYPE = null;

    public static class MovingBlockEntity extends Entity implements IEntityAdditionalSpawnData {

        private Direction gravityDirection = Direction.DOWN;
        private BlockState blockState = Blocks.SAND.defaultBlockState();
        public int time;
        private boolean cancelDrop;

        protected static final DataParameter<BlockPos> DATA_START_POS = EntityDataManager.defineId(net.minecraft.entity.item.FallingBlockEntity.class, DataSerializers.BLOCK_POS);

        public MovingBlockEntity(EntityType<? extends MovingBlockEntity> type, World world) {
            super(type, world);
        }

        public MovingBlockEntity(World world, double x, double y, double z, BlockState state) {
            this(MOVING_BLOCK_ENTITY_TYPE, world);
            this.blockState = state;
            this.blocksBuilding = true;
            this.setPos(x, y + (double) ((1.0F - this.getBbHeight()) / 2.0F), z);
            this.setDeltaMovement(Vector3d.ZERO);
            this.xo = x;
            this.yo = y;
            this.zo = z;
            this.setStartPos(this.blockPosition());
        }

        public void setGravityDirection(Direction gravityDirection) {
            this.gravityDirection = gravityDirection;
        }

        public MovingBlockEntity(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
            this(MOVING_BLOCK_ENTITY_TYPE, world);
        }

        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }

        @Override
        public void writeSpawnData(PacketBuffer buffer) {
            buffer.writeInt(Block.getId(this.blockState));
            buffer.writeEnum(this.gravityDirection);
        }

        @Override
        public void readSpawnData(PacketBuffer additionalData) {
            this.blockState = Block.stateById(additionalData.readInt());
            this.gravityDirection = additionalData.readEnum(Direction.class);
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT tag) { //this.entityData.get(DATA_FALL_DIRECTION)
            tag.put("BlockState", NBTUtil.writeBlockState(this.blockState));
            tag.putByte("GravityDirection", (byte) this.gravityDirection.get3DDataValue());
            tag.putInt("Time", this.time);
        }

        @Override
        protected void readAdditionalSaveData(CompoundNBT tag) {
            this.blockState = NBTUtil.readBlockState(tag.getCompound("BlockState"));
            this.gravityDirection = Direction.from3DDataValue(tag.getByte("GravityDirection"));
            //this.entityData.set(DATA_FALL_DIRECTION, Direction.from3DDataValue(tag.getByte("GravityDirection")));
            this.time = tag.getInt("Time");

            if (this.blockState.isAir()) {
                this.blockState = Blocks.RED_SAND.defaultBlockState();
            }
        }

        @Override
        protected void defineSynchedData() {
            this.entityData.define(DATA_START_POS, BlockPos.ZERO);
        }

        @Override
        public boolean isAttackable() {
            return false;
        }

        public void setStartPos(BlockPos pos) {
            this.entityData.set(DATA_START_POS, pos);
        }

        public BlockPos getStartPos() {
            return this.entityData.get(DATA_START_POS);
        }

        @Override
        protected boolean isMovementNoisy() {
            return false;
        }

        @Override
        public boolean isPickable() {
            return this.isAlive();
        }

        @Override
        public void tick() {
            if (this.blockState.isAir()) {
                this.remove();
            } else {
                Block block = this.blockState.getBlock();
                if (this.time++ == 0) {
                    BlockPos blockpos = this.blockPosition();
                    if (this.level.getBlockState(blockpos).is(block)) {
                        this.level.removeBlock(blockpos, false);
                    } else if (!this.level.isClientSide) {
                        this.remove();
                        return;
                    }
                }

                Vector3i n = this.gravityDirection.getNormal();
                if (!this.isNoGravity()) {

                    this.setDeltaMovement(this.getDeltaMovement().add(n.getX() * 0.04D, n.getY() * 0.04D, n.getZ() * 0.04D));
                }

                this.moveSelf(this.getDeltaMovement());
                if (!this.level.isClientSide) {
                    BlockPos blockpos1 = this.blockPosition();

                    boolean collided = this.verticalCollision || this.horizontalCollision;

                    boolean isVertical = this.gravityDirection.getAxis() == Direction.Axis.Y;

                    if (collided || !isVertical && this.random.nextBoolean() && this.position().distanceToSqr(Vector3d.atBottomCenterOf(this.blockPosition())) <=
                            MathHelper.square((float) this.getDeltaMovement().multiply(n.getX(), n.getY(), n.getZ()).length()) / 2f) {
                        BlockState blockstate = this.level.getBlockState(blockpos1);
                        //this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
                        if (!blockstate.is(Blocks.MOVING_PISTON)) {
                            this.remove();
                            if (!this.cancelDrop) {
                                boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, blockpos1, this.gravityDirection, ItemStack.EMPTY, Direction.UP));
                                //boolean flag3 = FallingBlock.isFree(this.level.getBlockState(blockpos1.relative(this.gravityDirection)));
                                boolean flag4 = this.blockState.canSurvive(this.level, blockpos1);
                                if (flag2 && flag4 && this.level.setBlock(blockpos1, this.blockState, 3)) {

                                } else if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.spawnAtLocation(block);
                                }
                            }
                        }
                    } else {
                        if (this.time > 100 && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.time > 600) {

                            if (this.gravityDirection == Direction.UP) {
                                this.gravityDirection = Direction.DOWN;
                                this.time = 1;
                            } else {
                                if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.spawnAtLocation(block);
                                }

                                this.remove();
                            }
                        }
                    }
                }
                this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
            }
        }


        public void moveSelf(Vector3d movement) {
            if (this.noPhysics) {
                this.setBoundingBox(this.getBoundingBox().move(movement));
                this.setLocationFromBoundingbox();
            } else {

                this.level.getProfiler().push("move");
                if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7D) {
                    movement = movement.multiply(this.stuckSpeedMultiplier);
                    this.stuckSpeedMultiplier = Vector3d.ZERO;
                    this.setDeltaMovement(Vector3d.ZERO);
                }

                Vector3d vector3d = this.collide(movement);
                if (vector3d.lengthSqr() > 1.0E-7D) {
                    this.setBoundingBox(this.getBoundingBox().move(vector3d));
                    this.setLocationFromBoundingbox();
                }

                this.level.getProfiler().pop();

                this.level.getProfiler().push("rest");
                this.horizontalCollision = !MathHelper.equal(movement.x, vector3d.x) || !MathHelper.equal(movement.z, vector3d.z);
                this.verticalCollision = movement.y != vector3d.y;
                this.onGround = this.verticalCollision && movement.y < 0.0D;
                BlockPos blockpos = this.getOnPos();
                BlockState blockstate = this.level.getBlockState(blockpos);

                this.checkFallDamage(vector3d.y, this.onGround, blockstate, blockpos);

                Vector3d vector3d1 = this.getDeltaMovement();
                if (movement.x != vector3d.x) {
                    this.setDeltaMovement(0.0D, vector3d1.y, vector3d1.z);
                }

                if (movement.z != vector3d.z) {
                    this.setDeltaMovement(vector3d1.x, vector3d1.y, 0.0D);
                }

                Block block = blockstate.getBlock();
                if (movement.y != vector3d.y) {
                    block.updateEntityAfterFallOn(this.level, this);
                }

                try {
                    this.checkInsideBlocks();
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.forThrowable(throwable, "Checking entity block collision");
                    CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being checked for collision");
                    this.fillCrashReportCategory(crashreportcategory);
                    throw new ReportedException(crashreport);
                }

                float f2 = this.getBlockSpeedFactor();
                this.setDeltaMovement(this.getDeltaMovement().multiply(f2, 1.0D, f2));

                this.level.getProfiler().pop();
            }
        }

        @Override
        public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
            int i = MathHelper.ceil(p_225503_1_ - 1.0F);
            if (i > 0) {
                List<Entity> list = Lists.newArrayList(this.level.getEntities(this, this.getBoundingBox()));
                boolean flag = this.blockState.is(BlockTags.ANVIL);
                DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

                for (Entity entity : list) {
                    float fallDamageAmount = 2.0F;
                    int fallDamageMax = 40;
                    entity.hurt(damagesource, (float) Math.min(MathHelper.floor((float) i * fallDamageAmount), fallDamageMax));
                }

                if (flag && (double) this.random.nextFloat() < (double) 0.05F + (double) i * 0.05D) {
                    BlockState blockstate = AnvilBlock.damage(this.blockState);
                    if (blockstate == null) {
                        this.cancelDrop = true;
                    } else {
                        this.blockState = blockstate;
                    }
                }
            }

            return false;
        }

        @Override
        public boolean displayFireAnimation() {
            return false;
        }

        @Override
        public boolean onlyOpCanSetNbt() {
            return true;
        }
    }


    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        RenderingRegistry.registerEntityRenderingHandler(MOVING_BLOCK_ENTITY_TYPE,
                renderManager -> new EntityRenderer<MovingBlockEntity>(renderManager) {

                    @Override
                    public void render(MovingBlockEntity entity, float val, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light) {
                        BlockState blockstate = entity.blockState;
                        if (blockstate.getRenderShape() == BlockRenderType.MODEL) {
                            World world = entity.level;
                            if (blockstate != world.getBlockState(entity.blockPosition()) && blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
                                matrixStack.pushPose();
                                BlockPos blockpos = new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
                                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
                                for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.chunkBufferLayers()) {
                                    if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
                                        ForgeHooksClient.setRenderLayer(type);
                                        blockrendererdispatcher.getModelRenderer().tesselateBlock(world, blockrendererdispatcher.getBlockModel(blockstate), blockstate, blockpos, matrixStack, buffer.getBuffer(type), false, new Random(), blockstate.getSeed(entity.getStartPos()), OverlayTexture.NO_OVERLAY);
                                    }
                                }
                                ForgeHooksClient.setRenderLayer(null);
                                matrixStack.popPose();
                                super.render(entity, val, partialTicks, matrixStack, buffer, light);
                            }
                        }
                    }

                    public ResourceLocation getTextureLocation(MovingBlockEntity entity) {
                        return AtlasTexture.LOCATION_BLOCKS;
                    }
                });
    }


}
