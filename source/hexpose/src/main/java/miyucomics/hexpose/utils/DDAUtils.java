package miyucomics.hexpose.utils;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class DDAUtils {
    public static List<Iota> raycastBlock(Vec3 position, Vec3 direction, Predicate<BlockPos> hitTarget, Predicate<BlockPos> exceededBounds) {
        Vec3 delta = new Vec3(Math.abs(1 / direction.x), Math.abs(1 / direction.y), Math.abs(1 / direction.z));

        int voxelX = (int) Math.floor(position.x);
        int voxelY = (int) Math.floor(position.y);
        int voxelZ = (int) Math.floor(position.z);
        int stepX;
        int stepY;
        int stepZ;
        double sideDistanceX;
        double sideDistanceY;
        double sideDistanceZ;

        if (direction.x > 0) {
            stepX = 1;
            sideDistanceX = (voxelX + 1 - position.x) * delta.x;
        } else {
            stepX = -1;
            sideDistanceX = (position.x - voxelX) * delta.x;
        }

        if (direction.y > 0) {
            stepY = 1;
            sideDistanceY = (voxelY + 1 - position.y) * delta.y;
        } else {
            stepY = -1;
            sideDistanceY = (position.y - voxelY) * delta.y;
        }

        if (direction.z > 0) {
            stepZ = 1;
            sideDistanceZ = (voxelZ + 1 - position.z) * delta.z;
        } else {
            stepZ = -1;
            sideDistanceZ = (position.z - voxelZ) * delta.z;
        }

        while (true) {
            char axis;
            int step;
            double offset;

            if (sideDistanceX < sideDistanceY && sideDistanceX < sideDistanceZ) {
                axis = 'X';
                step = stepX;
                offset = delta.x;
            } else if (sideDistanceY < sideDistanceZ) {
                axis = 'Y';
                step = stepY;
                offset = delta.y;
            } else {
                axis = 'Z';
                step = stepZ;
                offset = delta.z;
            }

            switch (axis) {
                case 'X':
                    voxelX += step;
                    sideDistanceX += offset;
                    break;
                case 'Y':
                    voxelY += step;
                    sideDistanceY += offset;
                    break;
                case 'Z':
                    voxelZ += step;
                    sideDistanceZ += offset;
                    break;
                default:
                    throw new IllegalStateException();
            }

            BlockPos pos = new BlockPos(voxelX, voxelY, voxelZ);
            if (hitTarget.test(pos)) {
                return List.of(new Vec3Iota(new Vec3(voxelX + 0.5, voxelY + 0.5, voxelZ + 0.5)));
            }
            if (exceededBounds.test(pos)) {
                return List.of(new NullIota());
            }
        }
    }

    public static List<Iota> raycastNormal(Vec3 position, Vec3 direction, Predicate<BlockPos> hitTarget, Predicate<BlockPos> exceededBounds) {
        Vec3 delta = new Vec3(Math.abs(1 / direction.x), Math.abs(1 / direction.y), Math.abs(1 / direction.z));

        int voxelX = (int) Math.floor(position.x);
        int voxelY = (int) Math.floor(position.y);
        int voxelZ = (int) Math.floor(position.z);
        int stepX;
        int stepY;
        int stepZ;
        double sideDistanceX;
        double sideDistanceY;
        double sideDistanceZ;

        if (direction.x > 0) {
            stepX = 1;
            sideDistanceX = (voxelX + 1 - position.x) * delta.x;
        } else {
            stepX = -1;
            sideDistanceX = (position.x - voxelX) * delta.x;
        }

        if (direction.y > 0) {
            stepY = 1;
            sideDistanceY = (voxelY + 1 - position.y) * delta.y;
        } else {
            stepY = -1;
            sideDistanceY = (position.y - voxelY) * delta.y;
        }

        if (direction.z > 0) {
            stepZ = 1;
            sideDistanceZ = (voxelZ + 1 - position.z) * delta.z;
        } else {
            stepZ = -1;
            sideDistanceZ = (position.z - voxelZ) * delta.z;
        }

        Vec3 normal = Vec3.ZERO;

        while (true) {
            char axis;
            int step;
            double offset;

            if (sideDistanceX < sideDistanceY && sideDistanceX < sideDistanceZ) {
                axis = 'X';
                step = stepX;
                offset = delta.x;
            } else if (sideDistanceY < sideDistanceZ) {
                axis = 'Y';
                step = stepY;
                offset = delta.y;
            } else {
                axis = 'Z';
                step = stepZ;
                offset = delta.z;
            }

            switch (axis) {
                case 'X':
                    voxelX += step;
                    sideDistanceX += offset;
                    normal = new Vec3(-step, 0.0, 0.0);
                    break;
                case 'Y':
                    voxelY += step;
                    sideDistanceY += offset;
                    normal = new Vec3(0.0, -step, 0.0);
                    break;
                case 'Z':
                    voxelZ += step;
                    sideDistanceZ += offset;
                    normal = new Vec3(0.0, 0.0, -step);
                    break;
                default:
                    throw new IllegalStateException();
            }

            BlockPos pos = new BlockPos(voxelX, voxelY, voxelZ);
            if (hitTarget.test(pos)) {
                return List.of(new Vec3Iota(normal));
            }
            if (exceededBounds.test(pos)) {
                return List.of(new NullIota());
            }
        }
    }
}