package com.ubs.backend.classes.enums;

/**
 * Enum to easily convert different units of Bytes
 *
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
public enum ByteConversion {
    BYTE((int) Math.pow(1024, 0)),
    KILOBYTE((int) Math.pow(1024, 1)),
    MEGABYTE((int) Math.pow(1024, 2)),
    GIGABYTE((int) Math.pow(1024, 3));

    /**
     * The value with which a value has to be multiplied by to get it in Bytes
     *
     * @since 17.07.2021
     */
    private final int toByte;

    /**
     * Default Constructor
     *
     * @param toByte The value with which a value has to be multiplied by to get it in Bytes
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    ByteConversion(int toByte) {
        this.toByte = toByte;
    }

    /**
     * Converts a Number from one Byte Unit to another
     *
     * @param size     the Size in fromUnit
     * @param fromUnit the origin Unit
     * @param toUnit   the target Unit
     * @return the size converted from the fromUnit to the toUnit
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public static double convert(double size, ByteConversion fromUnit, ByteConversion toUnit) {
        return size * fromUnit.toByte / toUnit.toByte;
    }

    /**
     * Converts a Number from one Byte Unit to another
     *
     * @param size   the Size in fromUnit
     * @param toUnit the target Unit
     * @return the Size converted from this Unit to the toUnit
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public double convert(double size, ByteConversion toUnit) {
        return convert(size, this, toUnit);
    }
}
