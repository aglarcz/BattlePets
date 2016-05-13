package darius0021.versions.v1_9_1;


import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;

public class PathFinderGoalFollow extends PathfinderGoal {

    EntityPlayer player;
    EntityInsentient creature;
    double range, tprange;
    double speed;
    private Navigation navigation;

    public PathFinderGoalFollow(EntityInsentient creature, EntityPlayer owner, double range, double tprange, double speed) {
        this.player = owner;
        this.creature = creature;
        this.range = range;
        this.speed = speed;
        this.navigation = (Navigation) this.creature.getNavigation();
        this.tprange = tprange;
    }

    @Override
    public boolean a() {
        if (creature instanceof ArmorStandPlus) {
            ((ArmorStandPlus) creature).update();
        }
        if (creature instanceof CustomPet) {
            ((CustomPet) creature).update();
        }
        if (!creature.passengers.isEmpty()) {
            riding();
            return false;
        }
        if (creature.getWorld().getWorld().getName() != player.world.getWorld().getName()) {
            creature.setGoalTarget(null);
            tp();
            return false;
        }
        double dist = creature.getBukkitEntity().getLocation().distance(player.getBukkitEntity().getLocation());
        if (dist >= tprange) {
            creature.setGoalTarget(null);
            tp();
            return false;
        }
        if (dist >= range) {
            if (creature.getGoalTarget() == null)
                return true;
            if (creature.getGoalTarget().dead) {
                creature.setGoalTarget(null);
                return true;
            }
            return false;
        }
        if (creature.getGoalTarget() == null) {
            creature.getNavigation().a(player, 0);
        }
        return false;
    }

    public void riding() {

        if (creature.passengers.isEmpty()) return;
        creature.lastYaw = (creature.yaw = player.yaw);
        creature.pitch = (player.pitch * 0.5F); //+
        // creature.setYawPitch(creature.yaw, creature.pitch);
        creature.getBukkitEntity().getLocation().setYaw(creature.yaw);
        creature.getBukkitEntity().getLocation().setPitch(creature.pitch); //+
        creature.aO = (creature.aM = creature.yaw);
        float f, f1;
        f = ((EntityLiving) player).bd * 0.5F;
        f1 = ((EntityLiving) player).be;
        if (f1 <= 0.0F)
            f1 *= 0.25F;
        f *= 0.75F;
        creature.l((float) speed * 0.7F);
        if (!creature.onGround) {
            f *= 10;
            f1 *= 10;
        }
        //Bukkit.broadcastMessage(f1+"");
        creature.g(f, f1);
        creature.P = 1F;
        Field jump = null;
        try {
            jump = EntityLiving.class.getDeclaredField("bc");
            jump.setAccessible(true);

            if (jump != null && (creature.onGround || creature instanceof BatPet)) {    // Wouldn't want it jumping while on the ground would we?

                if (jump.getBoolean(creature.passengers.get(0))) {
                    double jumpHeight = 0.5D;
                    creature.motY = jumpHeight;    // Used all the time in NMS for entity jumping
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tp() {
        ((LivingEntity) creature.getBukkitEntity()).teleport(player.getBukkitEntity().getLocation());
    }

    @Override
    public void e() {
        speed = creature.getBukkitEntity().getMetadata("Speed").get(0).asDouble();
        creature.a(player, 30F, 30F);
        this.navigation.a(player, speed);
        creature.a(player, 30F, 30F);
    }

    public void g(float f, float f1) {
        if ((creature.co()) || (creature.bx())) {
            if ((creature.isInWater())) {
                double d1 = creature.locY;
                float f4 = 0.8F;
                float f3 = 0.02F;
                float f2 = EnchantmentManager.d(creature);
                if (f2 > 3.0F) {
                    f2 = 3.0F;
                }
                if (!creature.onGround) {
                    f2 *= 0.5F;
                }
                if (f2 > 0.0F) {
                    f4 += (0.5460001F - f4) * f2 / 3.0F;
                    f3 += (creature.ck() - f3) * f2 / 3.0F;
                }
                creature.a(f, f1, f3);
                creature.move(creature.motX, creature.motY, creature.motZ);
                creature.motX *= f4;
                creature.motY *= 0.800000011920929D;
                creature.motZ *= f4;
                creature.motY -= 0.02D;
                if ((creature.positionChanged) && (creature.c(creature.motX, creature.motY + 0.6000000238418579D - creature.locY + d1, creature.motZ))) {
                    creature.motY = 0.300000011920929D;
                }
            } else if (creature.an()) {
                double d1 = creature.locY;
                creature.a(f, f1, 0.02F);
                creature.move(creature.motX, creature.motY, creature.motZ);
                creature.motX *= 0.5D;
                creature.motY *= 0.5D;
                creature.motZ *= 0.5D;
                creature.motY -= 0.02D;
                if ((creature.positionChanged) && (creature.c(creature.motX, creature.motY + 0.6000000238418579D - creature.locY + d1, creature.motZ))) {
                    creature.motY = 0.300000011920929D;
                }
            } else {
                if (creature.motY > -0.5D) {
                    creature.fallDistance = 1.0F;
                }
                Vec3D vec3d = creature.aB();
                float f5 = creature.pitch * 0.01745329F;

                double d0 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
                double d2 = Math.sqrt(creature.motX * creature.motX + creature.motZ * creature.motZ);
                double d3 = vec3d.b();
                float f6 = MathHelper.cos(f5);

                f6 = (float) (f6 * f6 * Math.min(1.0D, d3 / 0.4D));
                creature.motY += -0.08D + f6 * 0.06D;
                if ((creature.motY < 0.0D) && (d0 > 0.0D)) {
                    double d4 = creature.motY * -0.1D * f6;
                    creature.motY += d4;
                    creature.motX += vec3d.x * d4 / d0;
                    creature.motZ += vec3d.z * d4 / d0;
                }
                if (f5 < 0.0F) {
                    double d4 = d2 * -MathHelper.sin(f5) * 0.04D;
                    creature.motY += d4 * 3.2D;
                    creature.motX -= vec3d.x * d4 / d0;
                    creature.motZ -= vec3d.z * d4 / d0;
                }
                if (d0 > 0.0D) {
                    creature.motX += (vec3d.x / d0 * d2 - creature.motX) * 0.1D;
                    creature.motZ += (vec3d.z / d0 * d2 - creature.motZ) * 0.1D;
                }
                creature.motX *= 0.9900000095367432D;
                creature.motY *= 0.9800000190734863D;
                creature.motZ *= 0.9900000095367432D;
                creature.move(creature.motX, creature.motY, creature.motZ);
                if (creature.positionChanged) {
                    double d4 = Math.sqrt(creature.motX * creature.motX + creature.motZ * creature.motZ);
                    double d5 = d2 - d4;
                    float f7 = (float) (d5 * 10.0D - 3.0D);
                    if (f7 > 0.0F) {
                        creature.a(creature.e((int) f7), 1.0F, 1.0F);
                        creature.damageEntity(DamageSource.j, f7);
                    }
                }
                if (creature.onGround) {
                    creature.setFlag(7, false);
                }
            }

        }
        creature.aE = creature.aF;
        double d1 = creature.locX - creature.lastX;
        double d0 = creature.locZ - creature.lastZ;
        float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
        if (f2 > 1.0F) {
            f2 = 1.0F;
        }
        creature.aF += (f2 - creature.aF) * 0.4F;
        creature.aG += creature.aF;
    }
}
