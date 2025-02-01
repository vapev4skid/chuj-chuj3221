package thunder.hack.features.modules.misc;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render3DEngine;

import java.awt.*;
import java.util.ArrayList;

public class ChinaHat extends Module {
    private final Setting<Color> hatColor = new Setting<>("HatColor", new Color(255, 0, 213, 150));
    private final Setting<Boolean> rainbow = new Setting<>("Rainbow", false);
    private final Setting<Float> radius = new Setting<>("Radius", 0.6f, 0.1f, 2.0f);
    private final Setting<Float> height = new Setting<>("Height", 0.4f, 0.1f, 1.0f);
    private final Setting<Integer> segments = new Setting<>("Segments", 30, 3, 500);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", true);
    private final Setting<Float> rotationSpeed = new Setting<>("RotationSpeed", 1.0f, 0.1f, 5.0f);
    private final Setting<Boolean> excludeSelf = new Setting<>("ExcludeSelf", true);

    private float rotationAngle = 0.0f;

    public ChinaHat() {
        super("ChinaHat", Category.MISC);
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        if (mc.player == null || mc.world == null) return;

        if (rotate.getValue()) {
            rotationAngle += rotationSpeed.getValue();
            if (rotationAngle >= 360.0f) rotationAngle -= 360.0f;
        }

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == null || player.isInvisible()) continue;
            if (excludeSelf.getValue() && player.equals(mc.player)) continue;

            Vec3d playerPos = player.getPos().add(0, player.getEyeHeight(player.getPose()) - 0.2, 0);
            renderChinaHat(stack, playerPos, player.getYaw());
        }
    }

    private void renderChinaHat(MatrixStack stack, Vec3d pos, float yaw) {
        ArrayList<Vec3d> basePoints = new ArrayList<>();
        double centerX = pos.x;
        double centerY = pos.y;
        double centerZ = pos.z;

        for (int i = 0; i <= segments.getValue(); i++) {
            double angle = Math.toRadians((360.0 / segments.getValue()) * i + rotationAngle - yaw);
            double x = centerX + Math.sin(angle) * radius.getValue();
            double z = centerZ + Math.cos(angle) * radius.getValue();
            basePoints.add(new Vec3d(x, centerY, z));
        }

        int color = rainbow.getValue() ? getRainbowColor(0) : hatColor.getValue().getRGB();

        for (int i = 0; i < basePoints.size() - 1; i++) {
            Vec3d point1 = basePoints.get(i);
            Vec3d point2 = basePoints.get(i + 1);
            Render3DEngine.drawLine(point1, new Vec3d(centerX, centerY + height.getValue(), centerZ), new Color(color));
            Render3DEngine.drawLine(point1, point2, new Color(color));
        }

        for (int i = 0; i < basePoints.size() - 1; i++) {
            Vec3d point1 = basePoints.get(i);
            Vec3d point2 = basePoints.get(i + 1);
            Render3DEngine.drawLine(point1, point2, hatColor.getValue());
        }
    }

    private int getRainbowColor(int offset) {
        float hue = ((System.currentTimeMillis() + offset * 100) % 7200L) / 7200.0f;
        return Color.HSBtoRGB(hue, 1.0f, 1.0f);
    }
}