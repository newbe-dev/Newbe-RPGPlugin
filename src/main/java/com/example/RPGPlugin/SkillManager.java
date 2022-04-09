package com.example.RPGPlugin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SkillManager {

    public static boolean canUse(Player player, String skillName) {

        if(player.getLevel() < SerializeManager.yml.getInt(String.format("SkillInfo.%s.requireLevel", skillName))) { // 요구 레벨보다 낮다면
            return false;
        }

        return getElapsedTime(player.getUniqueId(), skillName) > getSkillCdt(skillName);
    }

    public static void useSkill(UUID uuid, String skillName) {
        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Skill.%s.%s", uuid, skillName));
        if (node == null) {
            node = SerializeManager.yml.createSection(String.format("Plugin.Skill.%s.%s", uuid, skillName));
        }

        node.set("lastUse", System.currentTimeMillis());
    }

    public static long getSkillCdt(String skillName) {
        return SerializeManager.yml.getLong(String.format("SkillInfo.%s.cdt", skillName));
    }

    public static long getElapsedTime(UUID uuid, String skillName) {
        ConfigurationSection node = SerializeManager.yml.getConfigurationSection(String.format("Plugin.Skill.%s.%s", uuid, skillName));
        if (node == null) {
            node = SerializeManager.yml.createSection(String.format("Plugin.Skill.%s.%s", uuid, skillName));
        }

        return (System.currentTimeMillis() - node.getLong("lastUse")); // / 1000
    }
}
