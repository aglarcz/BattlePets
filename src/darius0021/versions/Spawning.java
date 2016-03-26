package darius0021.versions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;

import darius0021.BattleMobs;

public interface Spawning {
	public LivingEntity SpawnCreature (PlayerInteractEvent event, BattleMobs plugin);
	public void update (LivingEntity pet, BattleMobs plugin);
	public void setTarget (LivingEntity pet, LivingEntity target);
	public void returnas(LivingEntity pet);
	public void nameupdate(LivingEntity pet);
	public void load();
}
