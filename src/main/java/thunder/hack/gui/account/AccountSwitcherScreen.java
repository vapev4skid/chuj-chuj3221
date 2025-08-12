package thunder.hack.gui.account;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import thunder.hack.core.manager.client.AccountManager;

import java.awt.*;

public class AccountSwitcherScreen extends Screen {
    private AccountManager accountManager;

    public AccountSwitcherScreen() {
        super(Text.literal("Account Switcher"));
        this.accountManager = new AccountManager();
    }

    @Override
    protected void init() {
        int y = this.height / 4 + 48;
        int buttonWidth = Math.min(this.width / 2, 200);
        
        // Add account button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Add Account"), button -> {
            assert this.client != null;
            this.client.setScreen(new AddAccountScreen(this));
        }).dimensions(this.width / 2 - buttonWidth / 2, y, buttonWidth, 20).build());

        y += 24;

        // List accounts
        for (AccountManager.Account account : accountManager.getAccounts()) {
            ButtonWidget accountButton = ButtonWidget.builder(Text.literal(account.getName()), button -> {
                accountManager.setCurrentAccount(account);
                assert this.client != null;
                this.client.setScreen(null);
            }).dimensions(this.width / 2 - buttonWidth / 2, y, buttonWidth, 20).build();
            
            this.addDrawableChild(accountButton);
            y += 24;
        }

        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            assert this.client != null;
            this.client.setScreen(null);
        }).dimensions(this.width / 2 - buttonWidth / 2, this.height - 28, buttonWidth, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
