package thunder.hack.gui.font;

import org.jetbrains.annotations.NotNull;
import thunder.hack.ThunderHack;
import thunder.hack.features.modules.client.ClientSettings;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class FontRenderers {
    public static FontRenderer settings;
    public static FontRenderer modules;
    public static FontRenderer categories;
    public static FontRenderer icons;
    public static FontRenderer mid_icons;
    public static FontRenderer big_icons;
    public static FontRenderer thglitch;
    public static FontRenderer thglitchBig;
    public static FontRenderer monsterrat;
    public static FontRenderer sf_bold;
    public static FontRenderer sf_bold_mini;
    public static FontRenderer sf_bold_micro;
    public static FontRenderer sf_medium;
    public static FontRenderer sf_medium_mini;
    public static FontRenderer sf_medium_modules;
    public static FontRenderer minecraft;
    public static FontRenderer profont;

    public static FontRenderer getModulesRenderer() {
        return modules;
    }
    
    public static FontRenderer getSelectedFont(ClientSettings.FontType fontType) {
        return switch (fontType) {
            case SF_Medium -> sf_medium;
            case SF_Bold -> sf_bold;
            case SF_Bold_Mini -> sf_bold_mini;
            case SF_Bold_Micro -> sf_bold_micro;
            case SF_Medium_Mini -> sf_medium_mini;
            case SF_Medium_Modules -> sf_medium_modules;
            case Monsterrat -> monsterrat;
            case Minecraft -> minecraft;
            case ProFont -> profont;
            case Icons -> icons;
            case Mid_Icons -> mid_icons;
            case Big_Icons -> big_icons;
            case THGlitch -> thglitch;
            case THGlitchBig -> thglitchBig;
            case Settings -> sf_medium;
            case Categories -> sf_medium;
            case Modules -> sf_medium;
        };
    }
    
    public static FontRenderer getGUIFont() {
        return getSelectedFont(ClientSettings.guiFont.getValue());
    }
    
    public static FontRenderer getHUDFont() {
        return getSelectedFont(ClientSettings.hudFont.getValue());
    }
    
    public static FontRenderer getModuleFont() {
        return getSelectedFont(ClientSettings.moduleFont.getValue());
    }

    public static @NotNull FontRenderer create(float size, String name) throws IOException, FontFormatException {
        return new FontRenderer(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(ThunderHack.class.getClassLoader().getResourceAsStream("assets/thunderhack/fonts/" + name + ".ttf"))).deriveFont(Font.PLAIN, size / 2f), size / 2f);
    }
}
