package thunder.hack.injection;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.features.modules.combat.ElytraTarget;
import thunder.hack.features.modules.misc.FakePlayer;
import thunder.hack.utility.interfaces.IEntityLivingelytra;
import thunder.hack.utility.interfaces.IOtherClientPlayerEntity;
import thunder.hack.utility.interfaces.IOtherClientPlayerEntityelytra;

import static thunder.hack.features.modules.Module.mc;

@Mixin(MixinOtherClientPlayerEntityelytra.class)
public class MixinOtherClientPlayerEntityelytra extends AbstractClientPlayerEntity implements IOtherClientPlayerEntityelytra {
    @Unique private double backUpX, backUpY, backUpZ;

    public MixinOtherClientPlayerEntityelytra(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    public void resolveelytra(ElytraTarget.Resolver mode) {
        if ((Object) this == FakePlayer.fakePlayer) {
            backUpY = -999;
            return;
        }

        backUpX = getX();
        backUpY = getY();
        backUpZ = getZ();

        if(mode == ElytraTarget.Resolver.BackTrack) {
            double minDst = 999d;
            ElytraTarget.Position bestPos = null;
            for (ElytraTarget.Position p : ((IEntityLivingelytra) this).getPositionHistory()) {
                double dst = mc.player.squaredDistanceTo(p.getX(), p.getY(), p.getZ());
                if (dst < minDst) {
                    minDst = dst;
                    bestPos = p;
                }
            }
            if(bestPos != null) {
                setPosition(bestPos.getX(), bestPos.getY(), bestPos.getZ());
                if(ElytraTarget.target == this)
                    ModuleManager.aura.resolvedBox = getBoundingBox();
            }
            return;
        }

        Vec3d from = new Vec3d(((IEntityLivingelytra) this).getPrevServerX(), ((IEntityLivingelytra) this).getPrevServerY(), ((IEntityLivingelytra) this).getPrevServerZ());
        Vec3d to = new Vec3d(serverX, serverY, serverZ);

        if(mode == ElytraTarget.Resolver.Advantage) {
            if (mc.player.squaredDistanceTo(from) > mc.player.squaredDistanceTo(to)) setPosition(to.x, to.y, to.z);
            else setPosition(from.x, from.y, from.z);
        } else {
            setPosition(to.x, to.y, to.z);
        }
        if(ElytraTarget.target == this)
            ModuleManager.elytraTarget.resolvedBox = getBoundingBox();
    }

    @Override
    public void releaseResolverelytra() {
        if (backUpY != -999) {
            setPosition(backUpX, backUpY, backUpZ);
            backUpY = -999;
        }
    }

}