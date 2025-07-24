package me.fertiz.netflux.util;

import java.util.EnumSet;

public enum DataUnit {
    // Bits (1024-based)
    BIT(1),
    KILOBIT(1024),
    MEGABIT(1024 * 1024),
    GIGABIT(1024L * 1024 * 1024),
    TERABIT(1024L * 1024 * 1024 * 1024),

    // Bytes (1000-based)
    BYTE(8),
    KILOBYTE(8 * 1_000),
    MEGABYTE(8 * 1_000_000),
    GIGABYTE(8L * 1_000_000_000),
    TERABYTE(8L * 1_000_000_000_000L);

    private final long bits;

    DataUnit(long bits) {
        this.bits = bits;
    }

    public long convertTo(DataUnit target, double value) {
        return (long) ((value * this.bits) / (double) target.bits);
    }

    public long toBits(double value) {
        return (long) (value * this.bits);
    }

    public long fromBits(long bits) {
        return bits / this.bits;
    }

    public static EnumSet<DataUnit> bitUnits() {
        return EnumSet.of(BIT, KILOBIT, MEGABIT, GIGABIT, TERABIT);
    }

    public static EnumSet<DataUnit> byteUnits() {
        return EnumSet.of(BYTE, KILOBYTE, MEGABYTE, GIGABYTE, TERABYTE);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
