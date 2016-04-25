package darius0021.versions.v1_9_1;

import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.Navigation;
import net.minecraft.server.v1_9_R1.World;

public class FlyingNav extends Navigation {

    public FlyingNav(EntityInsentient arg0, World arg1) {
        super(arg0, arg1);
    }

    @Override
    protected boolean b() {
        return true;
    }
}
