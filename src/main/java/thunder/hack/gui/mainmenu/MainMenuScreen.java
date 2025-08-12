package thunder.hack.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.ThunderUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static thunder.hack.core.manager.client.TelemetryManager.getTotalPlayers;
import static thunder.hack.features.modules.Module.mc;

public class MainMenuScreen extends Screen {
    private final List<MainMenuButton> buttons = new ArrayList<>();
    public boolean confirm = false;
    public static int ticksActive;
    private static String cachedOnlineText = "online: 0";
    private static long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 5000; // Update every 5 seconds
    private static MainMenuScreen INSTANCE = new MainMenuScreen();

    protected MainMenuScreen() {
        super(Text.of("THMainMenuScreen"));
        INSTANCE = this;

        buttons.add(new MainMenuButton(-110, -70, I18n.translate("menu.singleplayer").toUpperCase(Locale.ROOT),
                () -> mc.setScreen(new SelectWorldScreen(this))));
        buttons.add(new MainMenuButton(4, -70, I18n.translate("menu.multiplayer").toUpperCase(Locale.ROOT),
                () -> mc.setScreen(new MultiplayerScreen(this))));
        buttons.add(new MainMenuButton(-110, -29, I18n.translate("menu.options")
                .toUpperCase(Locale.ROOT)
                .replace(".", ""), () -> mc.setScreen(new OptionsScreen(this, mc.options))));
        buttons.add(new MainMenuButton(4, -29, "CLICKGUI", () -> ModuleManager.clickGui.setGui()));
        buttons.add(new MainMenuButton(-110, 12, "ALT MANAGER", () -> mc.setScreen(new AltManagerScreen(this))));
        buttons.add(new MainMenuButton(4, 12, I18n.translate("menu.quit").toUpperCase(Locale.ROOT), mc::scheduleStop));

        updateOnlineText();
    }

    public static MainMenuScreen getInstance() {
        ticksActive = 0;
        if (INSTANCE == null) {
            INSTANCE = new MainMenuScreen();
        }
        return INSTANCE;
    }

    @Override
    public void tick() {
        ticksActive++;
        if (ticksActive > 400) ticksActive = 0;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > UPDATE_INTERVAL) {
            updateOnlineText();
            lastUpdateTime = currentTime;
        }
    }

    private static void updateOnlineText() {
        try {
            int totalPlayers = getTotalPlayers();
            cachedOnlineText = String.format("online: %s%s", Formatting.DARK_GREEN, totalPlayers);
        } catch (Exception e) {
            cachedOnlineText = "online: 0";
        }
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        float halfOfWidth = mc.getWindow().getScaledWidth() / 2f;
        float halfOfHeight = mc.getWindow().getScaledHeight() / 2f;

        float mainX = halfOfWidth - 120f;
        float mainY = halfOfHeight - 80f;
        float mainWidth = 240f;
        float mainHeight = 140;

        renderBackground(context, mouseX, mouseY, delta);
        Render2DEngine.drawHudBase(context.getMatrices(), mainX, mainY, mainWidth, mainHeight, 20);

        buttons.forEach(b -> b.onRender(context, mouseX, mouseY));

        boolean hoveredLogo = Render2DEngine.isHovered(mouseX, mouseY, (int) (halfOfWidth - 120), (int) (halfOfHeight - 130), 210, 50);

        FontRenderers.thglitchBig.drawCenteredString(context.getMatrices(), "NANOCORE", (int) (halfOfWidth),
                (int) (halfOfHeight - 120), new Color(255, 255, 255, hoveredLogo ? 230 : 180).getRGB());

        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(),
                "Version by: dsc.gg/jebieztymcodem", (int) (halfOfWidth), (int) (halfOfHeight - 130),
                new Color(255, 255, 255, hoveredLogo ? 200 : 150).getRGB());

        boolean hovered = Render2DEngine.isHovered(mouseX, mouseY, halfOfWidth - 50, halfOfHeight + 70, 100, 10);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "<-- Back to default menu", halfOfWidth,
                halfOfHeight + 70, hovered ? -1 : Render2DEngine.applyOpacity(-1, 0.6f));

        { // online count block
            String onlineUsers = cachedOnlineText;

            FontRenderers.sf_bold.drawCenteredString(context.getMatrices(), onlineUsers, halfOfWidth,
                    halfOfHeight * 2 - 15, Color.GREEN);

            context.getMatrices().push();
            context.getMatrices().translate(halfOfWidth - 10 - FontRenderers.sf_medium.getStringWidth(onlineUsers) / 2f,
                    halfOfHeight * 2 - 17, 0);
            Render2DEngine.drawBloom(context.getMatrices(), Render2DEngine.applyOpacity(Color.GREEN, 0.6f), 9f);
            context.getMatrices().pop();

            context.getMatrices().push();
            context.getMatrices().translate(halfOfWidth - 10 - FontRenderers.sf_medium.getStringWidth(onlineUsers) / 2f,
                    halfOfHeight * 2 - 17, 0);
            Render2DEngine.drawBloom(context.getMatrices(),
                    Render2DEngine.applyOpacity(Color.GREEN,
                            (float) (0.5f + (Math.sin((double) System.currentTimeMillis() / 500)) / 2f)),
                    9f);
            context.getMatrices().pop();
        }

        Render2DEngine.drawHudBase(context.getMatrices(), mc.getWindow().getScaledWidth() - 40,
                mc.getWindow().getScaledHeight() - 40, 30, 30, 5,
                Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 40,
                        mc.getWindow().getScaledHeight() - 40, 30, 30) ? 0.7f : 1f);
        RenderSystem.setShaderColor(1f, 1f, 1f,
                Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 40,
                        mc.getWindow().getScaledHeight() - 40, 30, 30) ? 0.7f : 1f);
        context.drawTexture(TextureStorage.thTeam, mc.getWindow().getScaledWidth() - 40,
                mc.getWindow().getScaledHeight() - 40, 30, 30, 0, 0, 30, 30, 30, 30);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        Render2DEngine.drawHudBase(context.getMatrices(), mc.getWindow().getScaledWidth() - 80,
                mc.getWindow().getScaledHeight() - 40, 30, 30, 5,
                Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 80,
                        mc.getWindow().getScaledHeight() - 40, 30, 30) ? 0.7f : 1f);
        RenderSystem.setShaderColor(1f, 1f, 1f,
                Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 80,
                        mc.getWindow().getScaledHeight() - 40, 30, 30) ? 0.7f : 1f);
        context.drawTexture(TextureStorage.donation, mc.getWindow().getScaledWidth() - 79,
                mc.getWindow().getScaledHeight() - 39, 28, 28, 0, 0, 30, 30, 30, 30);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        int offsetY = 10;
        for (String change : ThunderUtility.changeLog) {
            String prefix = getPrefix(change);
            FontRenderers.sf_medium.drawString(context.getMatrices(), prefix, 10, offsetY,
                    Render2DEngine.applyOpacity(-1, 0.4f));
            offsetY += 10;
        }
    }

    private static @NotNull String getPrefix(@NotNull String change) {
        String prefix = "";
        if (change.contains("[+]")) {
            change = change.replace("[+] ", "");
            prefix = Formatting.GREEN + "[+] " + Formatting.RESET;
        } else if (change.contains("[-]")) {
            change = change.replace("[-] ", "");
            prefix = Formatting.RED + "[-] " + Formatting.RESET;
        } else if (change.contains("[/]")) {
            change = change.replace("[/] ", "");
            prefix = Formatting.LIGHT_PURPLE + "[/] " + Formatting.RESET;
        } else if (change.contains("[*]")) {
            change = change.replace("[*] ", "");
            prefix = Formatting.GOLD + "[*] " + Formatting.RESET;
        }
        return prefix + change;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float halfOfWidth = mc.getWindow().getScaledWidth() / 2f;
        float halfOfHeight = mc.getWindow().getScaledHeight() / 2f;
        buttons.forEach(b -> b.onClick((int) mouseX, (int) mouseY));

        if (Render2DEngine.isHovered(mouseX, mouseY, halfOfWidth - 50, halfOfHeight + 70, 100, 10)) {
            confirm = true;
            mc.setScreen(new TitleScreen());
            confirm = false;
        }

        if (Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 40,
                mc.getWindow().getScaledHeight() - 40, 40, 40))
            mc.setScreen(CreditsScreen.getInstance());

        if (Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 90,
                mc.getWindow().getScaledHeight() - 40, 40, 40))
            Util.getOperatingSystem().open(URI.create(
                    "https://www.startpage.com/av/proxy-image?piurl=https%3A%2F%2Fplus.unsplash.com%2Fpremium_photo-1673529439739-9b882b1ca760%3Ffm%3Djpg%26q%3D60%26w%3D3000%26ixlib%3Drb-4.1.0%26ixid%3DM3wxMjA3fDB8MHxzZWFyY2h8OXx8ZmVldHxlbnwwfHwwfHx8MA%253D%253D&sp=1754936409Td4797bfb4fa20c5343e315be4721dcc84e5ec0f23ab3dc3e42934ab519920203"));

        if (Render2DEngine.isHovered(mouseX, mouseY, (int) (halfOfWidth - 157),
                (int) (halfOfHeight - 140), 300, 70))
            Util.getOperatingSystem().open(URI.create(
                    "https://www.startpage.com/av/proxy-image?piurl=https%3A%2F%2Fplus.unsplash.com%2Fpremium_photo-1673529439739-9b882b1ca760%3Ffm%3Djpg%26q%3D60%26w%3D3000%26ixlib%3Drb-4.1.0%26ixid%3DM3wxMjA3fDB8MHxzZWFyY2h8OXx8ZmVldHxlbnwwfHwwfHx8MA%253D%253D&sp=1754936409Td4797bfb4fa20c5343e315be4721dcc84e5ec0f23ab3dc3e42934ab519920203"));

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
