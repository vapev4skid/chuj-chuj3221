package thunder.hack.features.modules.client;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Random;

public class AutoRegisterMe extends Module {
    private final Setting<Boolean> showPassword = new Setting<>("ShowPassword", true);
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public AutoRegisterMe() {
        super("AutoRegisterMe", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) return;

        String password = getRandomPassword(8);

        String registerCommand = "/register " + password + " " + password;

        mc.player.networkHandler.sendChatMessage(registerCommand);

        copyToClipboard(password);

        if (showPassword.getValue()) {
            mc.player.sendMessage(Text.literal("Hasło zarejestrowane: " + password), false);
        } else {
            mc.player.sendMessage(Text.literal("Hasło zostało zarejestrowane! Skopiowane do schowka."), false);
        }

        this.disable();
    }

    private String getRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }
}
