package darius0021.versions.v1_9_2;

import darius0021.versions.Util;
import net.minecraft.server.v1_9_R2.BossBattleServer;
import net.minecraft.server.v1_9_R2.EntityWither;
import net.minecraft.server.v1_9_R2.World;

public class WitherPet extends EntityWither {
    boolean baby;

    public WitherPet(World world) {
        super(world);
    }

    public WitherPet(World world, boolean t) {
        super(world);
        baby = false;
        BossBattleServer bossbar = (BossBattleServer) Util.getPrivateField("bF", EntityWither.class, this);
        bossbar.setVisible(false);
        this.navigation = new FlyingNav(this, this.world);

    }

    @Override
    protected void M() {

        if (baby)
            this.l(600);

        if (this.getBukkitEntity().getLocation().getBlock().getLocation().add(0, -1, 0).getBlock().getType().isSolid())
            this.motY += 0.2f;
    }

    public void setBaby(boolean flag) {
        baby = flag;
    }
}
