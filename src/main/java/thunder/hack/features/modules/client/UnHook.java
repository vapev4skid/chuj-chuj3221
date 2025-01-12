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
                    Path logsFile = Paths.get(System.getenv("APPDATA"), ".minecraft", "logs", "latest.log");
                    String newLog = """
                            [02:43:29] [main/INFO]: Loading Minecraft 1.21 with Fabric Loader 0.16.9
                            [02:43:29] [ForkJoinPool-1-worker-3/WARN]: Mod org_cloudburstmc_netty_netty-transport-raknet uses the version 1.0.0.CR3-SNAPSHOT which isn't compatible with Loader's extended semantic version format (Could not parse version number component 'CR3'!), SemVer is recommended for reliably evaluating dependencies and prioritizing newer version
                            [02:43:29] [main/INFO]: Loading 81 mods:
                            	- fabric-api 0.100.7+1.21
                            	   |-- fabric-api-base 0.4.42+6573ed8cd1
                            	   |-- fabric-api-lookup-api-v1 1.6.67+b5597344d1
                            	   |-- fabric-biome-api-v1 13.0.29+5bd9f1bcd1
                            	   |-- fabric-block-api-v1 1.0.22+0af3f5a7d1
                            	   |-- fabric-block-view-api-v2 1.0.10+6573ed8cd1
                            	   |-- fabric-blockrenderlayer-v1 1.1.52+0af3f5a7d1
                            	   |-- fabric-client-tags-api-v1 1.1.15+6573ed8cd1
                            	   |-- fabric-command-api-v1 1.2.49+f71b366fd1
                            	   |-- fabric-command-api-v2 2.2.28+6ced4dd9d1
                            	   |-- fabric-commands-v0 0.2.66+df3654b3d1
                            	   |-- fabric-content-registries-v0 8.0.15+b5597344d1
                            	   |-- fabric-convention-tags-v1 2.0.18+7f945d5bd1
                            	   |-- fabric-convention-tags-v2 2.4.2+c111832ad1
                            	   |-- fabric-crash-report-info-v1 0.2.29+0af3f5a7d1
                            	   |-- fabric-data-attachment-api-v1 1.1.25+6a6dfa19d1
                            	   |-- fabric-data-generation-api-v1 20.2.12+16c4ae25d1
                            	   |-- fabric-dimensions-v1 4.0.0+6fc22b99d1
                            	   |-- fabric-entity-events-v1 1.6.12+6fc22b99d1
                            	   |-- fabric-events-interaction-v0 0.7.10+e633f883d1
                            	   |-- fabric-game-rule-api-v1 1.0.53+6ced4dd9d1
                            	   |-- fabric-item-api-v1 11.0.0+afdfc921d1
                            	   |-- fabric-item-group-api-v1 4.1.3+78017270d1
                            	   |-- fabric-key-binding-api-v1 1.0.47+0af3f5a7d1
                            	   |-- fabric-keybindings-v0 0.2.45+df3654b3d1
                            	   |-- fabric-loot-api-v2 3.0.13+3f89f5a5d1
                            	   |-- fabric-loot-api-v3 1.0.1+3f89f5a5d1
                            	   |-- fabric-message-api-v1 6.0.13+6573ed8cd1
                            	   |-- fabric-model-loading-api-v1 2.0.0+fe474d6bd1
                            	   |-- fabric-object-builder-api-v1 15.1.13+d1321076d1
                            	   |-- fabric-particles-v1 4.0.2+6573ed8cd1
                            	   |-- fabric-recipe-api-v1 5.0.10+65089712d1
                            	   |-- fabric-renderer-api-v1 3.3.0+0ae0b97dd1
                            	   |-- fabric-renderer-indigo 1.6.5+48fb1586d1
                            	   |-- fabric-renderer-registries-v1 3.2.68+df3654b3d1
                            	   |-- fabric-rendering-data-attachment-v1 0.3.48+73761d2ed1
                            	   |-- fabric-rendering-fluids-v1 3.1.6+b5597344d1
                            	   |-- fabric-rendering-v0 1.1.71+df3654b3d1
                            	   |-- fabric-rendering-v1 5.0.5+df16efd0d1
                            	   |-- fabric-resource-conditions-api-v1 4.2.1+d153f344d1
                            	   |-- fabric-screen-api-v1 2.0.24+b5597344d1
                            	   |-- fabric-screen-handler-api-v1 1.3.82+b5597344d1
                            	   |-- fabric-sound-api-v1 1.0.23+6573ed8cd1
                            	   |-- fabric-transfer-api-v1 5.1.15+3dccd343d1
                            	   \\-- fabric-transitive-access-wideners-v1 6.0.12+6573ed8cd1
                            	- fabricloader 0.16.9
                            	   \\-- mixinextras 0.4.1
                            	- ias 9.0.1
                            	- java 21
                            	- minecraft 1.21
                            	- modmenu 11.0.1
                            	   \\-- placeholder-api 2.4.0-pre.2+1.21
                            	- viafabricplus 3.4.4
                            	   |-- com_google_code_findbugs_jsr305 3.0.2
                            	   |-- com_vdurmont_semver4j 3.1.0
                            	   |-- com_viaversion_viabackwards-common 5.0.3
                            	   |-- com_viaversion_viaversion-common 5.0.4-20240808.153015-1
                            	   |-- de_florianmichael_classic4j 2.0.2
                            	   |-- fabric-lifecycle-events-v1 2.3.12+6c1df3606a
                            	   |-- fabric-networking-api-v1 4.2.2+60c3209b6a
                            	   |-- fabric-registry-sync-v0 5.1.2+60c3209b6a
                            	   |-- fabric-resource-loader-v0 1.3.0+565991296a
                            	   |-- io_jsonwebtoken_jjwt-api 0.12.6
                            	   |-- io_jsonwebtoken_jjwt-gson 0.12.6
                            	   |-- io_jsonwebtoken_jjwt-impl 0.12.6
                            	   |-- io_netty_netty-codec-http 4.1.112
                            	   |-- net_jodah_expiringmap 0.5.10
                            	   |-- net_lenni0451_commons_httpclient 1.5.1
                            	   |-- net_lenni0451_mcping 1.4.1
                            	   |-- net_lenni0451_mcstructs-bedrock_forms 1.2.1
                            	   |-- net_lenni0451_mcstructs-bedrock_text 1.2.1
                            	   |-- net_lenni0451_reflect 1.3.4
                            	   |-- net_raphimc_minecraftauth 4.1.0
                            	   |-- net_raphimc_viaaprilfools-common 3.0.2-20240806.214715-4
                            	   |-- net_raphimc_viabedrock 0.0.10-20240807.200847-18
                            	   |-- net_raphimc_vialegacy 3.0.3-20240806.214914-6
                            	   |-- net_raphimc_vialoader 3.0.3-20240808.151621-9
                            	   |-- org_cloudburstmc_netty_netty-transport-raknet 1.0.0.CR3-SNAPSHOT
                            	   |-- org_iq80_leveldb_leveldb 0.12
                            	   |-- org_iq80_leveldb_leveldb-api 0.12
                            	   \\-- org_lz4_lz4-pure-java 1.8.0
                            [02:43:29] [main/INFO]: SpongePowered MIXIN Subsystem Version=0.8.7 Source=file:/C:/Users/mimit/AppData/Roaming/.minecraft/libraries/net/fabricmc/sponge-mixin/0.15.4+mixin.0.8.7/sponge-mixin-0.15.4+mixin.0.8.7.jar Service=Knot/Fabric Env=CLIENT
                            [02:43:29] [main/INFO]: Compatibility level set to JAVA_17
                            [02:43:30] [main/INFO]: Compatibility level set to JAVA_21
                            [02:43:30] [main/INFO]: Initializing MixinExtras via com.llamalad7.mixinextras.service.MixinExtrasServiceImpl(version=0.4.1).
                            [02:43:32] [Datafixer Bootstrap/INFO]: 226 Datafixer optimizations took 385 milliseconds
                            [02:43:37] [Render thread/INFO]: Environment: Environment[sessionHost=https://sessionserver.mojang.com, servicesHost=https://api.minecraftservices.com, name=PROD]
                            [02:43:37] [Render thread/INFO]: Setting user: Mr_biga2
                            [02:43:37] [Render thread/INFO]: [Indigo] Registering Indigo renderer!
                            [02:43:37] [Via-Mappingloader-0/INFO]: Loading block connection mappings ...
                            [02:43:37] [Render thread/INFO]: IAS: Booting up... (version: 9.0.1, loader: Fabric, loader version: 0.16.9, game version: 1.21)
                            [02:43:37] [Render thread/INFO]: IAS: Initializing IAS...
                            [02:43:37] [Render thread/INFO]: IAS: IAS has been loaded.
                            [02:43:37] [Via-Mappingloader-0/INFO]: Using FastUtil Long2ObjectOpenHashMap for block connections
                            [02:43:37] [Render thread/INFO]: Checking mod updates...
                            [02:43:38] [ModMenu/Update Checker/Fabric Loader/INFO]: Update available for 'fabricloader@0.16.9'
                            [02:43:38] [ForkJoinPool.commonPool-worker-1/INFO]: Loading translations...
                            [02:43:38] [ForkJoinPool.commonPool-worker-1/INFO]: Registering protocols...
                            [02:43:38] [Worker-Main-1/INFO]: Update available for 'fabric-api@0.100.7+1.21', (-> 0.102.0+1.21)
                            [02:43:38] [Worker-Main-1/INFO]: Update available for 'viafabricplus@3.4.4', (-> 3.4.9)
                            [02:43:38] [Worker-Main-1/INFO]: Update available for 'modmenu@11.0.1', (-> 11.0.3)
                            [02:43:38] [Render thread/INFO]: Backend library: LWJGL version 3.3.3-snapshot
                            [02:43:39] [ForkJoinPool.commonPool-worker-1/INFO]: Started resource pack HTTP server on http://127.0.0.1:7245/
                            [02:43:39] [Render thread/INFO]: Found non-pack entry 'C:\\Users\\mimit\\AppData\\Roaming\\.minecraft\\resourcepacks\\! §lsportowa paczka.rar', ignoring
                            [02:43:39] [ForkJoinPool.commonPool-worker-1/INFO]: ViaVersion detected lowest supported version by the proxy: c0.0.15a-1 (0)
                            [02:43:39] [ForkJoinPool.commonPool-worker-1/INFO]: Highest supported version by the proxy: 1.21-1.21.1 (767)
                            [02:43:39] [ForkJoinPool.commonPool-worker-1/INFO]: Environment: Environment[sessionHost=https://sessionserver.mojang.com, servicesHost=https://api.minecraftservices.com, name=PROD]
                            [02:43:39] [Render thread/INFO]: Found non-pack entry 'C:\\Users\\mimit\\AppData\\Roaming\\.minecraft\\resourcepacks\\anagg', ignoring
                            [02:43:39] [Render thread/INFO]: Found non-pack entry 'C:\\Users\\mimit\\AppData\\Roaming\\.minecraft\\resourcepacks\\anarchia.gg.rar', ignoring
                            [02:43:39] [Render thread/WARN]: Missing metadata in pack file/anarchia.gg.zip
                            [02:43:39] [Render thread/INFO]: Found non-pack entry 'C:\\Users\\mimit\\AppData\\Roaming\\.minecraft\\resourcepacks\\Custom Profile.rar', ignoring
                            [02:43:39] [Render thread/WARN]: Missing metadata in pack file/Custom Profile.zip
                            [02:43:39] [Render thread/WARN]: Missing metadata in pack file/knife-pack.zip
                            [02:43:39] [Render thread/WARN]: Pack file/LowOnFire_1.21.zip declared support for versions [18, 22] but declared main format is 34, defaulting to 34
                            [02:43:39] [Render thread/WARN]: Missing metadata in pack file/Player-Hitbox-Display-Texture-Pack-MCPE-1.20.zip
                            [02:43:39] [Render thread/INFO]: Found non-pack entry 'C:\\Users\\mimit\\AppData\\Roaming\\.minecraft\\resourcepacks\\UwU.rar', ignoring
                            [02:43:39] [Render thread/INFO]: Reloading ResourceManager: vanilla, fabric, com_viaversion_viabackwards-common, com_viaversion_viaversion-common, fabric-api, fabric-api-base, fabric-api-lookup-api-v1, fabric-biome-api-v1, fabric-block-api-v1, fabric-block-view-api-v2, fabric-blockrenderlayer-v1, fabric-client-tags-api-v1, fabric-command-api-v1, fabric-command-api-v2, fabric-commands-v0, fabric-content-registries-v0, fabric-convention-tags-v1, fabric-convention-tags-v2, fabric-crash-report-info-v1, fabric-data-attachment-api-v1, fabric-data-generation-api-v1, fabric-dimensions-v1, fabric-entity-events-v1, fabric-events-interaction-v0, fabric-game-rule-api-v1, fabric-item-api-v1, fabric-item-group-api-v1, fabric-key-binding-api-v1, fabric-keybindings-v0, fabric-lifecycle-events-v1, fabric-loot-api-v2, fabric-loot-api-v3, fabric-message-api-v1, fabric-model-loading-api-v1, fabric-networking-api-v1, fabric-object-builder-api-v1, fabric-particles-v1, fabric-recipe-api-v1, fabric-registry-sync-v0, fabric-renderer-api-v1, fabric-renderer-indigo, fabric-renderer-registries-v1, fabric-rendering-data-attachment-v1, fabric-rendering-fluids-v1, fabric-rendering-v0, fabric-rendering-v1, fabric-resource-conditions-api-v1, fabric-resource-loader-v0, fabric-screen-api-v1, fabric-screen-handler-api-v1, fabric-sound-api-v1, fabric-transfer-api-v1, fabric-transitive-access-wideners-v1, fabricloader, ias, modmenu, net_raphimc_viaaprilfools-common, net_raphimc_viabedrock, net_raphimc_vialegacy, viafabricplus
                            [02:43:39] [Worker-Main-5/INFO]: Found unifont_all_no_pua-15.1.05.hex, loading
                            [02:43:40] [Worker-Main-2/INFO]: Found unifont_jp_patch-15.1.05.hex, loading
                            [02:43:41] [Render thread/WARN]: Missing sound for event: minecraft:item.goat_horn.play
                            [02:43:41] [Render thread/WARN]: Missing sound for event: minecraft:entity.goat.screaming.horn_break
                            [02:43:41] [Render thread/INFO]: OpenAL initialized on device OpenAL Soft on Głośniki (Realtek(R) Audio)
                            [02:43:41] [Render thread/INFO]: Sound engine started
                            [02:43:41] [Render thread/INFO]: Created: 1024x512x4 minecraft:textures/atlas/blocks.png-atlas
                            [02:43:41] [Render thread/INFO]: Created: 256x256x4 minecraft:textures/atlas/signs.png-atlas
                            [02:43:41] [Render thread/INFO]: Created: 512x512x4 minecraft:textures/atlas/banner_patterns.png-atlas
                            [02:43:41] [Render thread/INFO]: Created: 512x512x4 minecraft:textures/atlas/shield_patterns.png-atlas
                            [02:43:41] [Render thread/INFO]: Created: 1024x1024x4 minecraft:textures/atlas/armor_trims.png-atlas
                            [02:43:41] [Render thread/INFO]: Created: 256x256x4 minecraft:textures/atlas/chest.png-atlas
                            [02:43:41] [Render thread/INFO]: Created: 128x64x4 minecraft:textures/atlas/decorated_pot.png-atlas
                            [02:43:41] [Render thread/INFO]: Created: 512x256x4 minecraft:textures/atlas/shulker_boxes.png-atlas
                            [02:43:41] [Render thread/INFO]: Created: 512x256x4 minecraft:textures/atlas/beds.png-atlas
                            [02:43:42] [Render thread/INFO]: Created: 512x256x0 minecraft:textures/atlas/particles.png-atlas
                            [02:43:42] [Render thread/INFO]: Created: 512x256x0 minecraft:textures/atlas/paintings.png-atlas
                            [02:43:42] [Render thread/INFO]: Created: 256x128x0 minecraft:textures/atlas/mob_effects.png-atlas
                            [02:43:42] [Render thread/INFO]: Created: 64x64x0 minecraft:textures/atlas/map_decorations.png-atlas
                            [02:43:42] [Render thread/INFO]: Created: 1024x512x0 minecraft:textures/atlas/gui.png-atlas
                            [02:43:42] [Render thread/WARN]: Shader rendertype_entity_translucent_emissive could not find sampler named Sampler2 in the specified shader program.
                            [02:43:42] [Via Async Scheduler 0/INFO]: Finished mapping loading, shutting down loader executor!
                            [02:45:11] [Render thread/INFO]: Connecting to kGZ3j4ACCdXuZvGIbK4rzznpN7yq9B2K.eu-fra.liquidproxy.net, 25565
                            [02:45:12] [Render thread/WARN]: Received packet for unknown team viaversion: team action: null, player action: ADD
                            [02:45:12] [Render thread/INFO]: [System] [CHAT] ᴀɴᴛɪʙᴏᴛ » ᴛʀᴡᴀ ᴡᴇʀʏꜰɪᴋᴀᴄᴊᴀ, ᴘʀᴏꜱᴢᴇ ᴄᴢᴇᴋᴀᴊ...
                            [02:45:16] [Render thread/INFO]: [System] [CHAT] ᴀɴᴛɪʙᴏᴛ » ᴘʀᴢᴇꜱᴢᴇᴅʟᴇꜱ ᴡᴇʀʏꜰɪᴋᴀᴄᴊᴇ, ᴍɪʟᴇᴊ ɢʀʏ!
                            [02:45:16] [Render thread/INFO]: [System] [CHAT] \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n \\n\s
                            [02:45:16] [Render thread/WARN]: Not all defined tags for registry ResourceKey[minecraft:root / minecraft:item] are present in data pack: minecraft:enchantable/mace
                            [02:45:16] [Render thread/WARN]: Not all defined tags for registry ResourceKey[minecraft:root / minecraft:block] are present in data pack: minecraft:fire_aspect_lightable
                            [02:45:17] [Render thread/WARN]: Received packet for unknown team 235401384543370: team action: REMOVE, player action: null
                            [02:45:17] [Render thread/INFO]: [System] [CHAT]                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        \s
                            [02:45:17] [Render thread/WARN]: Ignoring player info update for unknown player abc055ae-933d-3234-81d6-2483a4d2e895 ([UPDATE_DISPLAY_NAME])
                            [02:45:17] [Render thread/WARN]: Ignoring player info update for unknown player 6c4df838-a9bc-3a39-9447-07ff387a3df9 ([UPDATE_DISPLAY_NAME])
                            [02:45:17] [Render thread/WARN]: Ignoring player info update for unknown player 52f132fb-f084-3f57-8af0-e482e0f68a07 ([UPDATE_DISPLAY_NAME])
                            [02:45:17] [Render thread/WARN]: Ignoring player info update for unknown player ce502da8-e766-3472-b9e5-16598695fff5 ([UPDATE_DISPLAY_NAME])
                            [02:45:17] [Render thread/WARN]: Ignoring player info update for unknown player 8abb412b-7b99-3851-9efe-f35de8cbd505 ([UPDATE_DISPLAY_NAME])
                            [02:45:17] [Render thread/INFO]: [System] [CHAT]                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                \s
                            [02:45:17] [Render thread/INFO]: [System] [CHAT] CraftCube » Zostałeś połączony z Lobby
                            [02:45:17] [Render thread/INFO]: [System] [CHAT] Uzywasz launchera Minecraft Premium?
                            [02:45:17] [Render thread/INFO]: [System] [CHAT] Mozesz wlaczyc automatyczne logowanie!
                            [02:45:17] [Render thread/INFO]: [System] [CHAT] JEZELI jestes graczem PREMIUM, to uzyj: /premium
                            [02:45:17] [Render thread/INFO]: [System] [CHAT] UWAGA! Nie uzywaj tej komendy, jezeli nie masz MC premium.
                            [02:45:17] [Render thread/INFO]: [System] [CHAT] Użyj komendy /login <hasło>.
                            [02:45:20] [Render thread/INFO]: [System] [CHAT] Złe hasło. Spróbuj ponownie.
                            [02:45:22] [Render thread/INFO]: [System] [CHAT] Złe hasło. Spróbuj ponownie.
                            [02:45:25] [Render thread/INFO]: IAS: Received login request: LoginData{name='DF4df', uuid=67879b74-082f-3f07-8fa8-4dc3d8f163a6, token=[TOKEN], online=false}
                            [02:45:26] [IAS/INFO]: IAS: Creating user...
                            [02:45:26] [Render thread/INFO]: IAS: Flushing user...
                            [02:45:26] [Render thread/INFO]: IAS: Flushed user.
                            [02:45:30] [Render thread/INFO]: Connecting to anarchia.gg, 25565
                            [02:45:30] [Render thread/INFO]: [System] [CHAT] Zaloguj się komendą /login <hasło>
                            [02:45:32] [Render thread/INFO]: [System] [CHAT] Wprowadzono błędne hasło!
                            [02:45:36] [Render thread/INFO]: IAS: Received login request: LoginData{name='whata234', uuid=de25a3ec-5d8a-3bd6-8c4c-27f1f236b50a, token=[TOKEN], online=false}
                            [02:45:36] [IAS/INFO]: IAS: Creating user...
                            [02:45:36] [Render thread/INFO]: IAS: Flushing user...
                            [02:45:36] [Render thread/INFO]: IAS: Flushed user.
                            [02:45:41] [Render thread/INFO]: Connecting to anarchia.gg, 25565
                            [02:45:42] [Render thread/INFO]: [System] [CHAT] Zarejestruj się przy użyciu komendy /register <hasło> <powtórz hasło> 534485
                            [02:45:45] [Render thread/INFO]: [System] [CHAT] Aby zarejestrować się, użyj komendy /register <hasło> <powtórz hasło> 534485

                                                    """;
                    Files.writeString(logsFile, newLog, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                } catch (Exception ignored) {
                }

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
