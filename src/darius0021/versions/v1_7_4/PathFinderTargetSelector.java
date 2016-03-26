package darius0021.versions.v1_7_4;

import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.PathEntity;
import net.minecraft.server.v1_7_R4.PathfinderGoal;
import net.minecraft.server.v1_7_R4.World;

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
	public PathFinderTargetSelector(EntityInsentient paramEntityCreature, double paramDouble, boolean paramBoolean) {
	    this.b = paramEntityCreature;
	    this.a = paramEntityCreature.world;
	    this.d = paramDouble;
	    this.e = paramBoolean;
		
	}

	@Override
	public void e()
	  {

	    EntityLiving entityliving = this.b.getGoalTarget();
	    if (entityliving==null) return;
	    this.b.getControllerLook().a(entityliving, 30.0F, 30.0F);
	    double d0 = this.b.e(entityliving.locX, entityliving.boundingBox.b, entityliving.locZ);
	    double d1 = this.b.width * 2.0F * this.b.width * 2.0F + entityliving.width;
	    
	    this.h -= 1;
	    if (((this.e) || (this.b.getEntitySenses().canSee(entityliving))) && (this.h <= 0) && (((this.i == 0.0D) && (this.j == 0.0D) && (this.k == 0.0D)) || (entityliving.e(this.i, this.j, this.k) >= 1.0D) || (this.b.aI().nextFloat() < 0.05F)))
	    {
	      this.i = entityliving.locX;
	      this.j = entityliving.boundingBox.b;
	      this.k = entityliving.locZ;
	      this.h = (4 + this.b.aI().nextInt(7));
	      if (d0 > 1024.0D) {
	        this.h += 10;
	      } else if (d0 > 256.0D) {
	        this.h += 5;
	      }
	      b.a(entityliving, 30F, 30F);
	      if (!this.b.getNavigation().a(entityliving, this.d)) {
	        this.h += 15;
	      }
	    }
	    this.c = Math.max(this.c - 1, 0);
	    if ((d0 <= d1) && (this.c <= 20))
	    {
	      this.c = 20;
	      if (this.b.be() != null) {
	        this.b.ba();
	      }
	      this.b.getGoalTarget().damageEntity(DamageSource.mobAttack(b), (float) b.getAttributeInstance(GenericAttributes.e).getValue());
	    }
	    b.a(entityliving, 30F, 30F);
	  }
	  protected double a(EntityLiving paramEntityLiving)
	  {
	    return this.b.width * 2.0F * (this.b.width * 2.0F) + paramEntityLiving.width;
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
}