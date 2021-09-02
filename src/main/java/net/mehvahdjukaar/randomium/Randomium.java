package net.mehvahdjukaar.randomium;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
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
    public static final RegistryObject<Block> RANDOMIUM = BLOCKS.register(Randomium.class.getSimpleName().toLowerCase()+"_ore", () ->
            new FallingBlock(AbstractBlock.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops().strength(4.0F, 3.0F)
                    .harvestTool(ToolType.PICKAXE).harvestLevel(3)){
        @Override
        public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {

            ItemStack tool = builder.getOptionalParameter(LootParameters.TOOL);
            ItemStack loot;
            Entity e = builder.getOptionalParameter(LootParameters.THIS_ENTITY);
            float percentage = 0.5f;
            if(e instanceof LivingEntity){
                LivingEntity le = ((LivingEntity) e);
                if(le.hasEffect(Effects.LUCK)){
                    percentage += le.getEffect(Effects.LUCK).getAmplifier();
                }
                if(le.hasEffect(Effects.UNLUCK)){
                    percentage -= le.getEffect(Effects.UNLUCK).getAmplifier();
                }
                if(tool!=null){
                    int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
                    percentage += 0.175f*fortune;
                }
            }

            //world rng is better
            if(builder.getLevel().random.nextFloat()*100 <= percentage){

                loot = new ItemStack(RANDOMIUM_ITEM);
            }
            else if (tool != null && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) != 0) loot = new ItemStack(this.asItem());
            else{
                List<ItemStack> l = LOOT.get(builder.getLevel().random.nextInt(LOOT.size()));
                loot = l.get(builder.getLevel().random.nextInt(l.size()));
            }
            return Collections.singletonList(loot);
        }

        @Override
        public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
            return silktouch == 0 ? MathHelper.nextInt(RANDOM, 0, 9) : 0;
        }

        @Override
        public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
            return SOUNDS.get(RANDOM.nextInt(SOUNDS.size()));
        }

        @Override
        public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
            if(world instanceof World){
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
            return world.random.nextInt(16);
        }

        public int getGravityType(BlockPos pos) {
            long seed = this.getBlockSeed(pos);
            if (seed % 10 == 0) return 1;
            //else if (seed % 3 == 0) return 2;
            return 0;
        }

        @Override
        public void animateTick(BlockState state, World world, BlockPos pos, Random random) {}

        @Override
        public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
            int g = this.getGravityType(pos);

            switch (g){
                case 2:
                    //nested inner classes. nice
                    if (world.isEmptyBlock(pos.above()) || isFree(world.getBlockState(pos.above())) && pos.getY() <= 255) {
                        FallingBlockEntity fallingblockentity =
                            new FallingBlockEntity(world, (double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D, state){
                                @Override
                                public void setStartPos(BlockPos pos) {
                                    super.setStartPos(pos);
                                    this.fallDir = Direction.values()[new Random(MathHelper.getSeed(pos)).nextInt(5)+1];
                                }

                                //registering new entities is for losers
                                public Direction fallDir = Direction.DOWN;
                                @Override
                                public void tick() {
                                    if(!level.isClientSide && !FallingBlock.isFree(level.getBlockState(this.blockPosition().relative(fallDir)))){
                                        this.level.setBlock(this.blockPosition(), this.getBlockState(), 3);
                                        this.remove();
                                    }
                                    this.setDeltaMovement(this.getDeltaMovement().add(0,0.04,0).add(new Vector3d(fallDir.step()).scale(0.01)));

                                    super.tick();
                                }
                            };
                        this.falling(fallingblockentity);
                        world.addFreshEntity(fallingblockentity);
                    }
                    break;
                case 1:
                    super.tick(state, world, pos, random);
                    break;
                default:
            }
        }
    });


    public Randomium() {
        // Register the setup method for modloading
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(EventPriority.LOWEST, this::setup);
        bus.addGenericListener(Item.class, this::registerItems);
        bus.addGenericListener(IRecipeSerializer.class, this::registerRecipes);
        //bus.addListener(Randomium::registerItems);
        // Register the doClientStuff method for modloading
        bus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        BLOCKS.register(bus);
    }

    public static final List<List<ItemStack>> LOOT = new ArrayList<>();

    public static final List<Block> EMULATE = new ArrayList<>();

    public static final List<SoundType> SOUNDS = new ArrayList<>();


    @SubscribeEvent
    public void onTagLoad(TagsUpdatedEvent event) {
        if(LOOT.isEmpty()) {
            Tags.IOptionalNamedTag<Item> tag = ItemTags.createOptional(new ResourceLocation(MOD_ID, "blacklist"));
            ForgeRegistries.ITEMS.getValues().stream().filter(i -> !i.is(tag)).forEach(i -> {
                NonNullList<ItemStack> temp = NonNullList.create();
                Arrays.stream(ItemGroup.TABS).forEach(t -> i.fillItemCategory(t, temp));
                if (!temp.isEmpty()) LOOT.add(temp);
            });
        }
    }

    private void setup(final FMLCommonSetupEvent event) {

        try {
            //fixes vanilla extremely low update rate for rising entity
            //TODO: uff, create a new entity, this still glitches downwards for some reason
            Field field = ObfuscationReflectionHelper.findField(EntityType.class, "field_233595_bm_");
            //field.setAccessible(true);
            //field.set(EntityType.FALLING_BLOCK,3);
        }catch (Exception ignored){}



        //yay for one liners
        ForgeRegistries.BLOCKS.getValues().stream().map(b->b.getSoundType(b.defaultBlockState()))
                .filter(s->!SOUNDS.contains(s)).forEach(SOUNDS::add);
        EMULATE.addAll(ForgeRegistries.BLOCKS.getValues());

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(MOD_ID, "randomium_ore"), LoadLater.RANDOMIUM_ORE);
    }

    public static class LoadLater {

        public static final ConfiguredFeature<?, ?> RANDOMIUM_ORE = new Feature<OreFeatureConfig>(OreFeatureConfig.CODEC) {
            //copy pasted from MCreator. cry about it >:)
            @Override
            public boolean place(ISeedReader reader, ChunkGenerator generator, Random random, BlockPos pos, OreFeatureConfig config) {
                if (config.target.test(reader.getBlockState(pos), random)) {
                    reader.setBlock(pos, config.state, 2);
                    return true;
                }
                return false;
            }

        }.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, RANDOMIUM.get().defaultBlockState(), 1))
                .range(128).squared().count(4);
    }

    @SubscribeEvent
    public void addFeatureToBiomes(BiomeLoadingEvent event) {
        event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> LoadLater.RANDOMIUM_ORE);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
    }


    public void registerRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new SpecialRecipeSerializer<>(r -> new SpecialRecipe(r){

            private boolean isRandomium(ItemStack stack){
                return stack.getItem() == RANDOMIUM_ITEM;
            }
            @Override
            public boolean matches(CraftingInventory inv, World worldIn) {

                ItemStack itemstack = null;
                ItemStack itemstack1 = null;

                for(int i = 0; i < inv.getContainerSize(); ++i) {
                    ItemStack stack = inv.getItem(i);
                    if (!stack.isEmpty()) {

                        if(isRandomium(stack)) {
                            if (itemstack1 != null) {
                                return false;
                            }
                            itemstack1 = stack;
                        }
                        else {

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
                for(int i = 0; i < inv.getContainerSize(); ++i) {
                    ItemStack stack = inv.getItem(i);
                    if(!stack.isEmpty() && !isRandomium(stack)){
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

                for(int i = 0; i < nonnulllist.size(); ++i) {
                    ItemStack itemstack = inv.getItem(i).copy();
                    if(!isRandomium(itemstack)){
                        itemstack.setCount(1);
                        nonnulllist.set(i,itemstack);
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
                return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(MOD_ID,"randomium_clone"));
            }
        }).setRegistryName("randomium_clone"));

//RISING_BLOCK.getId().getPath()
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.trace(RAND.nextInt(1000)+"!");
        // do something when the server starts
    }

    private void registerItems(final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new Item(new Item.Properties()
                .tab(ItemGroup.TAB_BUILDING_BLOCKS).rarity(Rarity.EPIC)){

            @Override
            public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag tooltipFlag) {
                text.add((new TranslationTextComponent("message.randomium.description")).withStyle(TextFormatting.DARK_PURPLE));
                super.appendHoverText(stack, world, text, tooltipFlag);
            }
        }.setRegistryName(MOD_ID+":"+MOD_ID));
        event.getRegistry().register(new BlockItem(RANDOMIUM.get(), new Item.Properties()
                .tab(ItemGroup.TAB_BUILDING_BLOCKS)).setRegistryName(MOD_ID+":"+MOD_ID+"_ore"));

    }


}
