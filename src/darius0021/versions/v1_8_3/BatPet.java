package darius0021.versions.v1_8_3;

import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.World;

public class BatPet extends EntityBat {

    public BatPet(net.minecraft.server.v1_8_R3.World arg) {
        super(arg);
    }

    public BatPet(World arg, boolean l) {
        super(arg);
        this.navigation = new FlyingNav(this, this.world);
        this.setAsleep(false);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5);
    }

    @Override
    protected void E() {
        if (this.getBukkitEntity().getLocation().getBlock().getLocation().add(0, -1, 0).getBlock().getType().isSolid())
            this.motY += 0.2f;
    }
}