package me.flamin.lilypadOnlinePlayers;

import java.util.EnumSet;
import java.util.Set;

public class Verbosity {
    /**
     * Translates a numeric verbosity level into a set of VerbosityLevels enums.
     *
     * @param verbosity
     * @return EnumSet representing verbosity
     */
    public static EnumSet<VerbosityLevels> parseFlags(long verbosity) {
        EnumSet<VerbosityLevels> flags = EnumSet.noneOf(VerbosityLevels.class);
        for (VerbosityLevels level : EnumSet.allOf(VerbosityLevels.class)) {
            if ((level.getLevel() & verbosity) == level.getLevel()) {
                flags.add(level);
            }
        }
        return flags;
    }

    /**
     * Translates a set of VerbosityLevels enums into a numeric representation.
     *
     * @param levels Set of {@link me.flamin.lilypadOnlinePlayers.Verbosity.VerbosityLevels}
     * @return numeric representation of the verbosity level
     */
    public static long getStatusValue(Set<VerbosityLevels> levels) {
        long value = 0;
        for (VerbosityLevels level : levels) {
            value |= level.getLevel();
        }
        return value;
    }


    public enum VerbosityLevels {
        SHOW_MOVEMENTS(1 << 0),
        SHOW_UUIDS(1 << 1),
        SHOW_EXPIRATIONS(1 << 2),
        SHOW_PACKETS(1 << 3);

        private final long level;

        VerbosityLevels(long level) {
            this.level = level;
        }

        public long getLevel() {
            return level;
        }
    }
}
