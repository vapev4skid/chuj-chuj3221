package thunder.hack.features.modules.client;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

import thunder.hack.ThunderHack;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.discord.DiscordEventHandlers;
import thunder.hack.utility.discord.DiscordRPC;
import thunder.hack.utility.discord.DiscordRichPresence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static thunder.hack.features.modules.client.ClientSettings.isRu;

public final class RPC extends Module {
    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;

    // Discord ID as a simple static final string, not a Setting
    public static final String DISCORD_ID = "1404590768928194701";

    public static Setting<Mode> mode = new Setting<>("Picture", Mode.NanoCore);
    public static Setting<Boolean> showIP = new Setting<>("ShowIP", true);
    public static Setting<sMode> smode = new Setting<>("StateMode", sMode.Stats);
    public static Setting<Boolean> nickname = new Setting<>("Nickname", true);
    public static Setting<String> smallIconUrl = new Setting<>("Small Icon URL", "https://i.imgur.com/J6ByyBQ.png");
    public static Setting<String> state = new Setting<>("CustomState", "Your custom state here");

    public static DiscordRichPresence presence = new DiscordRichPresence();
    public static boolean started;
    private final Timer timer_delay = new Timer();
    private static Thread thread;

    String slov;
    String[] rpc_perebor_en = {
        "dsc.gg/jebieztymcodem", "dsc.gg/jebieztymcodem", "dsc.gg/jebieztymcodem",
        "dsc.gg/jebieztymcodem", "dsc.gg/jebieztymcodem", "dsc.gg/jebieztymcodem",
        "Beta Tester:V6.1"
    };
    String[] rpc_perebor_ru = {
        "Паркурит", "Репортит читеров", "Трогает траву",
        "Спрашивает как забиндить", "Репортит баги", "dsc.gg/jebieztymcodem",
        "Beta Tester:V6.1"
    };
    int randomInt;

    public RPC() {
        super("DiscordRPC", Category.CLIENT);
    }

    public static void WriteFile(String url1, String url2) {
        File file = new File("ThunderHackRecode/misc/RPC.txt");
        try {
            if (file.createNewFile() || file.exists()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(url1 + "SEPARATOR" + url2 + '\n');
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDisable() {
        started = false;
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }

    @Override
    public void onUpdate() {
        startRpc();
    }

    @Override
    public void onEnable() {
        started = false;
        if (thread != null) {
            thread.interrupt();
        }
        startRpc();
    }

    public void startRpc() {
        if (isDisabled()) return;

        try {
            if (mc.getSession() != null && "irek1jest".equalsIgnoreCase(mc.getSession().getUsername())) {
                return;
            }
        } catch (Throwable ignored) {}

        if (!started) {
            started = true;
            DiscordEventHandlers handlers = new DiscordEventHandlers();

            // Use the plain Discord ID constant here
            rpc.Discord_Initialize(DISCORD_ID, handlers, true, "");

            presence.startTimestamp = System.currentTimeMillis() / 1000L;
            presence.largeImageText = "Nano Core";
            rpc.Discord_UpdatePresence(presence);

            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    rpc.Discord_RunCallbacks();

                    presence.details = getDetails();

                    sMode currentMode = smode.getValue();
                    if (currentMode == sMode.Stats) {
                        presence.state = "Using dsc.gg/jebieztymcodem";
                    } else if (currentMode == sMode.Custom) {
                        presence.state = state.getValue();
                    } else if (currentMode == sMode.Version) {
                        presence.state = "v" + ThunderHack.VERSION + " for mc 1.21";
                    }

                    if (nickname.getValue()) {
                        presence.smallImageText = "Beta Tester";
                        presence.smallImageKey = smallIconUrl.getValue();
                    } else {
                        presence.smallImageText = "";
                        presence.smallImageKey = "";
                    }

                    presence.button_label_1 = "Download";
                    presence.button_url_1 = "https://discord.gg/xmTwmVNVtq";

                    Mode currentImageMode = mode.getValue();
                    if (currentImageMode == Mode.NanoCore) {
                        presence.largeImageKey = "https://raw.githubusercontent.com/MimiToKOX/ThunerHack-Exploitcore/main/assets/1.png";
                    } else if (currentImageMode == Mode.Recode) {
                        presence.largeImageKey = "https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExejY0c3N4b3I4ODEzdXF5cTN2OHdqNHZ2Zmo1amptdGQ2OGpmemdmcSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/4N7gM8jw0UasIuaXWl/giphy.gif";
                    } else if (currentImageMode == Mode.MegaCute) {
                        presence.largeImageKey = "https://c.tenor.com/bmKJSPw2JCoAAAAC/tenor.gif";
                    } else if (currentImageMode == Mode.Custom) {
                        presence.largeImageKey = "https://media.tenor.com/Lu3ZB5FTDdwAAAAi/duong2.gif";
                    }

                    rpc.Discord_UpdatePresence(presence);

                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "TH-RPC-Handler");
            thread.start();
        }
    }

    private String getDetails() {
        String result = "";

        if (mc.currentScreen instanceof MultiplayerScreen
                || mc.currentScreen instanceof AddServerScreen
                || mc.currentScreen instanceof TitleScreen) {
            if (timer_delay.passedMs(60 * 1000)) {
                randomInt = (int) (Math.random() * (rpc_perebor_en.length));
                slov = isRu() ? rpc_perebor_ru[randomInt] : rpc_perebor_en[randomInt];
                timer_delay.reset();
            }
            result = slov;

        } else if (mc.isInSingleplayer()) {
            result = isRu() ? "Читерит в одиночке" : "SinglePlayer hacker";
        }

        return result;
    }

    public enum Mode {
        Custom, MegaCute, Recode, NanoCore
    }

    public enum sMode {
        Custom, Stats, Version
    }
}
