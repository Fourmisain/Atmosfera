/*
 * Copyright 2021 Haven King
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.hephaestus.atmosfera;

import dev.hephaestus.atmosfera.client.sound.AtmosphericSoundDefinition;
import dev.hephaestus.atmosfera.client.sound.AtmosphericSoundSerializer;
import dev.hephaestus.atmosfera.world.context.EnvironmentContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Atmosfera implements ClientModInitializer {
	public static final String MODID = "atmosfera";
	public static final String MOD_NAME = "Atmosfera";
	private static final Logger LOG = LogManager.getLogger(MOD_NAME);

	public static final Map<Identifier, AtmosphericSoundDefinition> SOUND_DEFINITIONS = new HashMap<>();
	public static final Map<Identifier, AtmosphericSoundDefinition> MUSIC_DEFINITIONS = new HashMap<>();

	public static void debug(String message, Object... args) {
		if (AtmosferaConfig.printDebugMessages()) {
			LOG.info("[" + MOD_NAME + "] " +  message, args);
		}
	}

	public static void log(String message, Object... args) {
		LOG.info("[" + MOD_NAME + "] " +  message, args);
	}

	public static void warn(String message, Object... args) {
		LOG.warn("[" + MOD_NAME + "] " +  message, args);
	}

	public static void error(String message, Object... args) {
		LOG.error("[" + MOD_NAME + "] " +  message, args);
	}

	@Override
	public void onInitializeClient() {
		FabricLoader.getInstance().getModContainer(MODID).ifPresent(modContainer -> {
			ResourceManagerHelper.registerBuiltinResourcePack(id("dungeons"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);

			ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
					new AtmosphericSoundSerializer("sounds/ambient", SOUND_DEFINITIONS));
			ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
					new AtmosphericSoundSerializer("sounds/music", MUSIC_DEFINITIONS));
		});

		EnvironmentContext.init();

		log("Finished initialization.");
	}

	public static Identifier id(@NotNull String path, String... paths) {
		return new Identifier(MODID, path + (paths.length == 0 ? "" : "." + String.join(".", paths)));
	}
}
