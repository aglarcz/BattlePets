package darius0021.versions;

import darius0021.BattlePets;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;

public interface Spawning {
    public LivingEntity SpawnCreature(PlayerInteractEvent event, BattlePets plugin);

    public void update(LivingEntity pet, BattlePets plugin);

    public void setTarget(LivingEntity pet, LivingEntity target);

    public void returnPet(LivingEntity pet);

    public void nameUpdate(LivingEntity pet);

    public void load();
}
