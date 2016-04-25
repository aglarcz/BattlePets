package darius0021.versions.v1_8_3;


import net.minecraft.server.v1_8_R3.*;
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
        if (creature.passenger != null) {
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
        if (creature.passenger != player) return;
        creature.lastYaw = (creature.yaw = player.yaw);
        creature.pitch = (player.pitch * 0.5F);
        // creature.setYawPitch(creature.yaw, creature.pitch);
        creature.getBukkitEntity().getLocation().setYaw(creature.yaw);
        creature.getBukkitEntity().getLocation().setPitch(creature.pitch);
        creature.aK = (creature.aI = creature.yaw);
        float f, f1;
        f = ((EntityLiving) player).aZ * 0.5F;
        f1 = ((EntityLiving) player).ba;
        if (f1 <= 0.0F)
            f1 *= 0.25F;
        f *= 0.75F;
        creature.k((float) speed * 0.7F);
        if (!creature.onGround) {
            f *= 10;
            f1 *= 10;
        }
        //Bukkit.broadcastMessage(f1+"");
        g(f, f1);
        creature.S = 1F;
        Field jump = null;
        try {
            jump = EntityLiving.class.getDeclaredField("aY");
            jump.setAccessible(true);

            if (jump != null && (creature.onGround || creature instanceof BatPet)) {    // Wouldn't want it jumping while on the ground would we?

                if (jump.getBoolean(creature.passenger)) {
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
        if (creature.bM()) {
            if (creature.V()) {
                double d0 = creature.locY;
                float f3 = 0.8F;
                float f4 = 0.02F;
                float f2 = EnchantmentManager.b(creature);
                if (f2 > 3.0F) {
                    f2 = 3.0F;
                }
                if (!creature.onGround) {
                    f2 *= 0.5F;
                }
                if (f2 > 0.0F) {
                    f3 += (0.5460001F - f3) * f2 / 3.0F;
                    f4 += (creature.bI() * 1.0F - f4) * f2 / 3.0F;
                }
                creature.a(f, f1, f4);
                creature.move(creature.motX, creature.motY, creature.motZ);
                creature.motX *= f3;
                creature.motY *= 0.800000011920929D;
                creature.motZ *= f3;
                creature.motY -= 0.02D;
                if ((creature.positionChanged) && (creature.c(creature.motX, creature.motY + 0.6000000238418579D - creature.locY + d0, creature.motZ))) {
                    creature.motY = 0.300000011920929D;
                }
            } else if (creature.ab()) {
                double d0 = creature.locY;
                creature.a(f, f1, 0.02F);
                creature.move(creature.motX, creature.motY, creature.motZ);
                creature.motX *= 0.5D;
                creature.motY *= 0.5D;
                creature.motZ *= 0.5D;
                creature.motY -= 0.02D;
                if ((creature.positionChanged) && (creature.c(creature.motX, creature.motY + 0.6000000238418579D - creature.locY + d0, creature.motZ))) {
                    creature.motY = 0.300000011920929D;
                }
            } else {
                float f5 = 0.91F;
                if (creature.onGround) {
                    f5 = creature.world.getType(new BlockPosition(MathHelper.floor(creature.locX), MathHelper.floor(creature.getBoundingBox().b) - 1, MathHelper.floor(creature.locZ))).getBlock().frictionFactor * 0.91F;
                }
                float f6 = 0.1627714F / (f5 * f5 * f5);
                float f3;
                if (creature.onGround) {
                    f3 = creature.bI() * f6;
                } else {
                    f3 = creature.aM;
                }
                creature.a(f, f1, f3);
                f5 = 0.91F;
                if (creature.onGround) {
                    f5 = creature.world.getType(new BlockPosition(MathHelper.floor(creature.locX), MathHelper.floor(creature.getBoundingBox().b) - 1, MathHelper.floor(creature.locZ))).getBlock().frictionFactor * 0.91F;
                }
                if (creature.k_()) {
                    float f4 = 0.15F;
                    creature.motX = MathHelper.a(creature.motX, -f4, f4);
                    creature.motZ = MathHelper.a(creature.motZ, -f4, f4);
                    creature.fallDistance = 0.0F;
                    if (creature.motY < -0.15D) {
                        creature.motY = -0.15D;
                    }
                    boolean flag = false;
                    if ((flag) && (creature.motY < 0.0D)) {
                        creature.motY = 0.0D;
                    }
                }
                creature.move(creature.motX, creature.motY, creature.motZ);
                if ((creature.positionChanged) && (creature.k_())) {
                    creature.motY = 0.2D;
                }
                if ((creature.world.isClientSide) && ((!creature.world.isLoaded(new BlockPosition((int) creature.locX, 0, (int) creature.locZ))) || (!creature.world.getChunkAtWorldCoords(new BlockPosition((int) creature.locX, 0, (int) creature.locZ)).o()))) {
                    if (creature.locY > 0.0D) {
                        creature.motY = -0.1D;
                    } else {
                        creature.motY = 0.0D;
                    }
                } else {
                    creature.motY -= 0.08D;
                }
                creature.motY *= 0.9800000190734863D;
                creature.motX *= f5;
                creature.motZ *= f5;
            }
        }
        creature.aA = creature.aB;
        double d0 = creature.locX - creature.lastX;
        double d1 = creature.locZ - creature.lastZ;

        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
        if (f2 > 1.0F) {
            f2 = 1.0F;
        }
        creature.aB += (f2 - creature.aB) * 0.4F;
        creature.aC += creature.aB;
    }
}
