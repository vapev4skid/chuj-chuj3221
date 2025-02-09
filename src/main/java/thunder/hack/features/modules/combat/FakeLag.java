package thunder.hack.features.modules.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.core.manager.client.NotificationManager;
import thunder.hack.events.impl.EventTick;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.notification.Notification;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.Bind;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.player.PlayerEntityCopy;
import thunder.hack.utility.render.Render3DEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static thunder.hack.features.modules.client.ClientSettings.isRu;

public class FakeLag extends Module {
    public FakeLag() {
        super("FakeLag", Category.MOVEMENT);
    }

    private final Setting<Boolean> pulse = new Setting<>("Pulse", false);
    private final Setting<Integer> pulsePackets = new Setting<>("PulsePackets", 20, 1, 1000, v -> pulse.getValue());
    private final Setting<Boolean> render = new Setting<>("Render", true);
    private final Setting<Boolean> disableOnVelocity = new Setting<>("DisableOnVelocity", false);
    private final Setting<RenderMode> renderMode = new Setting<>("Render Mode", RenderMode.Circle, value -> render.getValue());
    private final Setting<ColorSetting> circleColor = new Setting<>("Color", new ColorSetting(0xFFda6464), value -> render.getValue() && renderMode.getValue() == RenderMode.Circle || renderMode.getValue() == RenderMode.Both);
    private final Setting<Bind> cancel = new Setting<>("Cancel", new Bind(GLFW.GLFW_KEY_LEFT_SHIFT, false, false));
    private long stopPacketsUntil = 0;
    private long disablePulseUntil = 0;
    private boolean isSending = false;

    private final ConcurrentHashMap<KeepAliveC2SPacket, Long> packets = new ConcurrentHashMap<>();


    private enum RenderMode {
        Circle,
        Model,
        Both
    }

    private PlayerEntityCopy blinkPlayer;
    public static Vec3d lastPos = Vec3d.ZERO;
    private Vec3d prevVelocity = Vec3d.ZERO;
    private float prevYaw = 0;
    private boolean prevSprinting = false;
    private final Queue<Packet<?>> storedPackets = new LinkedList<>();
    private final Queue<Packet<?>> storedTransactions = new LinkedList<>();
    private final AtomicBoolean sending = new AtomicBoolean(false);

    @Override
    public void onEnable() {
        if (mc.player == null
                || mc.world == null
                || mc.isIntegratedServerRunning()
                || mc.getNetworkHandler() == null) {
            disable();
            return;
        }

        storedTransactions.clear();
        lastPos = mc.player.getPos();
        prevVelocity = mc.player.getVelocity();
        prevYaw = mc.player.getYaw();
        prevSprinting = mc.player.isSprinting();
        mc.world.spawnEntity(new ClientPlayerEntity(mc, mc.world, mc.getNetworkHandler(), mc.player.getStatHandler(), mc.player.getRecipeBook(), mc.player.lastSprinting, mc.player.isSneaking()));
        sending.set(false);
        storedPackets.clear();
    }


    @Override
    public void onDisable() {
        if (mc.world == null || mc.player == null) return;

        while (!storedPackets.isEmpty())
            sendPacket(storedPackets.poll());

        if (blinkPlayer != null) blinkPlayer.deSpawn();
        blinkPlayer = null;
    }

    private void sendPacketSafely(Packet<?> packet) {
        if (isSending) return;
        isSending = true;
        sendPacket(packet);
        isSending = false;
    }

    @Override
    public String getDisplayInfo() {
        return Integer.toString(storedPackets.size());
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket) {
            EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) event.getPacket();

            if (packet.getId() == mc.player.getId()) {
                if (disableOnVelocity.getValue()) {
                    Managers.NOTIFICATION.publicity("[FakeLag] ", "velocity detected, flushing packets", 2, Notification.Type.WARNING);
                    sendPackets();
                }

                event.cancel();
                mc.player.setVelocity(0, 0, 0);
            }
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (fullNullCheck()) return;

        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            event.cancel();

            PlayerMoveC2SPacket movePacket = new PlayerMoveC2SPacket.PositionAndOnGround(
                    ((PlayerMoveC2SPacket) event.getPacket()).getX(mc.player.getX()) + (Math.random() * 0.002 - 0.001),
                    ((PlayerMoveC2SPacket) event.getPacket()).getY(mc.player.getY()),
                    ((PlayerMoveC2SPacket) event.getPacket()).getZ(mc.player.getZ()) + (Math.random() * 0.002 - 0.001),
                    ((PlayerMoveC2SPacket) event.getPacket()).isOnGround()
            );

            sendPacketSafely(movePacket);
        }

        Packet<?> pkt = event.getPacket();

        if (System.currentTimeMillis() < disablePulseUntil) {
            return;
        }

        if (sending.get()) return;

        if (pkt instanceof CommonPongC2SPacket) {
            storedTransactions.add(pkt);
        }

        if (pulse.getValue()) {
            if (pkt instanceof PlayerMoveC2SPacket) {
                event.cancel();
                storedPackets.add(pkt);
            }
        } else if (!(pkt instanceof ChatMessageC2SPacket || pkt instanceof TeleportConfirmC2SPacket || pkt instanceof KeepAliveC2SPacket || pkt instanceof AdvancementTabC2SPacket || pkt instanceof ClientStatusC2SPacket)) {
            event.cancel();
            storedPackets.add(pkt);
        }
    }

    @EventHandler
    public void onUpdate(EventTick event) {

        if (fullNullCheck()) return;

        if (isKeyPressed(cancel)) {
            storedPackets.clear();
            mc.player.setPos(lastPos.getX(), lastPos.getY(), lastPos.getZ());
            mc.player.setVelocity(prevVelocity);
            mc.player.setYaw(prevYaw);
            mc.player.setSprinting(prevSprinting);
            mc.player.setSneaking(false);
            mc.options.sneakKey.setPressed(false);
            sending.set(true);
            while (!storedTransactions.isEmpty())
                sendPacket(storedTransactions.poll());
            sending.set(false);
            disable(isRu() ? "Отменяю.." : "Canceling..");
            return;
        }

        if (pulse.getValue()) {
            if (storedPackets.size() >= pulsePackets.getValue()) {
                sendPackets();
            }
        }
    }


    private void resetPosition() {
        sending.set(false);
    }


    private void sendPackets() {


        if (mc.player == null) return;
        sending.set(true);

        while (!storedPackets.isEmpty()) {
            Packet<?> packet = storedPackets.poll();
            sendPacket(packet);
            if (packet instanceof PlayerMoveC2SPacket && !(packet instanceof PlayerMoveC2SPacket.LookAndOnGround)) {
                lastPos = new Vec3d(((PlayerMoveC2SPacket) packet).getX(mc.player.getX()), ((PlayerMoveC2SPacket) packet).getY(mc.player.getY()), ((PlayerMoveC2SPacket) packet).getZ(mc.player.getZ()));

                if (renderMode.getValue() == RenderMode.Model || renderMode.getValue() == RenderMode.Both) {
                    blinkPlayer.deSpawn();
                    blinkPlayer = new PlayerEntityCopy();
                    blinkPlayer.spawn();
                }
            }
        }

        sending.set(false);
        storedPackets.clear();
    }

    public void onRender3D(MatrixStack stack) {
        if (mc.player == null || mc.world == null) return;
        if (render.getValue() && lastPos != null) {
            if (renderMode.getValue() == RenderMode.Circle || renderMode.getValue() == RenderMode.Both) {
                float[] hsb = Color.RGBtoHSB(circleColor.getValue().getRed(), circleColor.getValue().getGreen(), circleColor.getValue().getBlue(), null);
                float hue = (float) (System.currentTimeMillis() % 7200L) / 7200F;
                int rgb = Color.getHSBColor(hue, hsb[1], hsb[2]).getRGB();
                ArrayList<Vec3d> vecs = new ArrayList<>();
                double x = lastPos.x;
                double y = lastPos.y;
                double z = lastPos.z;

                for (int i = 0; i <= 360; ++i) {
                    Vec3d vec = new Vec3d(x + Math.sin((double) i * Math.PI / 180.0) * 0.5D, y + 0.01, z + Math.cos((double) i * Math.PI / 180.0) * 0.5D);
                    vecs.add(vec);
                }

                for (int j = 0; j < vecs.size() - 1; ++j) {
                    Render3DEngine.drawLine(vecs.get(j), vecs.get(j + 1), new Color(rgb));
                    hue += (1F / 360F);
                    rgb = Color.getHSBColor(hue, hsb[1], hsb[2]).getRGB();
                }
            }
            if (renderMode.getValue() == RenderMode.Model || renderMode.getValue() == RenderMode.Both) {
                if (blinkPlayer == null) {
                    blinkPlayer = new PlayerEntityCopy();
                    blinkPlayer.spawn();
                }
            }
        }
    }

}

