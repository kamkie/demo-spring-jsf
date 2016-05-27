package com.example.utils;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

public class NanosOfMicroSecond implements TemporalField {

	public static final TemporalField INSTANCE = new NanosOfMicroSecond();

	private final String name;
	private final TemporalUnit baseUnit;
	private final TemporalUnit rangeUnit;
	private final ValueRange range;
	private final String displayNameKey;

	private NanosOfMicroSecond() {
		this("NanosOfMicroSecond", ChronoUnit.NANOS, ChronoUnit.MICROS, ValueRange.of(0, 999));
	}

	private NanosOfMicroSecond(String name, TemporalUnit baseUnit, TemporalUnit rangeUnit, ValueRange range) {
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
		return ChronoField.NANO_OF_SECOND.getFrom(temporal) % 1_000;
	}

	@Override
	public <R extends Temporal> R adjustInto(R temporal, long newValue) {
		return (R) temporal.with(this, newValue);
	}
}
