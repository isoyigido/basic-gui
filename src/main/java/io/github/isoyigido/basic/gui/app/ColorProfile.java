package io.github.isoyigido.basic.gui.app;

/// Stores a color theme.
///
/// Note: The colors are stored as {@link ColorSet} records for easier access to brighter/darker/grayscale variants.
///
/// @param background the background color (usually black or white)
/// @param primary the primary saturated color (blue, orange, etc.)
/// @param secondary the secondary saturated color (orange, blue etc.)
/// @param error the error color (usually red)
/// @param success the success color (usually green)
/// @param text the text color (usually the inverse of the background color)
/// @param placeholderText the placeholder text color (usually gray)
/// @param others other color sets
/// @see ColorSet
public record ColorProfile(
        ColorSet background,
        ColorSet primary,
        ColorSet secondary,
        ColorSet error,
        ColorSet success,
        ColorSet text,
        ColorSet placeholderText,
        ColorSet... others
) {}