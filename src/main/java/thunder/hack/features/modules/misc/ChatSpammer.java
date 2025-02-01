package thunder.hack.features.modules.misc;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import java.util.Random;

public class ChatSpammer extends Module {
    private final Setting<Integer> spamDaily = new Setting<>("SpamDaily", 10, 0, 60);
    private final Setting<Boolean> antiDetected = new Setting<>("Anti-Detected", true);
    private final Setting<String> spamText = new Setting<>("Text", "free komar + bypas --> dsc.gg/exploitcore");

    private long lastSpamTime = 0;

    public ChatSpammer() {
        super("ChatSpammer", Category.MISC);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpamTime >= spamDaily.getValue() * 1000L) {
            String message = spamText.getValue();

            if (antiDetected.getValue()) {
                message =  message + " [" + getRandomString(8) + "]";
            }

            mc.player.networkHandler.sendChatMessage(message);

            lastSpamTime = currentTime;
        }
    }

    private String getRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomString.toString();
    }
}
