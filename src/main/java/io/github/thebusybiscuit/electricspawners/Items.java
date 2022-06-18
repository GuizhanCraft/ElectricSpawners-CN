package io.github.thebusybiscuit.electricspawners;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import net.guizhanss.guizhanlib.minecraft.helper.entity.EntityTypeHelper;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.Map;

public final class Items {

    private static final Map<EntityType, SlimefunItemStack> SPAWNERS = new EnumMap<>(EntityType.class);

    private Items() {}

    public static final SlimefunItemStack FRAMEWORK = new SlimefunItemStack(
        "ELECTRIC_SPAWNER_FRAMEWORK",
        Material.SPAWNER,
        "&d电力刷怪笼框架"
    );

    public static final SlimefunItemStack ASSEMBLER = new SlimefunItemStack(
        "ELECTRIC_SPAWNER_ASSEMBLER",
        Material.SPAWNER,
        "&d电力刷怪笼组装机"
    );

    public static SlimefunItemStack getSpawner(EntityType type) {
        if (SPAWNERS.containsKey(type)) {
            return SPAWNERS.get(type);
        } else {
            SlimefunItemStack itemStack = new SlimefunItemStack(
                "ELECTRIC_SPAWNER_" + type,
                "db6bd9727abb55d5415265789d4f2984781a343c68dcaf57f554a5e9aa1cd",
                "&e电力刷怪笼 &7(" + EntityTypeHelper.getName(type) + ")",
                "",
                "&8\u21E8 &e\u26A1 &7最大实体数量: 6",
                "&8\u21E8 &e\u26A1 &7512 J 可储存",
                "&8\u21E8 &e\u26A1 &7240 J 每个生物"
            );
            SPAWNERS.put(type, itemStack);
            return itemStack;
        }
    }
}
