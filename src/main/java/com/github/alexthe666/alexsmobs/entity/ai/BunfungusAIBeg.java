package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.EnumSet;

public class BunfungusAIBeg extends Goal {
    private static final TargetingConditions ENTITY_PREDICATE = TargetingConditions.forNonCombat().range(32D);
    protected final EntityBunfungus jerboa;
    private final double speed;
    protected Player closestPlayer;
    private int delayTemptCounter;
    private boolean isRunning;

    public BunfungusAIBeg(EntityBunfungus jerboa, double speed) {
        this.jerboa = jerboa;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        } else {
            this.closestPlayer = this.jerboa.level.getNearestPlayer(ENTITY_PREDICATE, this.jerboa);
            if (this.closestPlayer == null) {
                return false;
            } else {
                boolean food = isFood(this.closestPlayer.getMainHandItem()) || isFood(this.closestPlayer.getOffhandItem());
                return food;
            }
        }
    }

    private boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.CARROT;
    }


    public boolean canContinueToUse() {
        return this.jerboa.getMainHandItem().isEmpty() && this.canUse();
    }

    public void start() {
        this.isRunning = true;
    }

    public void stop() {
        this.closestPlayer = null;
        this.jerboa.getNavigation().stop();
        this.delayTemptCounter = 100;
        this.jerboa.setBegging(false);
        this.isRunning = false;
    }

    public void tick() {
        this.jerboa.setSleeping(false);
        this.jerboa.getLookControl().setLookAt(this.closestPlayer, (float)(this.jerboa.getMaxHeadYRot() + 20), (float)this.jerboa.getMaxHeadXRot());
        if (this.jerboa.distanceToSqr(this.closestPlayer) < 12D) {
            this.jerboa.getNavigation().stop();
            this.jerboa.setBegging(true);
        } else {
            this.jerboa.getNavigation().moveTo(this.closestPlayer, this.speed);
        }

    }

    public boolean isRunning() {
        return this.isRunning;
    }
}
