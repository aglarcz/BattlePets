package darius0021.versions.v1_8_1;


import net.minecraft.server.v1_8_R1.*;
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
        if (creature instanceof ArmorStandPlus)
            ((ArmorStandPlus) creature).update();
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

    public void tp() {
        ((LivingEntity) creature.getBukkitEntity()).teleport(player.getBukkitEntity().getLocation());
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
        f = ((EntityLiving) player).aX * 0.5F;
        f1 = ((EntityLiving) player).aY;

        if (f1 <= 0.0F) {
            f1 *= 0.25F;
        }
        f *= 0.75F;
        creature.j((float) speed * 0.7F);
        creature.g(f, f1);
        creature.S = 1F;
        Field jump = null;
        try {
            jump = EntityLiving.class.getDeclaredField("aW");
            jump.setAccessible(true);

            if (jump != null && creature.onGround) {    // Wouldn't want it jumping while on the ground would we?

                if (jump.getBoolean(creature.passenger)) {
                    double jumpHeight = 0.5D;
                    creature.motY = jumpHeight;    // Used all the time in NMS for entity jumping
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void e() {
        speed = creature.getBukkitEntity().getMetadata("Speed").get(0).asDouble();
        creature.a(player, 30F, 30F);
        //if (creature instanceof EntityBat)
        //	flying();
        //else
        this.navigation.a(player, speed);
        creature.a(player, 30F, 30F);
    }

    void flying() {
        EntityBat b = (EntityBat) creature;
        EntityLiving localEntityLiving = player;

        double d1 = 3;
        if (localEntityLiving.getBukkitEntity().getLocation().distanceSquared(b.getBukkitEntity().getLocation()) < d1 * d1) {
            double d2 = localEntityLiving.locX - b.locX;
            double d3 = localEntityLiving.locZ - b.locZ;
            b.aG = (b.yaw = -(float) Math.atan2(d2, d3) * 180.0F / 3.141593F);
        }

    }
}
