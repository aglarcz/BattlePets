package darius0021.versions.v1_9_2;

import net.minecraft.server.v1_9_R2.EntityInsentient;
import net.minecraft.server.v1_9_R2.Navigation;
import net.minecraft.server.v1_9_R2.World;

public class FlyingNav extends Navigation {

    public FlyingNav(EntityInsentient arg0, World arg1) {
        super(arg0, arg1);
    }

    @Override
    protected boolean b() {
        return true;
    }
}
