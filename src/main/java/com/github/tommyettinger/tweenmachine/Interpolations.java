/*
 * Copyright (c) 2022-2023 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.tommyettinger.tweenmachine;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;

/**
 * Provides predefined {@link Interpolator} constants and ways to generate {@link InterpolationFunction} instances, as
 * well as acting as the registry for known Interpolator values so that they can be looked up by name. This has every
 * <a href="http://robertpenner.com/easing/">Penner easing function</a>, though not all with equivalent behavior. Unlike
 * the original Universal Tween Engine, every easing function that has an IN and an OUT version now also has an INOUT
 * and OUTIN version. The original didn't supply any OUTIN versions.
 * <br>
 * <a href="https://tommyettinger.github.io/digital/interpolators.html">You can view the graphs for every Interpolator here</a>.
 */
public final class Interpolations {
    /**
     * No need to instantiate.
     */
    private Interpolations() {
    }

    /**
     * Maps String tag keys to Interpolator values, storing them typically in insertion order.
     * This is not intended for external use, but is public in case it is ever needed directly.
     * You can also obtain the tags on their own using {@link #getTags()}, or an Array with every Interpolator using
     * {@link #getInterpolators()}.
     */
    public static final OrderedMap<String, Interpolator> REGISTRY = new OrderedMap<>(128);

    /**
     * Looks up the given {@code tag} in a registry of Interpolators, and if there exists one with that name, returns
     * it. Otherwise, this returns null.
     *
     * @param tag a tag used to register an Interpolator here
     * @return the Interpolator registered with the given tag, or null if none exists for that tag
     */
    public static Interpolator get(String tag) {
        return REGISTRY.get(tag);
    }

    /**
     * Gets a direct reference to the tags used as keys for the Interpolator registry. This Array can be sorted if you
     * want, or otherwise rearranged. If new tags are registered, this will reflect those changes.
     *
     * @return an Array containing every String tag registered for an Interpolator; direct reference
     */
    public static Array<String> getTags() {
        return REGISTRY.orderedKeys();
    }

    /**
     * Allocates a new Array of Interpolator items, fills it with every registered Interpolator, and returns that Array.
     * This is not a direct reference; modifying the Array won't change the registered contents.
     *
     * @return a new Array containing every Interpolator registered
     */
    public static Array<Interpolator> getInterpolators() {
        return REGISTRY.values().toArray();
    }

    /**
     * Linear interpolation; just returns its argument.
     */
    public static final InterpolationFunction linearFunction = (a -> a);

    /**
     * Plain linear interpolation, or "lerp"; this just returns the alpha it is given.
     */
    public static final Interpolator linear = new Interpolator("Linear.INOUT", linearFunction);
    /**
     * "Smoothstep" or a cubic Hermite spline.
     * <br>
     * This has been modified slightly for numerical correctness. The form of this spline usually given,
     * {code a * a * (3 - 2 * a)}, can exceed 1 for many inputs that are just less than 1, which makes that form much
     * harder to use in a table lookup because an output larger than 1 could mean an out-of-bounds index. Instead, we
     * use the form {@code a * a * (1 - a - a + 2)}, which always stays in the range 0.0f to 1.0f, inclusive.
     */
    public static final Interpolator smooth = new Interpolator("Smooth.INOUT", a -> a * a * (1 - a - a + 2));
    /**
     * "Smoothstep" or a cubic Hermite spline, but flipped.
     * <br>
     * This has been modified slightly for numerical correctness. The form of this spline usually given,
     * {code a * a * (3 - 2 * a)}, can exceed 1 for many inputs that are just less than 1, which makes that form much
     * harder to use in a table lookup because an output larger than 1 could mean an out-of-bounds index. Instead, we
     * use the form {@code a * a * (1 - a - a + 2)}, which always stays in the range 0.0f to 1.0f, inclusive.
     */
    public static final Interpolator smoothOutIn = new Interpolator("Smooth.OUTIN", smooth.fn.flip());
    /**
     * "Smoothstep" or a cubic Hermite spline, applied twice.
     * <br>
     * This has been modified slightly for numerical correctness. The form of the cubic Hermite spline usually given,
     * {code a * a * (3 - 2 * a)}, can exceed 1 for many inputs that are just less than 1, which makes that form much
     * harder to use in a table lookup because an output larger than 1 could mean an out-of-bounds index. Instead, we
     * use the form {@code a * a * (1 - a - a + 2)}, which always stays in the range 0.0f to 1.0f, inclusive.
     */
    public static final Interpolator smooth2 = new Interpolator("Smooth2.INOUT", a -> (a *= a * (1 - a - a + 2)) * a * (1 - a - a + 2));
    /**
     * "Smoothstep" or a cubic Hermite spline, applied twice, but flipped.
     * <br>
     * This has been modified slightly for numerical correctness. The form of the cubic Hermite spline usually given,
     * {code a * a * (3 - 2 * a)}, can exceed 1 for many inputs that are just less than 1, which makes that form much
     * harder to use in a table lookup because an output larger than 1 could mean an out-of-bounds index. Instead, we
     * use the form {@code a * a * (1 - a - a + 2)}, which always stays in the range 0.0f to 1.0f, inclusive.
     */
    public static final Interpolator smooth2OutIn = new Interpolator("Smooth2.OUTIN", smooth2.fn.flip());
    /**
     * A quintic Hermite spline by Ken Perlin.
     * <br>
     * This was modified slightly because the original constants were meant for doubles, and here we use floats. Without
     * this tiny change (the smallest possible change here, from 10.0f to 9.999998f), giving an input of 0.99999994f, or
     * one of thousands of other inputs, would unexpectedly produce an output greater than 1.0f .
     */
    public static final Interpolator smoother = new Interpolator("Smoother.INOUT", a -> a * a * a * (a * (a * 6f - 15f) + 9.999998f));
    /**
     * A quintic Hermite spline by Ken Perlin, but flipped.
     * <br>
     * This was modified; see {@link #smoother}.
     */
    public static final Interpolator smootherOutIn = new Interpolator("Smoother.OUTIN", smoother.fn.flip());
    /**
     * A quintic Hermite spline by Ken Perlin; this uses the same function as {@link #smoother}.
     * <br>
     * This was modified slightly because the original constants were meant for doubles, and here we use floats. Without
     * this tiny change (the smallest possible change here, from 10.0f to 9.999998f), giving an input of 0.99999994f, or
     * one of thousands of other inputs, would unexpectedly produce an output greater than 1.0f .
     */
    public static final Interpolator fade = new Interpolator("Fade.INOUT", smoother.fn);
    /**
     * A quintic Hermite spline by Ken Perlin, but flipped; this uses the same function as {@link #smootherOutIn}.
     * <br>
     * This was modified; see {@link #fade}.
     */
    public static final Interpolator fadeOutIn = new Interpolator("Fade.OUTIN", smootherOutIn.fn);
    /**
     * Produces an InterpolationFunction that uses the given power variable.
     * When power is greater than 1, this starts slowly, speeds up in the middle and slows down at the end. The
     * rate of acceleration and deceleration changes based on the parameter. Non-integer parameters are supported,
     * unlike the Pow in libGDX. Negative powers are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction powFunction(final float power) {
        return a -> {
            if (a <= 0.5f) return (float) Math.pow(a + a, power) * 0.5f;
            return (float) Math.pow(2f - a - a, power) * -0.5f + 1f;
        };
    }
    /**
     * Produces an InterpolationFunction that uses the given power variable.
     * When power is greater than 1, this starts quickly, slows down in the middle and speeds up at the end. The
     * rate of acceleration and deceleration changes based on the parameter. Non-integer parameters are supported,
     * unlike the Pow in libGDX. Negative powers are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction powOutInFunction(final float power) {
        return a -> {
            if (a > 0.5f) return (float) Math.pow(a + a - 1f, power) * 0.5f + 0.5f;
            return (float) Math.pow(1f - a - a, power) * -0.5f + 0.5f;
        };
    }

    /**
     * Produces an InterpolationFunction that uses the given power variable.
     * When power is greater than 1, this starts slowly and speeds up toward the end. The
     * rate of acceleration changes based on the parameter. Non-integer parameters are supported,
     * unlike the PowIn in libGDX. Negative powers are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction powInFunction(final float power) {
        return a -> (float) Math.pow(a, power);
    }
    /**
     * Produces an InterpolationFunction that uses the given power variable.
     * When power is greater than 1, this starts quickly and slows down toward the end. The
     * rate of deceleration changes based on the parameter. Non-integer parameters are supported,
     * unlike the PowOut in libGDX. Negative powers are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction powOutFunction(final float power) {
        return a -> 1f - (float) Math.pow(1f - a, power);
    }

    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 2.
     */
    public static final Interpolator pow2 = new Interpolator("Pow2.INOUT", powFunction(2f));
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 3.
     */
    public static final Interpolator pow3 = new Interpolator("Pow3.INOUT", powFunction(3f));
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 4.
     */
    public static final Interpolator pow4 = new Interpolator("Pow4.INOUT", powFunction(4f));
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 5.
     */
    public static final Interpolator pow5 = new Interpolator("Pow5.INOUT", powFunction(5f));
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 0.75.
     */
    public static final Interpolator pow0_75 = new Interpolator("Pow0_75.INOUT", powFunction(0.75f));
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 0.5. Optimized with {@link Math#sqrt(double)}.
     */
    public static final Interpolator pow0_5 = new Interpolator("Pow0_5.INOUT", a -> {
        if (a <= 0.5f) return (float) Math.sqrt(a + a) * 0.5f;
        return (float) Math.sqrt(2f - a - a) * -0.5f + 1f;
    });
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 0.25.
     */
    public static final Interpolator pow0_25 = new Interpolator("Pow0_25.INOUT", powFunction(0.25f));

    /**
     * Accelerates using {@link #powInFunction(float)} and power of 2.
     */
    public static final Interpolator pow2In = new Interpolator("Pow2.IN", powInFunction(2f));
    /**
     * Slow, then fast. This uses the same function as {@link #pow2In}.
     */
    public static final Interpolator slowFast = new Interpolator("SlowFast.IN", pow2In.fn);
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 3.
     */
    public static final Interpolator pow3In = new Interpolator("Pow3.IN", powInFunction(3f));
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 4.
     */
    public static final Interpolator pow4In = new Interpolator("Pow4.IN", powInFunction(4f));
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 5.
     */
    public static final Interpolator pow5In = new Interpolator("Pow5.IN", powInFunction(5f));
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 0.75.
     */
    public static final Interpolator pow0_75In = new Interpolator("Pow0_75.IN", powInFunction(0.75f));
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 0.5. Optimized with {@link Math#sqrt(double)}.
     */
    public static final Interpolator pow0_5In = new Interpolator("Pow0_5.IN", a -> (float) Math.sqrt(a));
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 0.25.
     */
    public static final Interpolator pow0_25In = new Interpolator("Pow0_25.IN", powInFunction(0.25f));
    /**
     * An alias for {@link #pow0_5In}, this is the inverse for {@link #pow2In}. Optimized with {@link Math#sqrt(double)}.
     */
    public static final Interpolator sqrtIn = new Interpolator("Sqrt.IN", a -> (float) Math.sqrt(a));
    /**
     * This is the inverse for {@link #pow3In}. Calls {@link Math#cbrt(double)} and casts it to float.
     */
    public static final Interpolator cbrtIn = new Interpolator("Cbrt.IN", a -> (float) Math.cbrt(a));

    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 2.
     */
    public static final Interpolator pow2Out = new Interpolator("Pow2.OUT", powOutFunction(2f));
    /**
     * Fast, then slow. This uses the same function as {@link #pow2Out}.
     */
    public static final Interpolator fastSlow = new Interpolator("FastSlow.OUT", pow2Out.fn);
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 3.
     */
    public static final Interpolator pow3Out = new Interpolator("Pow3.OUT", powOutFunction(3f));
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 4.
     */
    public static final Interpolator pow4Out = new Interpolator("Pow4.OUT", powOutFunction(4f));
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 5.
     */
    public static final Interpolator pow5Out = new Interpolator("Pow5.OUT", powOutFunction(5f));
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 0.75.
     */
    public static final Interpolator pow0_75Out = new Interpolator("Pow0_75.OUT", powOutFunction(0.75f));
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 0.5. Optimized with {@link Math#sqrt(double)}.
     */
    public static final Interpolator pow0_5Out = new Interpolator("Pow0_5.OUT", a -> 1f - (float) Math.sqrt(1f - a));
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 0.25.
     */
    public static final Interpolator pow0_25Out = new Interpolator("Pow0_25.OUT", powOutFunction(0.25f));
    /**
     * An alias for {@link #pow0_5Out}, this is the inverse of {@link #pow2Out}. Optimized with {@link Math#sqrt(double)}.
     */
    public static final Interpolator sqrtOut = new Interpolator("Sqrt.OUT", a -> 1f - (float) Math.sqrt(1f - a));
    /**
     * This is the inverse for {@link #pow3Out}. Optimized with {@link Math#cbrt(double)}.
     */
    public static final Interpolator cbrtOut = new Interpolator("Cbrt.OUT", a -> 1f - (float) Math.cbrt(1f - a));

    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 2.
     */
    public static final Interpolator pow2OutIn = new Interpolator("Pow2.OUTIN", powOutInFunction(2f));
    /**
     * Fast, then slow, then fast. This uses the same function as {@link #pow2OutIn}.
     */
    public static final Interpolator fastSlowFast = new Interpolator("FastSlowFast.OUTIN", pow2OutIn.fn);
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 3.
     */
    public static final Interpolator pow3OutIn = new Interpolator("Pow3.OUTIN", powOutInFunction(3f));
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 4.
     */
    public static final Interpolator pow4OutIn = new Interpolator("Pow4.OUTIN", powOutInFunction(4f));
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 5.
     */
    public static final Interpolator pow5OutIn = new Interpolator("Pow5.OUTIN", powOutInFunction(5f));
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 0.75.
     */
    public static final Interpolator pow0_75OutIn = new Interpolator("Pow0_75.OUTIN", powOutInFunction(0.75f));
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 0.5.
     */
    public static final Interpolator pow0_5OutIn = new Interpolator("Pow0_5.OUTIN", powOutInFunction(0.5f));
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 0.25.
     */
    public static final Interpolator pow0_25OutIn = new Interpolator("Pow0_25.OUTIN", powOutInFunction(0.25f));

    /**
     * Produces an InterpolationFunction that uses the given value and power variables.
     * When power is greater than 1, this starts slowly, speeds up in the middle and slows down at the end. The
     * rate of acceleration and deceleration changes based on the parameter. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction expFunction(final float value, final float power) {
        final float min = (float) Math.pow(value, -power), scale = 1f / (1f - min);
        return a -> {
            if (a <= 0.5f) return ((float)Math.pow(value, power * (a * 2f - 1f)) - min) * scale * 0.5f;
            return (2f - ((float)Math.pow(value, -power * (a * 2f - 1f)) - min) * scale) * 0.5f;
        };
    }

    /**
     * Produces an InterpolationFunction that uses the given value and power variables.
     * When power is greater than 1, this starts slowly and speeds up toward the end. The
     * rate of acceleration changes based on the parameter. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction expInFunction(final float value, final float power) {
        final float min = (float) Math.pow(value, -power), scale = 1f / (1f - min);
        return a -> ((float)Math.pow(value, power * (a - 1f)) - min) * scale;
    }

    /**
     * Produces an InterpolationFunction that uses the given value and power variables.
     * When power is greater than 1, this starts quickly and slows down toward the end. The
     * rate of deceleration changes based on the parameter. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction expOutFunction(final float value, final float power) {
        final float min = (float) Math.pow(value, -power), scale = 1f / (1f - min);
        return a -> 1f - ((float)Math.pow(value, -power * a) - min) * scale;
    }

    /**
     * Accelerates and decelerates using {@link #expFunction(float, float)}, value of 2 and power of 5.
     */
    public static final Interpolator exp5 = new Interpolator("Exp5.INOUT", expFunction(2f, 5f));

    /**
     * Accelerates and decelerates using {@link #expFunction(float, float)}, value of 2 and power of 10.
     */
    public static final Interpolator exp10 = new Interpolator("Exp10.INOUT", expFunction(2f, 10f));

    /**
     * Accelerates using {@link #expInFunction(float, float)}, value of 2 and power of 5.
     */
    public static final Interpolator exp5In = new Interpolator("Exp5.IN", expInFunction(2f, 5f));

    /**
     * Accelerates using {@link #expInFunction(float, float)}, value of 2 and power of 10.
     */
    public static final Interpolator exp10In = new Interpolator("Exp10.IN", expInFunction(2f, 10f));

    /**
     * Decelerates using {@link #expOutFunction(float, float)}, value of 2 and power of 5.
     */
    public static final Interpolator exp5Out = new Interpolator("Exp5.OUT", expOutFunction(2f, 5f));

    /**
     * Decelerates using {@link #expOutFunction(float, float)}, value of 2 and power of 10.
     */
    public static final Interpolator exp10Out = new Interpolator("Exp10.OUT", expOutFunction(2f, 10f));


    /**
     * Accelerates and decelerates using {@link #expFunction(float, float)}, value of 2 and power of 5, but flipped.
     */
    public static final Interpolator exp5OutIn = new Interpolator("Exp5.OUTIN", exp5.fn.flip());

    /**
     * Accelerates and decelerates using {@link #expFunction(float, float)}, value of 2 and power of 10, but flipped.
     */
    public static final Interpolator exp10OutIn = new Interpolator("Exp10.OUTIN", exp10.fn.flip());


    /**
     * Produces an InterpolationFunction that uses the possible shapes of the Kumaraswamy distribution, but without
     * involving a random component. This can produce a wide range of shapes for the interpolation, and may require
     * generating several during development to get a particular shape you want. The a and b parameters must be greater
     * than 0.0, but have no other requirements. Most curves that this method produces are somewhat asymmetrical.
     * @see <a href="https://en.wikipedia.org/wiki/Kumaraswamy_distribution">Wikipedia's page on this distribution.</a>
     * @param a the Kumaraswamy distribution's a parameter
     * @param b the Kumaraswamy distribution's b parameter
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction kumaraswamyFunction(final double a, final double b) {
        final double A = 1.0 / a;
        final double B = 1.0 / b;
        return x -> (float) Math.pow(1.0 - Math.pow(1.0 - x, B), A);
    }

    /**
     * Produces more results toward the edges. Uses {@code kumaraswamyFunction(0.75f, 0.75f)}.
     */
    public static final Interpolator kumaraswamyExtremeA = new Interpolator("KumaraswamyExtremeA.INOUT", kumaraswamyFunction(0.75f, 0.75f));
    /**
     * Produces more results toward the edges. Uses {@code kumaraswamyFunction(0.5f, 0.5f)}.
     */
    public static final Interpolator kumaraswamyExtremeB = new Interpolator("KumaraswamyExtremeB.INOUT", kumaraswamyFunction(0.5f, 0.5f));
    /**
     * Produces more results toward the edges. Uses {@code kumaraswamyFunction(0.25f, 0.25f)}.
     */
    public static final Interpolator kumaraswamyExtremeC = new Interpolator("KumaraswamyExtremeC.INOUT", kumaraswamyFunction(0.25f, 0.25f));
    /**
     * Produces more results in the center. Uses {@code kumaraswamyFunction(2f, 2f)}.
     */
    public static final Interpolator kumaraswamyCentralA = new Interpolator("KumaraswamyCentralA.INOUT", kumaraswamyFunction(2f, 2f));
    /**
     * Produces more results in the center. Uses {@code kumaraswamyFunction(4f, 4f)}.
     */
    public static final Interpolator kumaraswamyCentralB = new Interpolator("KumaraswamyCentralB.INOUT", kumaraswamyFunction(4f, 4f));
    /**
     * Produces more results in the center. Uses {@code kumaraswamyFunction(6f, 6f)}.
     */
    public static final Interpolator kumaraswamyCentralC = new Interpolator("KumaraswamyCentralC.INOUT", kumaraswamyFunction(6f, 6f));
    /**
     * Produces more results near 0. Uses {@code kumaraswamyFunction(1f, 5f)}.
     */
    public static final Interpolator kumaraswamyMostlyLow = new Interpolator("KumaraswamyMostlyLow.INOUT", kumaraswamyFunction(1f, 5f));
    /**
     * Produces more results near 1. Uses {@code kumaraswamyFunction(5f, 1f)}.
     */
    public static final Interpolator kumaraswamyMostlyHigh = new Interpolator("KumaraswamyMostlyHigh.INOUT", kumaraswamyFunction(5f, 1f));

// The "sine" implementations use the SIN and COS tables directly to avoid a few operations inside sinTurns().
// They use SIN_TO_COS as an offset because if the number of bits in the table changes, SIN_TO_COS will adapt.
    /**
     * Moves like a sine wave does; starts slowly, rises quickly, then ends slowly.
     */
    public static final Interpolator sine = new Interpolator("Sine.INOUT", a -> (a = MathUtils.sinDeg(a * 90f)) * a);

    /**
     * Moves like a sine wave does; starts slowly and rises quickly.
     */
    public static final Interpolator sineIn = new Interpolator("Sine.IN", a -> (1f - MathUtils.cosDeg(a * 90f)));
    /**
     * Moves like a sine wave does; starts quickly and slows down.
     */
    public static final Interpolator sineOut = new Interpolator("Sine.OUT", a -> MathUtils.sinDeg(a * 90f));
    /**
     * Moves like a sine wave does, but flipped; starts quickly, rises slowly, then ends quickly.
     */
    public static final Interpolator sineOutIn = new Interpolator("Sine.OUTIN", sine.fn.flip());

    /**
     * When graphed, forms two circular arcs; it starts slowly, accelerating rapidly towards the middle, then slows down
     * towards the end.
     */
    public static final Interpolator circle = new Interpolator("Circle.INOUT", a -> (a <= 0.5f
            ? (1f - (float)Math.sqrt(1f - a * a * 4f)) * 0.5f
            : ((float)Math.sqrt(1f - 4f * (a * (a - 2f) + 1f)) + 1f) * 0.5f));
    /**
     * When graphed, forms one circular arc, starting slowly and accelerating at the end.
     */
    public static final Interpolator circleIn = new Interpolator("Circle.IN", a -> (1f - (float)Math.sqrt(1f - a * a)));
    /**
     * When graphed, forms one circular arc, starting rapidly and decelerating at the end.
     */
    public static final Interpolator circleOut = new Interpolator("Circle.OUT", a -> ((float)Math.sqrt(a * (2f - a))));
    /**
     * When graphed, forms two circular arcs; it starts quickly, decelerating towards the middle, then speeds up
     * towards the end.
     */
    public static final Interpolator circleOutIn = new Interpolator("Circle.OUTIN", circle.fn.flip());

    /**
     * Produces an InterpolationFunction that uses the given {@code width, height, width, height, ...} float array.
     * Unlike {@link #bounceOutFunction(float...)}, this bounces at both the start and end of its interpolation.
     * Fair warning; using this is atypically complicated, and you should generally stick to using a predefined
     * Interpolator, such as {@link #bounce4}. You can also hand-edit the values in pairs; if you do, every even
     * index is a width, and every odd index is a height. Later widths are no greater than earlier ones; this is also
     * true for heights. No width is typically greater than 1.5f, and they are always positive and less than 2f.
     *
     * @param pairs width, height, width, height... in pairs; typically none are larger than 1.5f, and all are positive
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction bounceFunction(final float... pairs) {
        final InterpolationFunction bOut = bounceOutFunction(pairs), iOut = o -> {
            float test = o + pairs[0] * 0.5f;
            if (test < pairs[0]) return test / (pairs[0] * 0.5f) - 1f;
            return bOut.compute(o);
        };

        return a -> {
            if(a <= 0.5f) return (1f - iOut.compute(1f - a - a)) * 0.5f;
            return iOut.compute(a + a - 1) * 0.5f + 0.5f;
        };
    }

    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 2 bounces.
     */
    public static final Interpolator bounce2 = new Interpolator("Bounce2.INOUT", bounceFunction(1.2f, 1f, 0.4f, 0.33f));
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 3 bounces.
     */
    public static final Interpolator bounce3 = new Interpolator("Bounce3.INOUT", bounceFunction(0.8f, 1f, 0.4f, 0.33f, 0.2f, 0.1f));
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 4 bounces.
     */
    public static final Interpolator bounce4 = new Interpolator("Bounce4.INOUT", bounceFunction(0.65f, 1f, 0.325f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 4 bounces. While both this and
     * {@link #bounce4} use 4 bounces, this matches the behavior of bounce in libGDX.
     */
    public static final Interpolator bounce = new Interpolator("Bounce.INOUT", bounceFunction(0.68f, 1f, 0.34f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 5 bounces.
     */
    public static final Interpolator bounce5 = new Interpolator("Bounce5.INOUT", bounceFunction(0.61f, 1f, 0.31f, 0.45f, 0.21f, 0.3f, 0.11f, 0.15f, 0.06f, 0.06f));

    /**
     * Produces an InterpolationFunction that uses the given {@code width, height, width, height, ...} float array.
     * This bounces at the end of its interpolation.
     * Fair warning; using this is atypically complicated, and you should generally stick to using a predefined
     * Interpolator, such as {@link #bounce4Out}. You can also hand-edit the values in pairs; if you do, every even
     * index is a width, and every odd index is a height. Later widths are no greater than earlier ones; this is also
     * true for heights. No width is typically greater than 1.5f, and they are always positive and less than 2f.
     *
     * @param pairs width, height, width, height... in pairs; typically none are larger than 1.5f, and all are positive
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction bounceOutFunction(final float... pairs) {
        return a -> {
//            if(a >= 1f) return 1f;
            float b = a + pairs[0] * 0.5f;
            float width = 0f, height = 0f;
            for (int i = 0, n = (pairs.length & -2) - 1; i < n; i += 2) {
                width = pairs[i];
                if (b <= width) {
                    height = pairs[i + 1];
                    break;
                }
                b -= width;
            }
            float z = 4f / (width * width) * height * b;
            float f = 1f - z * (width - b);
            // pretty sure this is equivalent to the 2 lines above. Not certain.
//            a /= width;
//            float z = 4 / width * height * a;
//            return 1 - (z - z * a) * width;
            return a >= 0.98f ? MathUtils.lerp(f, 1f, 50f * (a - 0.98f)) : f;

        };
    }

    /**
     * Decelerates using {@link #bounceOutFunction(float...)}, with 2 bounces.
     */
    public static final Interpolator bounce2Out = new Interpolator("Bounce2.OUT", bounceOutFunction(1.2f, 1f, 0.4f, 0.33f));
    /**
     * Decelerates using {@link #bounceOutFunction(float...)}, with 3 bounces.
     */
    public static final Interpolator bounce3Out = new Interpolator("Bounce3.OUT", bounceOutFunction(0.8f, 1f, 0.4f, 0.33f, 0.2f, 0.1f));
    /**
     * Decelerates using {@link #bounceOutFunction(float...)}, with 4 bounces.
     */
    public static final Interpolator bounce4Out = new Interpolator("Bounce4.OUT", bounceOutFunction(0.65f, 1f, 0.325f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Decelerates using {@link #bounceOutFunction(float...)}, with 4 bounces. While both this and
     * {@link #bounce4Out} use 4 bounces, this matches the behavior of bounceOut in libGDX.
     */
    public static final Interpolator bounceOut = new Interpolator("Bounce.OUT", bounceOutFunction(0.68f, 1f, 0.34f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Decelerates using {@link #bounceOutFunction(float...)}, with 5 bounces.
     */
    public static final Interpolator bounce5Out = new Interpolator("Bounce5.OUT", bounceOutFunction(0.61f, 1f, 0.31f, 0.45f, 0.21f, 0.3f, 0.11f, 0.15f, 0.06f, 0.06f));

    /**
     * Produces an InterpolationFunction that uses the given {@code width, height, width, height, ...} float array.
     * This bounces at the start of its interpolation.
     * Fair warning; using this is atypically complicated, and you should generally stick to using a predefined
     * Interpolator, such as {@link #bounce4In}. You can also hand-edit the values in pairs; if you do, every even
     * index is a width, and every odd index is a height. Later widths are no greater than earlier ones; this is also
     * true for heights. No width is typically greater than 1.5f, and they are always positive and less than 2f.
     *
     * @param pairs width, height, width, height... in pairs; typically none are larger than 1.5f, and all are positive
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction bounceInFunction(final float... pairs) {
        final InterpolationFunction bOut = bounceOutFunction(pairs);
        return a -> 1f - bOut.compute(1f - a);
    }

    /**
     * Decelerates using {@link #bounceInFunction(float...)}, with 2 bounces.
     */
    public static final Interpolator bounce2In = new Interpolator("Bounce2.IN", bounceInFunction(1.2f, 1f, 0.4f, 0.33f));
    /**
     * Decelerates using {@link #bounceInFunction(float...)}, with 3 bounces.
     */
    public static final Interpolator bounce3In = new Interpolator("Bounce3.IN", bounceInFunction(0.8f, 1f, 0.4f, 0.33f, 0.2f, 0.1f));
    /**
     * Decelerates using {@link #bounceInFunction(float...)}, with 4 bounces.
     */
    public static final Interpolator bounce4In = new Interpolator("Bounce4.IN", bounceInFunction(0.65f, 1f, 0.325f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Decelerates using {@link #bounceInFunction(float...)}, with 4 bounces. While both this and
     * {@link #bounce4In} use 4 bounces, this matches the behavior of bounceIn in libGDX.
     */
    public static final Interpolator bounceIn = new Interpolator("Bounce.IN", bounceInFunction(0.68f, 1f, 0.34f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Decelerates using {@link #bounceInFunction(float...)}, with 5 bounces.
     */
    public static final Interpolator bounce5In = new Interpolator("Bounce5.IN", bounceInFunction(0.61f, 1f, 0.31f, 0.45f, 0.21f, 0.3f, 0.11f, 0.15f, 0.06f, 0.06f));

    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 2 bounces, but flipped.
     */
    public static final Interpolator bounce2OutIn = new Interpolator("Bounce2.OUTIN", bounce2.fn.flip());
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 3 bounces, but flipped.
     */
    public static final Interpolator bounce3OutIn = new Interpolator("Bounce3.OUTIN", bounce3.fn.flip());
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 4 bounces, but flipped.
     */
    public static final Interpolator bounce4OutIn = new Interpolator("Bounce4.OUTIN", bounce4.fn.flip());
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 4 bounces, but flipped.
     * While both this and {@link #bounce4OutIn} use 4 bounces, this matches the behavior of bounce in libGDX (flipped).
     */
    public static final Interpolator bounceOutIn = new Interpolator("Bounce.OUTIN", bounce.fn.flip());
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 5 bounces, but flipped.
     */
    public static final Interpolator bounce5OutIn = new Interpolator("Bounce5.OUTIN", bounce5.fn.flip());

    /**
     * Produces an InterpolationFunction that uses the given scale variable.
     * This drops below 0.0 at the start of the range, accelerates very rapidly, exceeds 1.0 at the middle of the input
     * range, and ends returning 1.0. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction swingFunction(final float scale) {
        final float sc = scale + scale;
        return a -> {
            if (a <= 0.5f) return ((sc + 1f) * (a += a) - sc) * a * a * 0.5f;
            return ((sc + 1f) * (a += a - 2f) + sc) * a * a * 0.5f + 1f;
        };
    }
    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 2.
     */
    public static final Interpolator swing2 = new Interpolator("Swing2.INOUT", swingFunction(2f));
    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 1.5.
     */
    public static final Interpolator swing = new Interpolator("Swing.INOUT", swingFunction(1.5f));
    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 3.
     */
    public static final Interpolator swing3 = new Interpolator("Swing3.INOUT", swingFunction(3f));
    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 0.75.
     */
    public static final Interpolator swing0_75 = new Interpolator("Swing0_75.INOUT", swingFunction(0.75f));
    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 0.5.
     */
    public static final Interpolator swing0_5 = new Interpolator("Swing0_5.INOUT", swingFunction(0.5f));

    /**
     * Produces an InterpolationFunction that uses the given scale variable.
     * This accelerates very rapidly, exceeds 1.0 at the middle of the input range, and ends returning 1.0. Negative
     * parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction swingOutFunction(final float scale) {
        return a -> ((scale + 1f) * --a + scale) * a * a + 1f;
    }
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 2.
     */
    public static final Interpolator swing2Out = new Interpolator("Swing2.OUT", swingOutFunction(2f));
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 2. This uses the same function as
     * {@link #swing2Out}.
     */
    public static final Interpolator swingOut = new Interpolator("Swing.OUT", swing2Out.fn);
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 3.
     */
    public static final Interpolator swing3Out = new Interpolator("Swing3.OUT", swingOutFunction(3f));
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 0.75.
     */
    public static final Interpolator swing0_75Out = new Interpolator("Swing0_75.OUT", swingOutFunction(0.75f));
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 0.5.
     */
    public static final Interpolator swing0_5Out = new Interpolator("Swing0_5.OUT", swingOutFunction(0.5f));

    /**
     * Produces an InterpolationFunction that uses the given scale variable.
     * This drops below 0.0 before the middle of the input range, later speeds up rapidly, and ends returning 1.0.
     * Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction swingInFunction(final float scale) {
        return a -> a * a * ((scale + 1f) * a - scale);
    }

    /**
     * Goes extra-low, using {@link #swingInFunction(float)} and scale of 2.
     */
    public static final Interpolator swing2In = new Interpolator("Swing2.IN", swingInFunction(2f));
    /**
     * Goes extra-low, using {@link #swingInFunction(float)} and scale of 2. This uses the same function as
     * {@link #swing2In}.
     */
    public static final Interpolator swingIn = new Interpolator("Swing.IN", swing2In.fn);
    /**
     * Goes extra-low, using {@link #swingInFunction(float)} and scale of 3.
     */
    public static final Interpolator swing3In = new Interpolator("Swing3.IN", swingInFunction(3f));
    /**
     * Goes extra-low, using {@link #swingInFunction(float)} and scale of 0.75.
     */
    public static final Interpolator swing0_75In = new Interpolator("Swing0_75.IN", swingInFunction(0.75f));
    /**
     * Goes extra-low, using {@link #swingInFunction(float)} and scale of 0.5.
     */
    public static final Interpolator swing0_5In = new Interpolator("Swing0_5.IN", swingInFunction(0.5f));

    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 2, but flipped.
     */
    public static final Interpolator swing2OutIn = new Interpolator("Swing2.OUTIN", swing2.fn.flip());
    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 1.5, but flipped.
     */
    public static final Interpolator swingOutIn = new Interpolator("Swing.OUTIN", swing.fn.flip());
    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 3, but flipped.
     */
    public static final Interpolator swing3OutIn = new Interpolator("Swing3.OUTIN", swing3.fn.flip());
    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 0.75, but flipped.
     */
    public static final Interpolator swing0_75OutIn = new Interpolator("Swing0_75.OUTIN", swing0_75.fn.flip());
    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 0.5, but flipped.
     */
    public static final Interpolator swing0_5OutIn = new Interpolator("Swing0_5.OUTIN", swing0_5.fn.flip());

    /**
     * Produces an InterpolationFunction that uses the given value, power, bounces, and scale variables.
     * This drops below 0.0 near the middle of the range, accelerates near-instantly, exceeds 1.0 just after that,
     * and ends returning 1.0. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction elasticFunction(final float value, final float power, final int bounces,
                                                        final float scale) {
        final float bounce = bounces * (0.5f - (bounces & 1)) * MathUtils.PI2;
        return a -> (a <= 0.5f)
                ? (float)Math.pow(value, power * ((a += a) - 1f)) * MathUtils.sin(a * bounce) * scale * 0.5f
                : 1f - (float)Math.pow(value, power * ((a = 2f - a - a) - 1f)) * MathUtils.sin(a * bounce) * scale * 0.5f;
    }
    /**
     * Goes extra low, then extra-high, using {@link #elasticFunction(float, float, int, float)}. Value is 2, power is
     * 10, bounces are 7, and scale is 1.
     */
    public static final Interpolator elastic = new Interpolator("Elastic.INOUT", elasticFunction(2f, 10f, 7, 1f));

    /**
     * Produces an InterpolationFunction that uses the given value, power, bounces, and scale variables.
     * This exceeds 1.0 just after the start of the range,
     * and ends returning 1.0. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction elasticOutFunction(final float value, final float power, final int bounces,
                                                                          final float scale) {
        final float bounce = bounces * (0.5f - (bounces & 1));
        return a -> {
            float f = (1f - (float) Math.pow(value, power * -a) * MathUtils.sin((bounce - a * bounce) * MathUtils.PI2) * scale);
            return a <= 0.02f ? MathUtils.lerp(0f, f, a * 50f) : f;
        };
    }

    /**
     * Goes extra-high near the start, using {@link #elasticOutFunction(float, float, int, float)}. Value is 2, power is
     * 10, bounces are 7, and scale is 1.
     */
    public static final Interpolator elasticOut = new Interpolator("Elastic.OUT", elasticOutFunction(2f, 10f, 7, 1f));

    /**
     * Produces an InterpolationFunction that uses the given value, power, bounces, and scale variables.
     * This drops below 0.0 just before the end of the range,
     * but jumps up so that it ends returning 1.0. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction elasticInFunction(final float value, final float power, final int bounces,
                                                                         final float scale) {
        final float bounce = bounces * (0.5f - (bounces & 1)) * MathUtils.PI2;
        return a -> {
            float f = (float) Math.pow(value, power * (a - 1)) * MathUtils.sin(a * bounce) * scale;
            return a >= 0.98f ? MathUtils.lerp(f, 1f, 50f * (a - 0.98f)) : f;
        };
    }
    /**
     * Goes extra-low near the end, using {@link #elasticInFunction(float, float, int, float)}. Value is 2, power is
     * 10, bounces are 6, and scale is 1.
     */
    public static final Interpolator elasticIn = new Interpolator("Elastic.IN", elasticInFunction(2f, 10f, 6, 1f));

    /**
     * Produces an InterpolationFunction that uses the given value, power, bounces, and scale variables.
     * This accelerates near-instantly, wiggles in to settle near the middle of the range, accelerates again near the
     * end, and finishes returning 1.0. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return an InterpolationFunction that will use the given configuration
     */
    public static InterpolationFunction elasticOutInFunction(final float value, final float power, final int bounces,
                                                                            final float scale) {
        final float bounce = (bounces * (0.5f - (bounces & 1)) - 0.25f) * MathUtils.PI2;
        return a -> (a > 0.5f)
                ? (float)Math.pow(value, power * ((a += a - 1) - 1f)) * MathUtils.sin(a * bounce) * scale * 0.5f + 0.5f
                : 0.5f - (float)Math.pow(value, power * ((a = 1f - a - a) - 1f)) * MathUtils.sin(a * bounce) * scale * 0.5f;
    }
    /**
     * Stays within the mid-range, using {@link #elasticOutInFunction(float, float, int, float)}. Value is 2, power
     * is 10, bounces are 7, and scale is 1.
     */
    public static final Interpolator elasticOutIn = new Interpolator("Elastic.OUTIN", elasticOutInFunction(2f, 10f, 7, 1f));


    // Aliases
    /**
     * Alias for {@link #pow2}.
     */
    public static final Interpolator quadInOut = new Interpolator("Quad.INOUT", pow2.fn);
    /**
     * Alias for {@link #pow2In}.
     */
    public static final Interpolator quadIn = new Interpolator("Quad.IN", pow2In.fn);
    /**
     * Alias for {@link #pow2Out}.
     */
    public static final Interpolator quadOut = new Interpolator("Quad.OUT", pow2Out.fn);
    /**
     * Alias for {@link #pow2OutIn}.
     */
    public static final Interpolator quadOutIn = new Interpolator("Quad.OUTIN", pow2OutIn.fn);
    /**
     * Alias for {@link #pow3}.
     */
    public static final Interpolator cubicInOut = new Interpolator("Cubic.INOUT", pow3.fn);
    /**
     * Alias for {@link #pow3In}.
     */
    public static final Interpolator cubicIn = new Interpolator("Cubic.IN", pow3In.fn);
    /**
     * Alias for {@link #pow3Out}.
     */
    public static final Interpolator cubicOut = new Interpolator("Cubic.OUT", pow3Out.fn);
    /**
     * Alias for {@link #pow3OutIn}.
     */
    public static final Interpolator cubicOutIn = new Interpolator("Cubic.OUTIN", pow3OutIn.fn);
    /**
     * Alias for {@link #pow4}.
     */
    public static final Interpolator quartInOut = new Interpolator("Quart.INOUT", pow4.fn);
    /**
     * Alias for {@link #pow4In}.
     */
    public static final Interpolator quartIn = new Interpolator("Quart.IN", pow4In.fn);
    /**
     * Alias for {@link #pow4Out}.
     */
    public static final Interpolator quartOut = new Interpolator("Quart.OUT", pow4Out.fn);
    /**
     * Alias for {@link #pow4OutIn}.
     */
    public static final Interpolator quartOutIn = new Interpolator("Quart.OUTIN", pow4OutIn.fn);
    /**
     * Alias for {@link #pow5}.
     */
    public static final Interpolator quintInOut = new Interpolator("Quint.INOUT", pow5.fn);
    /**
     * Alias for {@link #pow5In}.
     */
    public static final Interpolator quintIn = new Interpolator("Quint.IN", pow5In.fn);
    /**
     * Alias for {@link #pow5Out}.
     */
    public static final Interpolator quintOut = new Interpolator("Quint.OUT", pow5Out.fn);
    /**
     * Alias for {@link #pow5OutIn}.
     */
    public static final Interpolator quintOutIn = new Interpolator("Quint.OUTIN", pow5OutIn.fn);

    /**
     * Alias for {@link #exp10}.
     */
    public static final Interpolator expoInOut = new Interpolator("Expo.INOUT", exp10.fn);
    /**
     * Alias for {@link #exp10In}.
     */
    public static final Interpolator expoIn = new Interpolator("Expo.IN", exp10In.fn);
    /**
     * Alias for {@link #exp10Out}.
     */
    public static final Interpolator expoOut = new Interpolator("Expo.OUT", exp10Out.fn);
    /**
     * Alias for {@link #exp10OutIn}.
     */
    public static final Interpolator expoOutIn = new Interpolator("Expo.OUTIN", exp10OutIn.fn);

    /**
     * Alias for {@link #circle}.
     */
    public static final Interpolator circInOut = new Interpolator("Circ.INOUT", circle.fn);
    /**
     * Alias for {@link #circleIn}.
     */
    public static final Interpolator circIn = new Interpolator("Circ.IN", circleIn.fn);
    /**
     * Alias for {@link #circleOut}.
     */
    public static final Interpolator circOut = new Interpolator("Circ.OUT", circleOut.fn);
    /**
     * Alias for {@link #circleOutIn}.
     */
    public static final Interpolator circOutIn = new Interpolator("Circ.OUTIN", circleOutIn.fn);
    
    /**
     * Alias for {@link #swing}. Probably not an exact duplicate of the similarly-named Penner easing function.
     */
    public static final Interpolator backInOut = new Interpolator("Back.INOUT", swing.fn);
    /**
     * Alias for {@link #swingIn}. Probably not an exact duplicate of the similarly-named Penner easing function.
     */
    public static final Interpolator backIn = new Interpolator("Back.IN", swingIn.fn);
    /**
     * Alias for {@link #swingOut}. Probably not an exact duplicate of the similarly-named Penner easing function.
     */
    public static final Interpolator backOut = new Interpolator("Back.OUT", swingOut.fn);
    /**
     * Alias for {@link #swingOutIn}. Probably not an exact duplicate of the similarly-named Penner easing function.
     */
    public static final Interpolator backOutIn = new Interpolator("Back.OUTIN", swingOutIn.fn);

}
