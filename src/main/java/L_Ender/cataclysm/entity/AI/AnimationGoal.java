package L_Ender.cataclysm.entity.AI;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
public class AnimationGoal<T extends LivingEntity & IAnimatedEntity> extends Goal {
    private final T entity;
    private final Animation animation;

    public AnimationGoal(T entity, Animation animation) {
        this.entity = entity;
        this.animation = animation;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE, Flag.LOOK));

    }

    @Override
    public boolean canUse() {
        return this.test(this.entity.getAnimation());
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.test(this.entity.getAnimation()) && this.entity.getAnimationTick() < this.entity.getAnimation().getDuration();
    }

    @Override
    public void stop() {
        if (this.test(this.entity.getAnimation())) {
            AnimationHandler.INSTANCE.sendAnimationMessage(this.entity, IAnimatedEntity.NO_ANIMATION);
        }
    }

    protected boolean test(Animation animation) {
        return animation == this.animation;
    }
}