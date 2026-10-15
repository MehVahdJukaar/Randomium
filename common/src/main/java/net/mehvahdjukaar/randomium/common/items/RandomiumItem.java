package net.mehvahdjukaar.randomium.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;
import java.util.Random;


public class RandomiumItem extends Item {
    public RandomiumItem(Properties prop) {
        super(prop);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> text, TooltipFlag tooltipFlag) {
        text.accept((Component.translatable("message.randomium.description"))
                .withStyle(ChatFormatting.DARK_PURPLE));
        super.appendHoverText(stack, context, display, text, tooltipFlag);
    }

    //TODO: use this for trollium
    /*
    @Override
    public FontRenderer getFontRenderer(ItemStack stack) {
        return super.getFontRenderer(stack);
    }*/


    private static boolean crazy;
    private static final Random RANDOM = new Random();

    private static final String OBFUSCATE_TEXT = "" + ChatFormatting.OBFUSCATED + "asfoiz";

    @Override
    public Component getName(ItemStack stack) {
        IS_RENDERING_TOOLTIP = true;
        return crazy ? Component.literal(OBFUSCATE_TEXT) : super.getName(stack);
    }

    private static int MIN_COOLDOWN = 0;
    private static boolean IS_RENDERING_TOOLTIP;

    public static void tickEffects() {
        if (IS_RENDERING_TOOLTIP) {
            if (MIN_COOLDOWN == 0) {
                if (crazy) {
                    if (RANDOM.nextInt(4) == 0) crazy = false;
                } else {
                    if (RANDOM.nextInt(16) == 0) crazy = true;
                }
                MIN_COOLDOWN = 17;
            } else MIN_COOLDOWN--;
        }
        IS_RENDERING_TOOLTIP = false;

    }
}