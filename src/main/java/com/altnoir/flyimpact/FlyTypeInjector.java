package com.altnoir.flyimpact;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * PoopSky 通过数据包合并苍蝇品种。若合并未生效，则在重载后补上附属品种。
 */
public final class FlyTypeInjector {
    private FlyTypeInjector() {
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener((ResourceManagerReloadListener) FlyTypeInjector::onResourceReload);
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ensureExtraTypesRegistered();
    }

    private static void onResourceReload(ResourceManager resourceManager) {
        ensureExtraTypesRegistered();
    }

    @SuppressWarnings("unchecked")
    static void ensureExtraTypesRegistered() {
        try {
            Class<?> managerClass = Class.forName("com.altnoir.poopsky.content.FlyTypeManager");
            Object instance = managerClass.getField("INSTANCE").get(null);
            List<String> types = (List<String>) managerClass.getMethod("getFlyTypes").invoke(instance);

            List<String> missing = Flyimpact.EXTRA_FLY_TYPES.stream()
                    .filter(id -> !types.contains(id))
                    .toList();
            if (missing.isEmpty()) {
                return;
            }

            Field field = managerClass.getDeclaredField("flyTypes");
            field.setAccessible(true);
            List<String> copy = new ArrayList<>(types);
            copy.addAll(missing);
            field.set(instance, List.copyOf(copy));
            Flyimpact.LOGGER.info("Registered extra fly types with PoopSky FlyTypeManager: {}", missing);
        } catch (ReflectiveOperationException exception) {
            Flyimpact.LOGGER.warn("Could not register extra fly types: {}", exception.toString());
        }
    }
}
