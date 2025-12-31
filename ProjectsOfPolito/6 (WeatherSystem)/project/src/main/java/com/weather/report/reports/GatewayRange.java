package com.weather.report.reports;

import java.util.Objects;//ADDED FOR R2

public class GatewayRange<T extends Comparable<T>>//ADDED FOR R2
    implements Report.Range<T>, Comparable<Report.Range<T>> {//ADDED FOR R2

  private final T start;//ADDED FOR R2
  private final T end;//ADDED FOR R2
  private final boolean isLast;//ADDED FOR R2

  // Constructor for normal buckets
  public GatewayRange(T start, T end, boolean isLast) {//ADDED FOR R2
    this.start = start;//ADDED FOR R2
    this.end = end;//ADDED FOR R2
    this.isLast = isLast;//ADDED FOR R2
  }

  // Convenience constructor (used when min == max)
  public GatewayRange(T start, T end) {//ADDED FOR R2
    this(start, end, true);//ADDED FOR R2
  }

  @Override//ADDED FOR R2
  public T getStart() {//ADDED FOR R2
    return start;//ADDED FOR R2
  }//ADDED FOR R2

  @Override//ADDED FOR R2
  public T getEnd() {//ADDED FOR R2
    return end;//ADDED FOR R2
  }

  @Override//ADDED FOR R2
  public boolean contains(T value) {//ADDED FOR R2
    if (value.compareTo(start) < 0) return false;//ADDED FOR R2
    return isLast//ADDED FOR R2
        ? value.compareTo(end) <= 0//ADDED FOR R2
        : value.compareTo(end) < 0;//ADDED FOR R2
  }

  @Override//ADDED FOR R2
  public int compareTo(Report.Range<T> other) {//ADDED FOR R2
    return this.start.compareTo(other.getStart());//ADDED FOR R2
  }

  @Override//ADDED FOR R2
  public boolean equals(Object o) {//ADDED FOR R2
    if (this == o) return true;//ADDED FOR R2
    if (!(o instanceof Report.Range<?> r)) return false;//ADDED FOR R2
    return Objects.equals(start, r.getStart()) &&//ADDED FOR R2
           Objects.equals(end, r.getEnd());//ADDED FOR R2
  }//ADDED FOR R2

  @Override//ADDED FOR R2
  public int hashCode() {//ADDED FOR R2
    return Objects.hash(start, end);//ADDED FOR R2
  }

  @Override//ADDED FOR R2
  public String toString() {//ADDED FOR R2
    return "[" + start + ", " + end + (isLast ? "]" : ")");//ADDED FOR R2
  }
}
