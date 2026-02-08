package com.github.tommyettinger.tweenmachine;

/**
 * A simple wrapper around an {@link InterpolationFunction} so it is associated with a String {@link #tag}. This
 * also implements InterpolationFunction, and wraps the {@link #fn} it stores to clamp its input to the 0 to 1
 * range (preventing potentially troublesome complications when large or negative inputs come in).
 */
public class Interpolator implements InterpolationFunction {
    /**
     * A unique String that identifies this object.
     *
     * @see #getTag()
     */
    public final String tag;
    /**
     * The InterpolationFunction this actually uses to do its math work.
     *
     * @see #getFn()
     */
    public final InterpolationFunction fn;

    /**
     * Calls {@link #Interpolator(String, InterpolationFunction)} with {@code "Linear.INOUT"} and
     * {@link Interpolations#linearFunction}.
     * Because {@link Interpolations#linear} is already registered with that tag and function, this isn't very useful.
     */
    public Interpolator() {
        this("Linear.INOUT", Interpolations.linearFunction);
    }

    /**
     * Creates an Interpolator that will use the given {@code fn} and registers it with the given tag. The tag must
     * be unique; if {@link Interpolations#get(String)} returns a non-null value when looking up the given tag, then
     * if you create an Interpolator with that tag, the existing value will be overwritten.
     *
     * @param tag a unique String that can be used as a key to access this with {@link Interpolations#get(String)}
     * @param fn  an {@link InterpolationFunction} to wrap
     */
    public Interpolator(String tag, InterpolationFunction fn) {
        this.tag = tag;
        this.fn = fn;
        Interpolations.REGISTRY.put(tag, this);
    }

    /**
     * Does bounds-checking on the input before passing it to {@link #fn}. If alpha is less than 0, it is treated as
     * 0; if alpha is greater than 1, it is treated as 1. Note that the output is still unrestricted, so
     * InterpolationFunctions that can produce results outside the 0-1 range still can do that.
     *
     * @param alpha almost always between 0 and 1, inclusive, and will be clamped to ensure that
     * @return an interpolated value based on alpha, which may (for some functions) be negative, or greater than 1
     */
    @Override
    public float compute(float alpha) {
        return fn.compute(Math.min(Math.max(alpha, 0f), 1f));
    }

    /**
     * Gets the tag for this Interpolator, which is a unique String that identifies this object. If another
     * Interpolator tries to use the same tag, this Interpolator will be un-registered and will no longer be
     * returnable from {@link Interpolations#get(String)}.
     *
     * @return the tag String
     */
    public String getTag() {
        return tag;
    }

    /**
     * Gets the InterpolationFunction this actually uses to do its math work. Calling this function on its own does
     * not behave the same way as calling {@link Interpolator#compute(float)} on this Interpolator; the Interpolator
     * method clamps the result if the {@code alpha} parameter is below 0 or above 1.
     *
     * @return the InterpolationFunction this uses
     */
    public InterpolationFunction getFn() {
        return fn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interpolator that = (Interpolator) o;

        return tag.equals(that.tag);
    }

    @Override
    public int hashCode() {
        return tag.hashCode();
    }

    @Override
    public String toString() {
        return tag;
    }
}
