package com.example.utils;

import com.google.common.base.MoreObjects;

import java.time.temporal.*;

public enum SubMillis implements TemporalField {

    MICROS_OF_MILLI_SECOND("MicrosOfMilliSecond", ChronoUnit.MICROS, ChronoUnit.MILLIS, ValueRange.of(0, 999)) {
        @Override
        public long getFrom(TemporalAccessor temporal) {
            return ChronoField.NANO_OF_SECOND.getFrom(temporal) % 1_000_000 / 1000;
        }
    },
    NANOS_OF_MICRO_SECOND("NanosOfMicroSecond", ChronoUnit.NANOS, ChronoUnit.MICROS, ValueRange.of(0, 999)) {
        @Override
        public long getFrom(TemporalAccessor temporal) {
            return ChronoField.NANO_OF_SECOND.getFrom(temporal) % 1_000;
        }
    };

    private final String name;
    private final TemporalUnit baseUnit;
    private final TemporalUnit rangeUnit;
    private final ValueRange range;

    SubMillis(String name, TemporalUnit baseUnit, TemporalUnit rangeUnit, ValueRange range) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.range = range;
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
    @SuppressWarnings("unchecked")
    public <R extends Temporal> R adjustInto(R temporal, long newValue) {
        return (R) temporal.with(this, newValue);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("baseUnit", baseUnit)
                .add("rangeUnit", rangeUnit)
                .add("range", range)
                .toString();
    }

}
