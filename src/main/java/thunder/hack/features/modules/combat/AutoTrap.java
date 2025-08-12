package thunder.hack.features.modules.combat;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.player.CombatManager;
import thunder.hack.features.modules.base.TrapModule;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.math.MathUtility;
import net.minecraft.util.math.BlockPos;
import thunder.hack.utility.player.InteractionUtility;

public final class AutoTrap extends TrapModule {
    private final Setting<CombatManager.TargetBy> targetBy = new Setting<>("Target By", CombatManager.TargetBy.Distance);
    private final Setting<Boolean> targetMovingPlayers = new Setting<>("MovingPlayers", false);
    
    // Anti-flagging improvements
    private final Setting<Boolean> randomizeDelay = new Setting<>("Randomize Delay", false);
    private final Setting<Integer> minDelay = new Setting<>("Min Delay", 1, 0, 10, v -> randomizeDelay.getValue());
    private final Setting<Integer> maxDelay = new Setting<>("Max Delay", 3, 0, 15, v -> randomizeDelay.getValue());
    private final Setting<Boolean> validateTarget = new Setting<>("Validate Target", false);
    private final Setting<Float> minTargetHealth = new Setting<>("Min Target Health", 4.0f, 1.0f, 20.0f, v -> validateTarget.getValue());
    private final Setting<Boolean> avoidSpectators = new Setting<>("Avoid Spectators", false);
    private final Setting<Boolean> smartTargeting = new Setting<>("Smart Targeting", false);
    
    private final Timer targetValidationTimer = new Timer();
    private final Timer randomDelayTimer = new Timer();
    private int currentRandomDelay = 0;

    public AutoTrap() {
        super("AutoTrap", Category.COMBAT);
    }

    @Override
    protected boolean needNewTarget() {
        if (target == null) return true;
        
        // Basic validation
        if (target.distanceTo(mc.player) > range.getValue() 
            || target.getHealth() + target.getAbsorptionAmount() <= 0 
            || target.isDead()) {
            return true;
        }
        
        // Enhanced validation with timer to avoid constant target switching
        if (validateTarget.getValue() && targetValidationTimer.passedMs(1000)) {
            if (target.getHealth() + target.getAbsorptionAmount() < minTargetHealth.getValue()) {
                return true;
            }
            
            // Check if target is in spectator mode
            if (avoidSpectators.getValue() && target.isSpectator()) {
                return true;
            }
            
            targetValidationTimer.reset();
        }
        
        return false;
    }

    @Override
    protected @Nullable PlayerEntity getTarget() {
        if (smartTargeting.getValue()) {
            // Use more sophisticated targeting logic
            return Managers.COMBAT.getTarget(range.getValue(), targetBy.getValue(), p -> {
                // Basic movement check
                boolean movementCheck = p.getVelocity().lengthSquared() < 0.08 || targetMovingPlayers.getValue();
                
                // Additional validation
                if (validateTarget.getValue()) {
                    if (p.getHealth() + p.getAbsorptionAmount() < minTargetHealth.getValue()) {
                        return false;
                    }
                }
                
                if (avoidSpectators.getValue() && p.isSpectator()) {
                    return false;
                }
                
                return movementCheck;
            });
        } else {
            // Original targeting logic
            return Managers.COMBAT.getTarget(range.getValue(), targetBy.getValue(), p -> p.getVelocity().lengthSquared() < 0.08 || targetMovingPlayers.getValue());
        }
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        targetValidationTimer.reset();
        randomDelayTimer.reset();
        currentRandomDelay = 0;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        targetValidationTimer.reset();
        randomDelayTimer.reset();
        currentRandomDelay = 0;
    }
    
    // Override the placeBlock method to add random delays
    @Override
    protected boolean placeBlock(BlockPos pos, InteractionUtility.Rotate rotate) {
        if (randomizeDelay.getValue()) {
            if (currentRandomDelay == 0) {
                currentRandomDelay = (int) MathUtility.random(minDelay.getValue(), maxDelay.getValue());
                randomDelayTimer.reset();
            }
            
            if (!randomDelayTimer.passedMs(currentRandomDelay * 50)) {
                return false;
            }
            
            currentRandomDelay = 0;
        }
        
        return super.placeBlock(pos, rotate);
    }
}