package com.github.tommyettinger.tweenmachine;

/**
 * A type of function that takes a float from 0 to 1 (usually, and also usually inclusive) and returns a float that
 * is typically in the 0 to 1 range or just outside it. Meant for easier communication with libGDX's Interpolation
 * class by using one of its Interpolation constants with a method reference. This is a functional interface whose
 * functional method is {@link #compute(float)}.
 * <br>
 * This isn't often directly needed in user code; you can very often use existing, predefined Interpolator constants
 * or create new InterpolationFunction instances using methods in Interpolations like
 * {@link Interpolations#expFunction(float, float)}. You can use a lambda as an InterpolationFunction if it takes
 * a float and returns a float.
 */
public interface InterpolationFunction {
    /**
     * Given a float {@code alpha}, which is almost always between 0 and 1 inclusive, gets a typically-different
     * float that is usually (but not always) between 0 and 1 inclusive.
     *
     * @param alpha almost always between 0 and 1, inclusive
     * @return a value that is usually between 0 and 1, inclusive, for inputs between 0 and 1
     */
    float compute(float alpha);

    /**
     * Maps a call to {@link #compute(float)} from the 0-1 range to the {@code start}-{@code end} range.
     * Usually (but not always), this returns a float between start and end, inclusive.
     *
     * @param start the inclusive lower bound; some functions can return less
     * @param end   the inclusive upper bound; some functions can return more
     * @param alpha almost always between 0 and 1, inclusive
     * @return a value that is usually between start and end, inclusive, for alpha between 0 and 1
     */
    default float compute(float start, float end, float alpha) {
        return start + compute(alpha) * (end - start);
    }

    /**
     * Effectively splits the interpolation function at its midway point (where alpha is 0.5) and returns a new
     * InterpolationFunction that interpolates like the first half when alpha is greater than 0.5, and interpolates
     * like the second half when alpha is less than 0.5. In both cases, the returned function will be offset so that
     * it starts at 0 when alpha is 0, ends at 1 when alpha is 1, and returns 0.5 when alpha is 0.5, but only so
     * long as this original InterpolationFunction also has those behaviors. If this InterpolationFunction does not
     * return 0 at 0, 1 at 1, and 0.5 at 0.5, then the InterpolationFunction this returns may not be continuous.
     * <br>
     * This is meant to create variants on "In, .OUT" interpolation functions that instead go "Out, In."
     *
     * @return a new InterpolationFunction that acts like this one, but with its starting and ending halves switched
     */
    default InterpolationFunction flip() {
        return a -> compute(TweenUtils.fract(a + 0.5f)) + Math.copySign(0.5f, a - 0.5f);
    }
}
