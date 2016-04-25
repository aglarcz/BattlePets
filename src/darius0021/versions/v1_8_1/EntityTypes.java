package darius0021.versions.v1_8_1;

import darius0021.versions.Util;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum EntityTypes {
    BLOCK("Block", 9999, ArmorStandPlus.class) {},
    BAT("Bat", 65, BatPet.class) {},
    WITHER("Bat", 64, WitherPet.class) {};


    @SuppressWarnings("rawtypes")
    public static Map<String, Class> mobs = new HashMap<String, Class>();

    private EntityTypes(String name, int id, Class<? extends Entity> custom) {
        addToMaps(custom, name, id);
    }

    public static void loadMobs() {
        mobs.put("block", ArmorStandPlus.class);
        mobs.put("magma_cube", EntityMagmaCube.class);
        mobs.put("slime", EntitySlime.class);
        mobs.put("cave_spider", EntityCaveSpider.class);
        mobs.put("chicken", EntityChicken.class);
        mobs.put("cow", EntityCow.class);
        mobs.put("creeper", EntityCreeper.class);
        mobs.put("horse", EntityHorse.class);
        mobs.put("iron_golem", EntityIronGolem.class);
        mobs.put("mushroom_cow", EntityMushroomCow.class);
        mobs.put("ocelot", EntityOcelot.class);
        mobs.put("pig", EntityPig.class);
        mobs.put("pig_zombie", EntityPigZombie.class);
        mobs.put("rabbit", EntityRabbit.class);
        mobs.put("sheep", EntitySheep.class);
        mobs.put("silverfish", EntitySilverfish.class);
        mobs.put("skeleton", EntitySkeleton.class);
        mobs.put("snowman", EntitySnowman.class);
        mobs.put("spider", EntitySpider.class);
        mobs.put("villager", EntityVillager.class);
        mobs.put("wolf", EntityWolf.class);
        mobs.put("zombie", EntityZombie.class);
        mobs.put("bat", BatPet.class);
        mobs.put("witch", EntityWitch.class);
        mobs.put("wither", WitherPet.class);
        mobs.put("armorstand", EntityArmorStand.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Entity createEntity(String name, World world) {
        Entity entity = null;
        Class localClass = mobs.get(name);
        if (localClass != null) {
            try {
                if (name.equalsIgnoreCase("wither") || name.equalsIgnoreCase("bat")) {
                    entity = (Entity) localClass.getConstructor(new Class[]{World.class, boolean.class}).newInstance(new Object[]{world, true});
                } else
                    entity = (Entity) localClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
            } catch (Exception e) {
                Bukkit.getLogger().info("[ERROR] Battlepets: No such mobtype: " + name);
            }
        }
        return entity;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Entity spawnEntity(Entity entity, Location loc) {
        World world = entity.world;
        entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        //((CraftWorld)loc.getWorld()).getHandle().addEntity(entity, SpawnReason.CUSTOM);

        int x = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);
        world.getChunkAt(x, j).a(entity);
        world.entityList.add(entity);
        List u = (List) Util.getPrivateField("u", World.class, world);
        for (int i = 0; i < u.size(); i++) {
            ((IWorldAccess) u.get(i)).a(entity);
        }
        IntHashMap entities = (IntHashMap) Util.getPrivateField("entitiesById", World.class, world);
        entities.a(entity.getId(), entity);
        entity.valid = true;
        return entity;

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addToMaps(Class clazz, String name, int id) {
        ((Map) Util.getPrivateField("c", net.minecraft.server.v1_8_R1.EntityTypes.class, null)).put(name, clazz);
        ((Map) Util.getPrivateField("d", net.minecraft.server.v1_8_R1.EntityTypes.class, null)).put(clazz, name);
        //((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(Integer.valueOf(id), clazz);
        ((Map) Util.getPrivateField("f", net.minecraft.server.v1_8_R1.EntityTypes.class, null)).put(clazz, Integer.valueOf(id));
        //((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, Integer.valueOf(id));
    }
}