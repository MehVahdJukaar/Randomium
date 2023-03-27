package net.mehvahdjukaar.randomium.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


public class RandomiumItem extends Item {
    public RandomiumItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag tooltipFlag) {
        text.add((Component.translatable("message.randomium.description")).withStyle(ChatFormatting.DARK_PURPLE));
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

    private static final String OBFUSCATE_TEXT = "" + ChatFormatting.OBFUSCATED + "asfoiz";

    @Override
    public String getDescriptionId() {
        renderingTooltip = true;
        return crazy ? OBFUSCATE_TEXT : super.getDescriptionId();
    }

    private static int minCooldown = 0;
    private static boolean renderingTooltip;

    public static void tickEffects() {
        if (renderingTooltip) {
            if (minCooldown == 0) {
                if (crazy) {
                    if (RANDOM.nextInt(4) == 0) crazy = false;
                } else {
                    if (RANDOM.nextInt(16) == 0) crazy = true;
                }
                minCooldown = 17;
            } else minCooldown--;
        }
        renderingTooltip = false;

    }
}