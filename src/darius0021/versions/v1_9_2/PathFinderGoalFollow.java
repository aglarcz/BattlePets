package darius0021.versions.v1_9_2;


import net.minecraft.server.v1_9_R2.*;
import org.bukkit.entity.LivingEntity;


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
    	/**
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
        **/
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
}
