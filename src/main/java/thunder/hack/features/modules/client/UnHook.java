package thunder.hack.features.modules.client;

import net.minecraft.SharedConstants;
import net.minecraft.client.util.Icons;
import net.minecraft.util.Formatting;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ConfigManager;
import thunder.hack.features.modules.Module;
import thunder.hack.utility.math.MathUtility;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

import static thunder.hack.features.modules.client.ClientSettings.isRu;

public class UnHook extends Module {
    public UnHook() {
        super("UnHook", Category.CLIENT);
    }

    List<Module> list;
    public int code = 0;

    @Override
    public void onEnable() {
        code = (int) MathUtility.random(10, 99);
        for (int i = 0; i < 20; i++)
            sendMessage(isRu() ? Formatting.RED + "Ща все свернется, напиши в чат " + Formatting.WHITE + code + Formatting.RED + " чтобы все вернуть!"
                    : Formatting.RED + "It's all close now, write to the chat " + Formatting.WHITE + code + Formatting.RED + " to return everything!");

        list = Managers.MODULE.getEnabledModules();

        mc.setScreen(null);

        Managers.ASYNC.run(() -> {
            mc.executeSync(() -> {
                for (Module module : list) {
                    if (module.equals(this))
                        continue;
                    module.disable();
                }
                ClientSettings.customMainMenu.setValue(false);

                try {
                    mc.getWindow().setIcon(mc.getDefaultResourcePack(), SharedConstants.getGameVersion().isStable() ? Icons.RELEASE : Icons.SNAPSHOT);
                } catch (Exception ignored) {
                }

                mc.inGameHud.getChatHud().clear(true);
                setEnabled(true);

                try {
                    ConfigManager.MAIN_FOLDER.renameTo(new File("XaeroWaypoints_BACKUP092738"));
                } catch (Exception ignored) {
                }
            });
        }, 5000);
    }

    @Override
    public void onDisable() {
        if (list == null)
            return;

        for (Module module : list) {
            if (module.equals(this))
                continue;
            module.enable();
        }
        ClientSettings.customMainMenu.setValue(true);

        try {
            new File("XaeroWaypoints_BACKUP092739").renameTo(new File("ThunderHackRecode"));
        } catch (Exception ignored) {
        }
    }
}
