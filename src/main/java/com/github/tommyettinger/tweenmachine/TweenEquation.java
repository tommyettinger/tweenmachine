package com.github.tommyettinger.tweenmachine;

/**
 * A simple wrapper around an {@link TweenFunction} so it is associated with a String {@link #tag}. This
 * also implements TweenFunction, and wraps the {@link #fn} it stores to clamp its input to the 0 to 1
 * range (preventing potentially troublesome complications when large or negative inputs come in).
 */
public class TweenEquation implements TweenFunction {
    /**
     * A unique String that identifies this object.
     *
     * @see #getTag()
     */
    public final String tag;
    /**
     * The TweenFunction this actually uses to do its math work.
     *
     * @see #getFn()
     */
    public final TweenFunction fn;

    /**
     * Calls {@link #TweenEquation(String, TweenFunction)} with {@code "Linear.INOUT"} and
     * {@link TweenEquations#linearFunction}.
     * Because {@link TweenEquations#linear} is already registered with that tag and function, this isn't very useful.
     */
    public TweenEquation() {
        this("Linear.INOUT", TweenEquations.linearFunction);
    }

    /**
     * Creates a TweenEquation that will use the given {@code fn} and registers it with the given tag. The tag must
     * be unique; if {@link TweenEquations#get(String)} returns a non-null value when looking up the given tag, then
     * if you create a TweenEquation with that tag, the existing value will be overwritten.
     *
     * @param tag a unique String that can be used as a key to access this with {@link TweenEquations#get(String)}
     * @param fn  an {@link TweenFunction} to wrap
     */
    public TweenEquation(String tag, TweenFunction fn) {
        this.tag = tag;
        this.fn = fn;
        TweenEquations.REGISTRY.put(tag, this);
    }

    /**
     * Does bounds-checking on the input before passing it to {@link #fn}. If alpha is less than 0, it is treated as
     * 0; if alpha is greater than 1, it is treated as 1. Note that the output is still unrestricted, so
     * TweenFunctions that can produce results outside the 0-1 range still can do that.
     *
     * @param alpha almost always between 0 and 1, inclusive, and will be clamped to ensure that
     * @return an interpolated value based on alpha, which may (for some functions) be negative, or greater than 1
     */
    @Override
    public float compute(float alpha) {
        return fn.compute(Math.min(Math.max(alpha, 0f), 1f));
    }

    /**
     * Gets the tag for this TweenEquation, which is a unique String that identifies this object. If another
     * TweenEquation tries to use the same tag, this TweenEquation will be un-registered and will no longer be
     * returnable from {@link TweenEquations#get(String)}.
     *
     * @return the tag String
     */
    public String getTag() {
        return tag;
    }

    /**
     * Gets the TweenFunction this actually uses to do its math work. Calling this function on its own does
     * not behave the same way as calling {@link TweenEquation#compute(float)} on this TweenEquation; the TweenEquation
     * method clamps the result if the {@code alpha} parameter is below 0 or above 1.
     *
     * @return the TweenFunction this uses
     */
    public TweenFunction getFn() {
        return fn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TweenEquation that = (TweenEquation) o;

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
