package com.edenxander.autofish;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class AutoFishClient implements ClientModInitializer {

    private static KeyBinding toggleAutoFishKey;
    private static KeyBinding unlockMouseKey;

    private static boolean autoFishEnabled = false;
    private static boolean mouseUnlocked = false;

    private static int recastCooldown = 0;
    private static int biteCooldown = 0;

    @Override
    public void onInitializeClient() {
        toggleAutoFishKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autofish-mouseunlock.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.autofish-mouseunlock"
        ));

        unlockMouseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autofish-mouseunlock.unlockmouse",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "category.autofish-mouseunlock"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // Toggle AutoFish
            while (toggleAutoFishKey.wasPressed()) {
                autoFishEnabled = !autoFishEnabled;
                client.player.sendMessage(Text.literal("AutoFish: " + (autoFishEnabled ? "§aON" : "§cOFF")), true);
            }

            // Toggle Mouse Unlock
            while (unlockMouseKey.wasPressed()) {
                mouseUnlocked = !mouseUnlocked;
                if (mouseUnlocked) {
                    client.mouse.unlockCursor();
                    client.player.sendMessage(Text.literal("Mouse: §aUNLOCKED"), true);
                } else {
                    client.mouse.lockCursor();
                    client.player.sendMessage(Text.literal("Mouse: §cLOCKED"), true);
                }
            }

            // Keep mouse unlocked if flag is set (re-apply every tick so game doesn't re-grab)
            if (mouseUnlocked && client.currentScreen == null) {
                client.mouse.unlockCursor();
            }

            if (!autoFishEnabled) return;

            if (recastCooldown > 0) {
                recastCooldown--;
                return;
            }
            if (biteCooldown > 0) {
                biteCooldown--;
            }

            ItemStack main = client.player.getMainHandStack();
            ItemStack off = client.player.getOffHandStack();
            boolean holdingRod = main.getItem() instanceof FishingRodItem || off.getItem() instanceof FishingRodItem;
            if (!holdingRod) return;

            FishingBobberEntity bobber = client.player.fishHook;

            if (bobber == null) {
                // No bobber → cast (same as player_press_use)
                useRod(client);
                recastCooldown = 10; // small delay after cast
                return;
            }

            // Bite detection – mirrors the "!!!" check
            // Primary: bobber has caught a fish (vanilla field)
            // Fallback: sudden downward velocity (classic autofish method)
            boolean bite = false;
            try {
                // Access the private caughtFish via the public isInOpenWater / or velocity
                // In 1.21 the field is still present; many mods check velocity for reliability
                if (bobber.getVelocity().y < -0.04 && bobber.isOnGround() == false && bobber.getY() < client.player.getY()) {
                    // sudden pull down = bite
                    bite = true;
                }
            } catch (Exception ignored) {}

            // Extra safety: if the bobber has been out long enough and velocity spikes
            if (bite && biteCooldown <= 0) {
                useRod(client); // reel in
                biteCooldown = 15;
                recastCooldown = 20; // wait before recast (matches the 0.3s sleep)
            }
        });
    }

    private void useRod(MinecraftClient client) {
        if (client.interactionManager == null || client.player == null) return;
        Hand hand = client.player.getMainHandStack().getItem() instanceof FishingRodItem ? Hand.MAIN_HAND : Hand.OFF_HAND;
        client.interactionManager.interactItem(client.player, hand);
        client.player.swingHand(hand);
    }
}
