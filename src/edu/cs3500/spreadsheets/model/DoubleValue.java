package edu.cs3500.spreadsheets.model;

import java.util.Objects;

/**
 * Formulas that evaluate to values that are doubles. These formulas extend the AbstractValue
 * abstract class and are a type of Formula.
 */
public class DoubleValue extends AbstractValue<Double> {

  /**
   * Public constructor for DoubleValue.
   *
   * @param value double value
   */
  public DoubleValue(double value) {
    this.value = value;
  }

  @Override
  public ValueType getValueType() {
    return ValueType.DOUBLE;
  }

  @Override
  public String getPrintString(Worksheet worksheet) {
    return String.format("%f", this.value);
  }

  @Override
  public <R> R accept(FormulaVisitor<R> visitor) {
    return visitor.visitDoubleValue(this);
  }

  @Override
  public Formula evaluate(Worksheet worksheet) {
    return new DoubleValue(this.value);
  }

  @Override
  public String toString() {
    return String.format("%f", this.value);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DoubleValue)) {
      return false;
    }

    DoubleValue that = (DoubleValue) obj;
    return Math.abs(this.value - that.getValue()) <= 0.000001;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
