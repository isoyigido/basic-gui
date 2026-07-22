package io.github.isoyigido.basic.gui.core.loader.parameters.numbers;

import io.github.isoyigido.basic.gui.core.loader.Parameter;
import io.github.isoyigido.basic.gui.core.loader.parameters.NumberParameter;

/// Represents a number parameter for {@link Float} values. Uses the parser function {@link Float#parseFloat(String)}.
/// Provides the methods {@link #positive()}, {@link #negative()}, {@link #notNegative()} and {@link #notPositive()}
/// for creating new `FloatParameter` instances that are only allowed to store positive, negative, non-negative,
/// and non-positive floating-point numbers respectively.
/// @see Float
/// @see NumberParameter
/// @see Parameter
public class FloatParameter extends NumberParameter<Float> {
    /// Constructs a number parameter for floating-point numbers with the parser function {@link Float#parseFloat(String)}.
    /// @see Float#parseFloat(String)
    /// @see NumberParameter
    /// @see Parameter
    public FloatParameter() {
        // Use Float.parseFloat(String)
        super(Float::parseFloat);
    }

    /// Returns a new `FloatParameter` instance that is only allowed to store positive values.
    /// @return the new `FloatParameter` instance
    /// @implNote This method creates a new `FloatParameter` instance with the default parameterless constructor,
    ///           and sets its minimum allowed value to {@value Float#MIN_VALUE}.
    public static FloatParameter positive() {
        // Return a new FloatParameter instance with a minimum allowed value of the smallest positive number
        FloatParameter floatParameter = new FloatParameter();
        floatParameter.setMinimumValue(Float.MIN_VALUE);
        return floatParameter;
    }

    /// Returns a new `FloatParameter` instance that is only allowed to store negative values.
    /// @return the new `FloatParameter` instance
    /// @implNote This method creates a new `FloatParameter` instance with the default parameterless constructor,
    ///           and sets its maximum allowed value to -{@value Float#MIN_VALUE}.
    public static FloatParameter negative() {
        // Return a new FloatParameter instance with a maximum allowed value of the largest negative number
        FloatParameter floatParameter = new FloatParameter();
        floatParameter.setMaximumValue(-Float.MIN_VALUE);
        return floatParameter;
    }

    /// Returns a new `FloatParameter` instance that is only allowed to store non-negative values.
    /// @return the new `FloatParameter` instance
    /// @implNote This method creates a new `FloatParameter` instance with the default parameterless constructor,
    ///           and sets its minimum allowed value to 0.
    public static FloatParameter notNegative() {
        // Return a new FloatParameter instance with a minimum allowed value of 0.0f
        FloatParameter floatParameter = new FloatParameter();
        floatParameter.setMinimumValue(0.0f);
        return floatParameter;
    }

    /// Returns a new `FloatParameter` instance that is only allowed to store non-positive values.
    /// @return the new `FloatParameter` instance
    /// @implNote This method creates a new `FloatParameter` instance with the default parameterless constructor,
    ///           and sets its maximum allowed value to 0.
    public static FloatParameter notPositive() {
        // Return a new FloatParameter instance with a maximum allowed value of 0.0f
        FloatParameter floatParameter = new FloatParameter();
        floatParameter.setMaximumValue(0.0f);
        return floatParameter;
    }
}