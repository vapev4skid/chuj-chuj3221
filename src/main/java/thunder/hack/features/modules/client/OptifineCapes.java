package thunder.hack.features.modules.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import thunder.hack.features.modules.Module;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Author: @dragonostic
 * Adapted for NanoCore client capes
 */
public final class OptifineCapes extends Module {

    public OptifineCapes() {
        super("NanoCoreCapes", Category.CLIENT);
    }

    public interface ReturnCapeTexture {
        void response(Identifier id);
    }

    public static void loadPlayerCape(GameProfile player, ReturnCapeTexture response) {
        try {
            String uuid = player.getId().toString();
            NativeImageBackedTexture nIBT = getCapeFromURL(
                    String.format("http://s.optifine.net/capes/%s.png", player.getName())
            );
            if (nIBT != null) {
                Identifier capeTexture = MinecraftClient.getInstance()
                        .getTextureManager()
                        .registerDynamicTexture("th-cape-" + uuid, nIBT);
                response.response(capeTexture);
            }
        } catch (Exception ignored) {
        }
    }

    public static NativeImageBackedTexture getCapeFromURL(String capeStringURL) {
        try {
            URL capeURL = new URL(capeStringURL);
            return getCapeFromStream(capeURL.openStream());
        } catch (IOException e) {
            return null;
        }
    }

    public static NativeImageBackedTexture getCapeFromStream(InputStream image) {
        try {
            NativeImage cape = NativeImage.read(image);
            if (cape != null) {
                return new NativeImageBackedTexture(parseCape(cape));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NativeImage parseCape(NativeImage image) {
        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        // Ensure power-of-two scaling
        for (int imageSrcHeight = srcHeight; imageWidth < imageSrcWidth || imageHeight < imageSrcHeight; imageHeight *= 2) {
            imageWidth *= 2;
        }

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.setColor(x, y, image.getColor(x, y));
            }
        }
        image.close();
        return imgNew;
    }
}
