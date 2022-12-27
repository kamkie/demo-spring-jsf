package com.example.utils;

import lombok.ToString;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

@ToString
public enum SubMillis implements TemporalField {

    MICROS_OF_MILLI_SECOND(ChronoUnit.MICROS, ChronoUnit.MILLIS, ValueRange.of(0, 999)) {
        @Override
        public long getFrom(TemporalAccessor temporal) {
            return ChronoField.NANO_OF_SECOND.getFrom(temporal) % 1_000_000 / 1000;
        }
    },
    NANOS_OF_MICRO_SECOND(ChronoUnit.NANOS, ChronoUnit.MICROS, ValueRange.of(0, 999)) {
        @Override
        public long getFrom(TemporalAccessor temporal) {
            return ChronoField.NANO_OF_SECOND.getFrom(temporal) % 1_000;
        }
    };

    private final TemporalUnit baseUnit;
    private final TemporalUnit rangeUnit;
    private final ValueRange range_;

    SubMillis(TemporalUnit baseUnit, TemporalUnit rangeUnit, ValueRange range) {
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.range_ = range;
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
        return range_;
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
        return temporal instanceof LocalTime;
    }

    @Override
    public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
        return range_;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Temporal> R adjustInto(R temporal, long newValue) {
        if (isSupportedBy(temporal)) {
            return (R) LocalTime.ofNanoOfDay(getBaseUnit().getDuration().getNano() * newValue);
        }
        throw new UnsupportedTemporalTypeException("only LocalTime is supported");
    }

}
