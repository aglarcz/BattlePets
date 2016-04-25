package darius0021.versions.v1_8_3;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.Navigation;
import net.minecraft.server.v1_8_R3.World;

public class FlyingNav extends Navigation {

    public FlyingNav(EntityInsentient arg0, World arg1) {
        super(arg0, arg1);
    }

    @Override
    protected boolean b() {
        return true;
    }
}
