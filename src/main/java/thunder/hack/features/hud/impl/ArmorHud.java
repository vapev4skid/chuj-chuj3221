package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import thunder.hack.features.hud.HudElement;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.features.modules.client.HudEditor;

import java.awt.*;

public class ArmorHud extends HudElement {
    public ArmorHud() {
        super("ArmorHud", 60, 35); // Increased height to accommodate bars
    }

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.V2);
    private final Setting<Boolean> showDurability = new Setting<>("ShowDurability", true);
    private final Setting<Boolean> showBars = new Setting<>("ShowBars", true);
    private final Setting<BarStyle> barStyle = new Setting<>("BarStyle", BarStyle.Green, v -> showBars.getValue());

    private enum Mode {
        V1, V2
    }
    
    private enum BarStyle {
        Green, ColorCoded, Rainbow
    }

    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        float xItemOffset = getPosX();
        for (ItemStack itemStack : mc.player.getInventory().armor.reversed()) {
            if (itemStack.isEmpty()) continue;

            if (mode.is(Mode.V1)) {
                context.drawItem(itemStack, (int) xItemOffset, (int) getPosY());
                context.drawItemInSlot(mc.textRenderer,itemStack,  (int) xItemOffset, (int) getPosY());
            } else {
                RenderSystem.setShaderColor(0.4f,0.4f,0.4f,0.35f);
                context.drawItem(itemStack, (int) xItemOffset, (int) getPosY());
                RenderSystem.setShaderColor(1f,1f,1f,1f);
                float offset = ((itemStack.getItem() instanceof ArmorItem ai) && ai.getSlotType() == EquipmentSlot.HEAD) ? -4 : 0;
                Render2DEngine.addWindow(context.getMatrices(), (int) xItemOffset, getPosY() + offset + (15 - offset) * ((float) itemStack.getDamage() / (float) itemStack.getMaxDamage()), xItemOffset + 15, getPosY() + 15, 1f);
                context.drawItem(itemStack, (int) xItemOffset, (int) getPosY());
                Render2DEngine.popWindow();
            }
            
            // Draw green bars beneath each armor piece
            if (showBars.getValue() && itemStack.getMaxDamage() > 0) {
                float durabilityPercent = ((float) (itemStack.getMaxDamage() - itemStack.getDamage()) / (float) itemStack.getMaxDamage());
                float barWidth = 15f; // Width of the bar
                float barHeight = 3f; // Height of the bar
                float barY = getPosY() + 17; // Position below the armor item
                
                // Determine bar color based on style
                Color barColor;
                switch (barStyle.getValue()) {
                    case Green:
                        barColor = Color.GREEN;
                        break;
                    case ColorCoded:
                        if (durabilityPercent > 0.75f) {
                            barColor = Color.GREEN;
                        } else if (durabilityPercent > 0.5f) {
                            barColor = Color.YELLOW;
                        } else if (durabilityPercent > 0.25f) {
                            barColor = Color.ORANGE;
                        } else {
                            barColor = Color.RED;
                        }
                        break;
                    case Rainbow:
                        // Rainbow effect based on durability
                        float hue = durabilityPercent * 0.33f; // Green to red
                        barColor = Color.getHSBColor(hue, 1.0f, 1.0f);
                        break;
                    default:
                        barColor = Color.GREEN;
                }
                
                // Draw the bar background (dark)
                Render2DEngine.drawRect(context.getMatrices(), xItemOffset, barY, barWidth, barHeight, new Color(0x333333));
                
                // Draw the filled portion of the bar
                float filledWidth = barWidth * durabilityPercent;
                if (filledWidth > 0) {
                    Render2DEngine.drawRect(context.getMatrices(), xItemOffset, barY, filledWidth, barHeight, barColor);
                }
            }
            
            // Display durability percentage
            if (showDurability.getValue() && itemStack.getMaxDamage() > 0) {
                int durability = (int) (((float) (itemStack.getMaxDamage() - itemStack.getDamage()) / (float) itemStack.getMaxDamage()) * 100);
                String durabilityText = durability + "%";
                
                // Color based on durability
                Color durabilityColor;
                if (durability > 75) {
                    durabilityColor = Color.GREEN;
                } else if (durability > 50) {
                    durabilityColor = Color.YELLOW;
                } else if (durability > 25) {
                    durabilityColor = Color.ORANGE;
                } else {
                    durabilityColor = Color.RED;
                }
                
                FontRenderers.getHUDFont().drawString(context.getMatrices(), durabilityText, xItemOffset + 2, getPosY() + 22, durabilityColor.getRGB());
            }
            
            xItemOffset += 20;
        }

        setBounds(getPosX(), getPosY(), 60, 35); // Updated bounds for new height
    }
}
