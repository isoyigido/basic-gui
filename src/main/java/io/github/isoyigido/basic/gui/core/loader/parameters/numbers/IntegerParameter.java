package io.github.isoyigido.basic.gui.core.loader.parameters.numbers;

import io.github.isoyigido.basic.gui.core.loader.Parameter;
import io.github.isoyigido.basic.gui.core.loader.parameters.NumberParameter;

/// Represents a number parameter for {@link Integer} values. Uses the parser function {@link Integer#parseInt(String)}.
/// Provides the methods {@link #positive()}, {@link #negative()}, {@link #notNegative()} and {@link #notPositive()}
/// for creating new `IntegerParameter` instances that are only allowed to store positive, negative, non-negative,
/// and non-positive integers respectively.
/// @see Integer
/// @see NumberParameter
/// @see Parameter
public class IntegerParameter extends NumberParameter<Integer> {
    /// Constructs a number parameter for integers with the parser function {@link Integer#parseInt(String)}.
    /// @see Integer#parseInt(String)
    /// @see NumberParameter
    /// @see Parameter
    public IntegerParameter() {
        // Use Integer.parseInt(String)
        super(Integer::parseInt);
    }

    /// Returns a new `IntegerParameter` instance that is only allowed to store positive values.
    /// @return the new `IntegerParameter` instance
    /// @implNote This method creates a new `IntegerParameter` instance with the default parameterless constructor,
    ///           and sets its minimum allowed value to 1.
    public static IntegerParameter positive() {
        // Return a new IntegerParameter instance with a minimum allowed value of 1
        IntegerParameter integerParameter = new IntegerParameter();
        integerParameter.setMinimumValue(1);
        return integerParameter;
    }

    /// Returns a new `IntegerParameter` instance that is only allowed to store negative values.
    /// @return the new `IntegerParameter` instance
    /// @implNote This method creates a new `IntegerParameter` instance with the default parameterless constructor,
    ///           and sets its maximum allowed value to -1.
    public static IntegerParameter negative() {
        // Return a new IntegerParameter instance with a maximum allowed value of -1
        IntegerParameter integerParameter = new IntegerParameter();
        integerParameter.setMaximumValue(-1);
        return integerParameter;
    }

    /// Returns a new `IntegerParameter` instance that is only allowed to store non-negative values.
    /// @return the new `IntegerParameter` instance
    /// @implNote This method creates a new `IntegerParameter` instance with the default parameterless constructor,
    ///           and sets its minimum allowed value to 0.
    public static IntegerParameter notNegative() {
        // Return a new IntegerParameter instance with a minimum allowed value of 0
        IntegerParameter integerParameter = new IntegerParameter();
        integerParameter.setMinimumValue(0);
        return integerParameter;
    }

    /// Returns a new `IntegerParameter` instance that is only allowed to store non-positive values.
    /// @return the new `IntegerParameter` instance
    /// @implNote This method creates a new `IntegerParameter` instance with the default parameterless constructor,
    ///           and sets its maximum allowed value to 0.
    public static IntegerParameter notPositive() {
        // Return a new IntegerParameter instance with a maximum allowed value of 0
        IntegerParameter integerParameter = new IntegerParameter();
        integerParameter.setMaximumValue(0);
        return integerParameter;
    }
}