package me.mossfet.damagetint;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TintConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DYNAMIC = BUILDER
            .comment("When enabled, the configured threshold is ignored and your current maximum health is used instead")
            .translation("damagetint.configuration.dynamic")
            .define("dynamic", true);

    public static final ModConfigSpec.DoubleValue THRESHOLD_HEALTH = BUILDER
            .comment("The threshold determines when the tint starts appearing (Example: /tint 10 --> Tint starts at 10 HP (5 hearts))")
            .translation("damagetint.configuration.health")
            .defineInRange("health", 20.0, 0.0, Double.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
