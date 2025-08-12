package thunder.hack.features.modules.client;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;

import java.awt.*;

public final class ClientSettings extends Module {
    public static Setting<Boolean> futureCompatibility = new Setting<>("FutureCompatibility", false);
    public static Setting<Boolean> customMainMenu = new Setting<>("CustomMainMenu", true);
    public static Setting<Boolean> customPanorama = new Setting<>("CustomPanorama", true);
    public static Setting<Boolean> customLoadingScreen = new Setting<>("CustomLoadingScreen", true);
    public static Setting<Boolean> scaleFactorFix = new Setting<>("ScaleFactorFix", false);
    public static Setting<Float> scaleFactorFixValue = new Setting<>("ScaleFactorFixValue", 2f, 0f, 4f);
    public static Setting<Boolean> renderRotations = new Setting<>("RenderRotations", true);
    public static Setting<Boolean> clientMessages = new Setting<>("ClientMessages", true);
    public static Setting<Boolean> debug = new Setting<>("Debug", false);
    public static Setting<Boolean> customBob = new Setting<>("CustomBob", true);
    public static Setting<Boolean> telemetry = new Setting<>("Telemetry", true);
    public static Setting<Language> language = new Setting<>("Language", Language.ENG);
    public static Setting<String> prefix = new Setting<>("Prefix", "@");
    public static Setting<ClipCommandMode> clipCommandMode = new Setting<>("ClipCommandMode", ClipCommandMode.Matrix);
    public static Setting<ColorSetting> auraESPColor = new Setting<>("AuraESPColor", new ColorSetting(new Color(255, 0, 0, 255)));
    
    // Font settings
    public static Setting<FontType> guiFont = new Setting<>("GUIFont", FontType.SF_Medium);
    public static Setting<FontType> hudFont = new Setting<>("HUDFont", FontType.SF_Bold);
    public static Setting<FontType> moduleFont = new Setting<>("ModuleFont", FontType.Modules);

    public enum Language {
        RU,
        ENG
    }

    public enum ClipCommandMode {
        Default,
        Matrix
    }
    
    public enum FontType {
        SF_Medium,
        SF_Bold,
        SF_Bold_Mini,
        SF_Bold_Micro,
        SF_Medium_Mini,
        SF_Medium_Modules,
        Monsterrat,
        Minecraft,
        ProFont,
        Icons,
        Mid_Icons,
        Big_Icons,
        THGlitch,
        THGlitchBig,
        Settings,
        Categories,
        Modules
    }

    public ClientSettings() {
        super("ClientSettings", Category.CLIENT);
    }

    public static boolean isRu() {
        return language.is(Language.RU);
    }

    @Override
    public boolean isToggleable() {
        return false;
    }
}
