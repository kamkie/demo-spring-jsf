package com.example.utils;

import java.time.temporal.*;

public class MicrosOfMilliSecond implements TemporalField {

    public static final TemporalField INSTANCE = new MicrosOfMilliSecond();

    private final String name;
    private final TemporalUnit baseUnit;
    private final TemporalUnit rangeUnit;
    private final ValueRange range;
    private final String displayNameKey;

    private MicrosOfMilliSecond() {
        this("MicrosOfMilliSecond", ChronoUnit.MICROS, ChronoUnit.MILLIS, ValueRange.of(0, 999));
    }

    private MicrosOfMilliSecond(String name, TemporalUnit baseUnit, TemporalUnit rangeUnit, ValueRange range) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.range = range;
        this.displayNameKey = null;
    }

    @Override
    public TemporalUnit getBaseUnit() {
        return baseUnit;
    }

    @Override
    public TemporalUnit getRangeUnit() {
        return rangeUnit;
    }

    @Override
    public ValueRange range() {
        return range;
    }

    @Override
    public boolean isDateBased() {
        return false;
    }

    @Override
    public boolean isTimeBased() {
        return true;
    }

    @Override
    public boolean isSupportedBy(TemporalAccessor temporal) {
        return temporal.isSupported(this);
    }

    @Override
    public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
        return temporal.range(this);
    }

    @Override
    public long getFrom(TemporalAccessor temporal) {
        return ChronoField.NANO_OF_SECOND.getFrom(temporal) % 1_000_000 / 1000;
    }

    @Override
    public <R extends Temporal> R adjustInto(R temporal, long newValue) {
        return (R) temporal.with(this, newValue);
    }
}
