package net.mehvahdjukaar.randomium.items;

import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Randomium.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RandomiumItem extends Item {
    public RandomiumItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag tooltipFlag) {
        text.add((new TranslationTextComponent("message.randomium.description")).withStyle(TextFormatting.DARK_PURPLE));
        super.appendHoverText(stack, world, text, tooltipFlag);
    }

    //TODO: use this for trollium
    /*
    @Override
    public FontRenderer getFontRenderer(ItemStack stack) {
        return super.getFontRenderer(stack);
    }*/


    private static boolean crazy;
    private static final Random RANDOM = new Random();

    private static final String OBFUSCATE_TEXT = "" + TextFormatting.OBFUSCATED + "asfoiz";

    @Override
    public String getDescriptionId() {
        renderingTooltip = true;
        return crazy ? OBFUSCATE_TEXT : super.getDescriptionId();
    }

    private static int minCooldown = 0;
    private static boolean renderingTooltip;

    @SubscribeEvent
    public static void test(TickEvent.ClientTickEvent tickEvent){
        if(tickEvent.phase == TickEvent.Phase.END){
            if(renderingTooltip){
                if(minCooldown == 0) {
                    if (crazy) {
                        if (RANDOM.nextInt(4) == 0) crazy = false;
                    } else {
                        if (RANDOM.nextInt(16) == 0) crazy = true;
                    }
                    minCooldown = 17;
                }
                else minCooldown--;
            }
            renderingTooltip = false;
        }
    }
}