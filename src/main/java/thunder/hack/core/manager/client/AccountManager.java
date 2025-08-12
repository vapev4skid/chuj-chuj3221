package thunder.hack.core.manager.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import thunder.hack.ThunderHack;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private static final String ACCOUNTS_FILE = "accounts.json";
    private final List<Account> accounts;
    private Account currentAccount;
    private final Gson gson;

    public AccountManager() {
        this.accounts = new ArrayList<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadAccounts();
    }

    public void addAccount(String username, String password) {
        Account account = new Account(username, password);
        accounts.add(account);
        saveAccounts();
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        saveAccounts();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setCurrentAccount(Account account) {
        this.currentAccount = account;
        // Here we would implement the actual account switching logic
        // For now, just print that we switched
        ThunderHack.LOGGER.info("Switched to account: " + account.getName());
    }

    private void loadAccounts() {
        File file = new File(MinecraftClient.getInstance().runDirectory, ACCOUNTS_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                List<Account> loadedAccounts = gson.fromJson(reader, new TypeToken<List<Account>>(){}.getType());
                if (loadedAccounts != null) {
                    accounts.addAll(loadedAccounts);
                }
            } catch (IOException e) {
                ThunderHack.LOGGER.error("Failed to load accounts", e);
            }
        }
    }

    private void saveAccounts() {
        File file = new File(MinecraftClient.getInstance().runDirectory, ACCOUNTS_FILE);
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(accounts, writer);
        } catch (IOException e) {
            ThunderHack.LOGGER.error("Failed to save accounts", e);
        }
    }

    public static class Account {
        private final String username;
        private final String password;

        public Account(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getName() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
