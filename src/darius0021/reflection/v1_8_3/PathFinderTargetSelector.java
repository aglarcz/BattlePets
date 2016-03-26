package darius0021.reflection.v1_8_3;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.PathEntity;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.World;

public class PathFinderTargetSelector extends PathfinderGoal {

	  World a;
	  protected EntityInsentient b;
	  int c;
	  double d;
	  boolean e;
	  PathEntity f;
	  private int h;
	  private double i;
	  private double j;
	  private double k;
	  Class<? extends Entity> g;
	public PathFinderTargetSelector(EntityInsentient paramEntityCreature, double paramDouble, boolean paramBoolean) {
	    this.b = paramEntityCreature;
	    this.a = paramEntityCreature.world;
	    this.d = paramDouble;
	    this.e = paramBoolean;
	    a(3);
		
	}

	@Override
	public void e()
	  {
	    EntityLiving localEntityLiving = this.b.getGoalTarget();
	    if (localEntityLiving==null) return;
	    this.b.getControllerLook().a(localEntityLiving, 30.0F, 30.0F);
	    double d1 = this.b.e(localEntityLiving.locX, localEntityLiving.getBoundingBox().b, localEntityLiving.locZ);
	    double d2 = a(localEntityLiving);
	    this.h -= 1;
	    if (((this.e) || (this.b.getEntitySenses().a(localEntityLiving))) && 
	      (this.h <= 0) && (
	      ((this.i == 0.0D) && (this.j == 0.0D) && (this.k == 0.0D)) || (localEntityLiving.e(this.i, this.j, this.k) >= 1.0D) || (this.b.bc().nextFloat() < 0.05F)))
	    {
	      this.i = localEntityLiving.locX;
	      this.j = localEntityLiving.getBoundingBox().b;
	      this.k = localEntityLiving.locZ;
	      this.h = (4 + this.b.bc().nextInt(7));
	      if (d1 > 1024.0D) {
	        this.h += 10;
	      } else if (d1 > 256.0D) {
	        this.h += 5;
	      }
	      b.a(localEntityLiving, 30F, 30F);
	      if (!this.b.getNavigation().a(localEntityLiving, this.d)) {
	        this.h += 15;
	      }
	    }
	    this.c = Math.max(this.c - 1, 0);
	    if ((d1 <= d2) && (this.c <= 0))
	    {
	      this.c = 20;
	      if (this.b.bA() != null) {
	        this.b.bw();
	      }
	      this.b.getGoalTarget().damageEntity(DamageSource.mobAttack(b), (float) b.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue());
	    }
	      b.a(localEntityLiving, 30F, 30F);
	  }

	@Override
	public boolean a() {
		if (b.passenger!=null)
			return false;
		if (b.getGoalTarget()==null)
		return false;
		if (b.getGoalTarget().dead)
		return false;
		return true;
	}
	  protected double a(EntityLiving paramEntityLiving)
	  {
	    return this.b.width * 2.0F * (this.b.width * 2.0F) + paramEntityLiving.width;
	  }
}