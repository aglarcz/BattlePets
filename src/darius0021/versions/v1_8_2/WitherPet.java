package darius0021.versions.v1_8_2;

import net.minecraft.server.v1_8_R2.EntityWither;
import net.minecraft.server.v1_8_R2.World;

public class WitherPet extends EntityWither {
    boolean baby;

    public WitherPet(World world) {
        super(world);
    }

    public WitherPet(World world, boolean t) {
        super(world);
        this.navigation = new FlyingNav(this, this.world);
        baby = false;
    }

    @Override
    protected void E() {
        if (baby)
            this.r(600);
        if (this.getBukkitEntity().getLocation().getBlock().getLocation().add(0, -1, 0).getBlock().getType().isSolid())
            this.motY += 0.2f;
    }

    public void setBaby(boolean flag) {
        baby = flag;
    }
}
