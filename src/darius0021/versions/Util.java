package darius0021.versions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class Util {

    @SuppressWarnings("rawtypes")
    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;

        try {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            o = field.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static ItemStack CreateItem(String material, int size) {
        try {
            String[] data = material.split(":");
            ItemStack item = new ItemStack(Material.valueOf(data[0]), data.length == 2 ? Short.parseShort(data[1]) : (short) 0, (short) size);
            return item;
        } catch (Exception e) {
            return new ItemStack(Material.AIR);
        }
    }
}
