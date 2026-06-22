package me.mossfet.damagetint;

import me.mossfet.damagetint.commands.TintCommand;
import me.mossfet.damagetint.hud.DamageTintHud;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DamageTint implements ClientModInitializer {
	public static final String MOD_ID = "damagetint";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Runs the mod initializer on the client environment.
	 */
	@Override
	public void onInitializeClient() {
		// Initialize Config Files
		if (!TintConfig.instance().getFile().exists()) {
			TintConfig.instance().init();
		} else {
			TintConfig.instance().update();
		}

		ClientCommandRegistrationCallback.EVENT.register(
                TintCommand::register
		);
		HudElementRegistry.addLast(
				DamageTint.id("vignette"),
				DamageTintHud::render
		);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
