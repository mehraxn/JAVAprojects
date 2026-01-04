package com.weather.report.reports;

import java.util.Objects;

public class GatewayRange<T extends Comparable<T>>
    implements Report.Range<T>, Comparable<Report.Range<T>> {

  private final T start;
  private final T end;
  private final boolean isLast;

  // Constructor for normal buckets
  public GatewayRange(T start, T end, boolean isLast) {
    this.start = start;
    this.end = end;
    this.isLast = isLast;
  }

  // Convenience constructor (used when min == max)
  public GatewayRange(T start, T end) {
    this(start, end, true);
  }

  @Override
  public T getStart() {
    return start;
  }

  @Override
  public T getEnd() {
    return end;
  }

  @Override
  public boolean contains(T value) {
    if (value.compareTo(start) < 0) return false;
    return isLast
        ? value.compareTo(end) <= 0
        : value.compareTo(end) < 0;
  }

  @Override
  public int compareTo(Report.Range<T> other) {
    return this.start.compareTo(other.getStart());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Report.Range<?> r)) return false;
    return Objects.equals(start, r.getStart()) &&
           Objects.equals(end, r.getEnd());
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end);
  }

  @Override
  public String toString() {
    return "[" + start + ", " + end + (isLast ? "]" : ")");
  }
}
