package thunder.hack.gui.mainmenu;

import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.NotNull;
import thunder.hack.injection.accesors.IMinecraftClient;
import thunder.hack.utility.render.Render2DEngine;

import java.util.Optional;

import static thunder.hack.features.modules.Module.mc;

public class AltManagerScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget nameField;
    private ButtonWidget loginButton;
    private ButtonWidget backButton;

    public AltManagerScreen(Screen parent) {
        super(Text.of("AltManager"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        nameField = new TextFieldWidget(textRenderer, centerX - 100, centerY - 30, 200, 20, Text.of("Nickname"));
        nameField.setPlaceholder(Text.of("Enter nickname"));
        nameField.setMaxLength(32);
        addSelectableChild(nameField);
        setInitialFocus(nameField);

        loginButton = ButtonWidget.builder(Text.of("Login"), button -> doLogin())
                .dimensions(centerX - 100, centerY + 0, 95, 20)
                .build();
        backButton = ButtonWidget.builder(Text.of("Back"), button -> close())
                .dimensions(centerX + 5, centerY + 0, 95, 20)
                .build();

        addDrawableChild(loginButton);
        addDrawableChild(backButton);
    }

    private void doLogin() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) return;
        try {
            Session session = new Session(name, Uuids.getOfflinePlayerUuid(name), "", Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);

            IMinecraftClient clientAccess = (IMinecraftClient) mc;
            clientAccess.setSessionT(session);
            mc.getGameProfile().getProperties().clear();

            UserApiService apiService = UserApiService.OFFLINE;
            clientAccess.setUserApiService(apiService);
            clientAccess.setSocialInteractionsManagerT(new SocialInteractionsManager(mc, apiService));
            clientAccess.setProfileKeys(ProfileKeys.create(apiService, session, mc.runDirectory.toPath()));
            clientAccess.setAbuseReportContextT(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), apiService));

            close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        float halfOfWidth = mc.getWindow().getScaledWidth() / 2f;
        float halfOfHeight = mc.getWindow().getScaledHeight() / 2f;

        float mainX = halfOfWidth - 140f;
        float mainY = halfOfHeight - 70f;
        float mainWidth = 280f;
        float mainHeight = 140f;

        Render2DEngine.drawHudBase(context.getMatrices(), mainX, mainY, mainWidth, mainHeight, 16, false);
        context.drawCenteredTextWithShadow(textRenderer, "ALT MANAGER", (int) (mainX + mainWidth / 2f), (int) (mainY + 8), 0xFFFFFFFF);

        super.render(context, mouseX, mouseY, delta);
        nameField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        mc.setScreen(parent);
    }
}


