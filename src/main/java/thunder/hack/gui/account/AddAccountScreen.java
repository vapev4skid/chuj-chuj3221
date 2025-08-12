package thunder.hack.gui.account;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import thunder.hack.core.manager.client.AccountManager;

public class AddAccountScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget usernameField;
    private TextFieldWidget passwordField;
    private final AccountManager accountManager;

    public AddAccountScreen(Screen parent) {
        super(Text.literal("Add Account"));
        this.parent = parent;
        this.accountManager = new AccountManager();
    }

    @Override
    protected void init() {
        int y = this.height / 4 + 48;
        int fieldWidth = Math.min(this.width / 2, 200);

        // Username field
        this.usernameField = new TextFieldWidget(this.textRenderer, this.width / 2 - fieldWidth / 2, y, fieldWidth, 20, Text.literal(""));
        this.usernameField.setMaxLength(32);
        this.usernameField.setText("");
        this.addDrawableChild(this.usernameField);

        y += 24;

        // Password field
        this.passwordField = new TextFieldWidget(this.textRenderer, this.width / 2 - fieldWidth / 2, y, fieldWidth, 20, Text.literal(""));
        this.passwordField.setMaxLength(32);
        this.passwordField.setText("");
        this.addDrawableChild(this.passwordField);

        y += 24;

        // Add account button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Add"), button -> {
            String username = this.usernameField.getText();
            String password = this.passwordField.getText();
            if (!username.isEmpty()) {
                accountManager.addAccount(username, password);
                assert this.client != null;
                this.client.setScreen(parent);
            }
        }).dimensions(this.width / 2 - fieldWidth / 2, y, fieldWidth, 20).build());

        y += 24;

        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            assert this.client != null;
            this.client.setScreen(parent);
        }).dimensions(this.width / 2 - fieldWidth / 2, y, fieldWidth, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, "Username:", this.width / 2, this.height / 4 + 28, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Password:", this.width / 2, this.height / 4 + 76, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
