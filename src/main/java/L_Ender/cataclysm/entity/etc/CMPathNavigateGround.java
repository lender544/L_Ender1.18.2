package L_Ender.cataclysm.entity.etc;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class CMPathNavigateGround extends GroundPathNavigation {
    public CMPathNavigateGround(Mob mob, Level world) {
        super(mob, world);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new CMPathFinder(this.nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected void followThePath() {
        Path path = Objects.requireNonNull(this.path);
        Vec3 mobPos = this.getTempMobPos();
        int pathLength = path.getNodeCount();
        for (int i = path.getNextNodeIndex(); i < path.getNodeCount(); i++) {
            if (path.getNode(i).y != Math.floor(mobPos.y)) {
                pathLength = i;
                break;
            }
        }
        final Vec3 base = mobPos.add(-this.mob.getBbWidth() * 0.5F, 0.0F, -this.mob.getBbWidth() * 0.5F);
        final Vec3 max = base.add(this.mob.getBbWidth(), this.mob.getBbHeight(), this.mob.getBbWidth());
        if (this.tryShortcut(path, new Vec3(this.mob.getX(), this.mob.getY(), this.mob.getZ()), pathLength, base, max)) {
            if (this.isAt(path, 0.5F) || this.atElevationChange(path) && this.isAt(path, this.mob.getBbWidth() * 0.5F)) {
                path.setNextNodeIndex(path.getNextNodeIndex() + 1);
            }
        }
        this.doStuckDetection(mobPos);
    }

    private boolean isAt(Path path, float threshold) {
        final Vec3 pathPos = path.getNextEntityPos(this.mob);
        return Mth.abs((float) (this.mob.getX() - pathPos.x)) < threshold &&
                Mth.abs((float) (this.mob.getZ() - pathPos.z)) < threshold &&
                Math.abs(this.mob.getY() - pathPos.y) < 1.0D;
    }

    private boolean atElevationChange(Path path) {
        final int curr = path.getNextNodeIndex();
        final int end = Math.min(path.getNodeCount(), curr + Mth.ceil(this.mob.getBbWidth() * 0.5F) + 1);
        final int currY = path.getNode(curr).y;
        for (int i = curr + 1; i < end; i++) {
            if (path.getNode(i).y != currY) {
                return true;
            }
        }
        return false;
    }

    private boolean tryShortcut(Path path, Vec3 mobPos, int pathLength, Vec3 base, Vec3 max) {
        for (int i = pathLength; --i > path.getNextNodeIndex(); ) {
            final Vec3 vec = path.getEntityPosAtNode(this.mob, i).subtract(mobPos);
            if (this.sweep(vec, base, max)) {
                path.setNextNodeIndex(i);
                return false;
            }
        }
        return true;
    }


    static final float EPSILON = 1.0E-8F;

    // Based off of https://github.com/andyhall/voxel-aabb-sweep/blob/d3ef85b19c10e4c9d2395c186f9661b052c50dc7/index.js
    private boolean sweep(Vec3 vec, Vec3 base, Vec3 max) {
        float t = 0.0F;
        float max_t = (float) vec.length();
        if (max_t < EPSILON) return true;
        final float[] tr = new float[3];
        final int[] ldi = new int[3];
        final int[] tri = new int[3];
        final int[] step = new int[3];
        final float[] tDelta = new float[3];
        final float[] tNext = new float[3];
        final float[] normed = new float[3];
        for (int i = 0; i < 3; i++) {
            float value = element(vec, i);
            boolean dir = value >= 0.0F;
            step[i] = dir ? 1 : -1;
            float lead = element(dir ? max : base, i);
            tr[i] = element(dir ? base : max, i);
            ldi[i] = leadEdgeToInt(lead, step[i]);
            tri[i] = trailEdgeToInt(tr[i], step[i]);
            normed[i] = value / max_t;
            tDelta[i] = Mth.abs(max_t / value);
            float dist = dir ? (ldi[i] + 1 - lead) : (lead - ldi[i]);
            tNext[i] = tDelta[i] < Float.POSITIVE_INFINITY ? tDelta[i] * dist : Float.POSITIVE_INFINITY;
        }

        return true;
    }


    protected boolean hasValidPathType(BlockPathTypes p_26467_) {
        if (p_26467_ == BlockPathTypes.WATER) {
            return false;
        } else if (p_26467_ == BlockPathTypes.LAVA) {
            return false;
        } else {
            return p_26467_ != BlockPathTypes.OPEN;
        }
    }

    static int leadEdgeToInt(float coord, int step) {
        return Mth.floor(coord - step * EPSILON);
    }

    static int trailEdgeToInt(float coord, int step) {
        return Mth.floor(coord + step * EPSILON);
    }

    static float element(Vec3 v, int i) {
        switch (i) {
            case 0: return (float) v.x;
            case 1: return (float) v.y;
            case 2: return (float) v.z;
            default: return 0.0F;
        }
    }
}
