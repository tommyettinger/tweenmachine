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

import static com.github.tommyettinger.tweenmachine.TweenUtils.barronSpline;

/**
 * Provides predefined {@link TweenEquation} constants and ways to generate {@link TweenFunction} instances, as
 * well as acting as the registry for known TweenEquation values so that they can be looked up by name. This has every
 * <a href="http://robertpenner.com/easing/">Penner easing function</a>. Unlike
 * the original Universal Tween Engine, every easing function that has an IN and an OUT version now also has an INOUT
 * and OUTIN version. The original didn't supply any OUTIN versions.
 * <br>
 * <a href="https://tommyettinger.github.io/tweenmachine/equations.html">You can view the graphs for every TweenEquation here</a>.
 */
public final class TweenEquations {
    /**
     * No need to instantiate.
     */
    private TweenEquations() {
    }

    /**
     * Maps String tag keys to TweenEquation values, storing them typically in insertion order.
     * This is not intended for external use, but is public in case it is ever needed directly.
     * You can also obtain the tags on their own using {@link #getTags()}, or an Array with every TweenEquation using
     * {@link #getTweenEquations()}.
     */
    public static final OrderedMap<String, TweenEquation> REGISTRY = new OrderedMap<>(128);

    /**
     * Looks up the given {@code tag} in a registry of TweenEquations, and if there exists one with that name, returns
     * it. Otherwise, this returns null.
     *
     * @param tag a tag used to register a TweenEquation here
     * @return the TweenEquation registered with the given tag, or null if none exists for that tag
     */
    public static TweenEquation get(String tag) {
        return REGISTRY.get(tag);
    }

    /**
     * Gets a direct reference to the tags used as keys for the TweenEquation registry. This Array can be sorted if you
     * want, or otherwise rearranged. If new tags are registered, this will reflect those changes.
     *
     * @return an Array containing every String tag registered for a TweenEquation; direct reference
     */
    public static Array<String> getTags() {
        return REGISTRY.orderedKeys();
    }

    /**
     * Allocates a new Array of TweenEquation items, fills it with every registered TweenEquation, and returns that Array.
     * This is not a direct reference; modifying the Array won't change the registered contents.
     *
     * @return a new Array containing every TweenEquation registered
     */
    public static Array<TweenEquation> getTweenEquations() {
        return REGISTRY.values().toArray();
    }

    /**
     * Linear interpolation; just returns its argument.
     */
    public static final TweenFunction linearFunction = (a -> a);

    /**
     * Plain linear interpolation, or "lerp"; this just returns the alpha it is given.
     */
    public static final TweenEquation linear = new TweenEquation("Linear.INOUT", linearFunction);
    /**
     * "Smoothstep" or a cubic Hermite spline.
     * <br>
     * This has been modified slightly for numerical correctness. The form of this spline usually given,
     * {code a * a * (3 - 2 * a)}, can exceed 1 for many inputs that are just less than 1, which makes that form much
     * harder to use in a table lookup because an output larger than 1 could mean an out-of-bounds index. Instead, we
     * use the form {@code a * a * (1 - a - a + 2)}, which always stays in the range 0.0f to 1.0f, inclusive.
     */
    public static final TweenEquation smooth = new TweenEquation("Smooth.INOUT", a -> a * a * (1 - a - a + 2));
    /**
     * "Smoothstep" or a cubic Hermite spline, but flipped.
     * <br>
     * This has been modified slightly for numerical correctness. The form of this spline usually given,
     * {code a * a * (3 - 2 * a)}, can exceed 1 for many inputs that are just less than 1, which makes that form much
     * harder to use in a table lookup because an output larger than 1 could mean an out-of-bounds index. Instead, we
     * use the form {@code a * a * (1 - a - a + 2)}, which always stays in the range 0.0f to 1.0f, inclusive.
     */
    public static final TweenEquation smoothOutIn = new TweenEquation("Smooth.OUTIN", smooth.fn.flip());
    /**
     * "Smoothstep" or a cubic Hermite spline, applied twice.
     * <br>
     * This has been modified slightly for numerical correctness. The form of the cubic Hermite spline usually given,
     * {code a * a * (3 - 2 * a)}, can exceed 1 for many inputs that are just less than 1, which makes that form much
     * harder to use in a table lookup because an output larger than 1 could mean an out-of-bounds index. Instead, we
     * use the form {@code a * a * (1 - a - a + 2)}, which always stays in the range 0.0f to 1.0f, inclusive.
     */
    public static final TweenEquation smooth2 = new TweenEquation("Smooth2.INOUT", a -> (a *= a * (1 - a - a + 2)) * a * (1 - a - a + 2));
    /**
     * "Smoothstep" or a cubic Hermite spline, applied twice, but flipped.
     * <br>
     * This has been modified slightly for numerical correctness. The form of the cubic Hermite spline usually given,
     * {code a * a * (3 - 2 * a)}, can exceed 1 for many inputs that are just less than 1, which makes that form much
     * harder to use in a table lookup because an output larger than 1 could mean an out-of-bounds index. Instead, we
     * use the form {@code a * a * (1 - a - a + 2)}, which always stays in the range 0.0f to 1.0f, inclusive.
     */
    public static final TweenEquation smooth2OutIn = new TweenEquation("Smooth2.OUTIN", smooth2.fn.flip());
    /**
     * A quintic Hermite spline by Ken Perlin.
     * <br>
     * This was modified slightly because the original constants were meant for doubles, and here we use floats. Without
     * this tiny change (the smallest possible change here, from 10.0f to 9.999998f), giving an input of 0.99999994f, or
     * one of thousands of other inputs, would unexpectedly produce an output greater than 1.0f .
     */
    public static final TweenEquation smoother = new TweenEquation("Smoother.INOUT", a -> a * a * a * (a * (a * 6f - 15f) + 9.999998f));
    /**
     * A quintic Hermite spline by Ken Perlin, but flipped.
     * <br>
     * This was modified; see {@link #smoother}.
     */
    public static final TweenEquation smootherOutIn = new TweenEquation("Smoother.OUTIN", smoother.fn.flip());
    /**
     * A quintic Hermite spline by Ken Perlin; this uses the same function as {@link #smoother}.
     * <br>
     * This was modified slightly because the original constants were meant for doubles, and here we use floats. Without
     * this tiny change (the smallest possible change here, from 10.0f to 9.999998f), giving an input of 0.99999994f, or
     * one of thousands of other inputs, would unexpectedly produce an output greater than 1.0f .
     */
    public static final TweenEquation fade = new TweenEquation("Fade.INOUT", smoother.fn);
    /**
     * A quintic Hermite spline by Ken Perlin, but flipped; this uses the same function as {@link #smootherOutIn}.
     * <br>
     * This was modified; see {@link #fade}.
     */
    public static final TweenEquation fadeOutIn = new TweenEquation("Fade.OUTIN", smootherOutIn.fn);
    /**
     * Produces a TweenFunction that uses the given power variable.
     * When power is greater than 1, this starts slowly, speeds up in the middle and slows down at the end. The
     * rate of acceleration and deceleration changes based on the parameter. Noninteger parameters are supported,
     * unlike the Pow in libGDX. Negative powers are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction powFunction(final float power) {
        return a -> {
            if (a <= 0.5f) return (float) Math.pow(a + a, power) * 0.5f;
            return (float) Math.pow(2f - a - a, power) * -0.5f + 1f;
        };
    }
    /**
     * Produces a TweenFunction that uses the given power variable.
     * When power is greater than 1, this starts quickly, slows down in the middle and speeds up at the end. The
     * rate of acceleration and deceleration changes based on the parameter. Noninteger parameters are supported,
     * unlike the Pow in libGDX. Negative powers are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction powOutInFunction(final float power) {
        return a -> {
            if (a > 0.5f) return (float) Math.pow(a + a - 1f, power) * 0.5f + 0.5f;
            return (float) Math.pow(1f - a - a, power) * -0.5f + 0.5f;
        };
    }

    /**
     * Produces a TweenFunction that uses the given power variable.
     * When power is greater than 1, this starts slowly and speeds up toward the end. The
     * rate of acceleration changes based on the parameter. Noninteger parameters are supported,
     * unlike the PowIn in libGDX. Negative powers are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction powInFunction(final float power) {
        return a -> (float) Math.pow(a, power);
    }
    /**
     * Produces a TweenFunction that uses the given power variable.
     * When power is greater than 1, this starts quickly and slows down toward the end. The
     * rate of deceleration changes based on the parameter. Noninteger parameters are supported,
     * unlike the PowOut in libGDX. Negative powers are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction powOutFunction(final float power) {
        return a -> 1f - (float) Math.pow(1f - a, power);
    }

    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 2.
     */
    public static final TweenEquation pow2 = new TweenEquation("Pow2.INOUT", powFunction(2f));
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 3.
     */
    public static final TweenEquation pow3 = new TweenEquation("Pow3.INOUT", powFunction(3f));
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 4.
     */
    public static final TweenEquation pow4 = new TweenEquation("Pow4.INOUT", powFunction(4f));
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 5.
     */
    public static final TweenEquation pow5 = new TweenEquation("Pow5.INOUT", powFunction(5f));
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 0.75.
     */
    public static final TweenEquation pow0_75 = new TweenEquation("Pow0_75.INOUT", powFunction(0.75f));
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 0.5. Optimized with {@link Math#sqrt(double)}.
     */
    public static final TweenEquation pow0_5 = new TweenEquation("Pow0_5.INOUT", a -> {
        if (a <= 0.5f) return (float) Math.sqrt(a + a) * 0.5f;
        return (float) Math.sqrt(2f - a - a) * -0.5f + 1f;
    });
    /**
     * Accelerates and decelerates using {@link #powFunction(float)} and power of 0.25.
     */
    public static final TweenEquation pow0_25 = new TweenEquation("Pow0_25.INOUT", powFunction(0.25f));

    /**
     * Accelerates using {@link #powInFunction(float)} and power of 2.
     */
    public static final TweenEquation pow2In = new TweenEquation("Pow2.IN", powInFunction(2f));
    /**
     * Slow, then fast. This uses the same function as {@link #pow2In}.
     */
    public static final TweenEquation slowFast = new TweenEquation("SlowFast.IN", pow2In.fn);
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 3.
     */
    public static final TweenEquation pow3In = new TweenEquation("Pow3.IN", powInFunction(3f));
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 4.
     */
    public static final TweenEquation pow4In = new TweenEquation("Pow4.IN", powInFunction(4f));
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 5.
     */
    public static final TweenEquation pow5In = new TweenEquation("Pow5.IN", powInFunction(5f));
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 0.75.
     */
    public static final TweenEquation pow0_75In = new TweenEquation("Pow0_75.IN", powInFunction(0.75f));
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 0.5. Optimized with {@link Math#sqrt(double)}.
     */
    public static final TweenEquation pow0_5In = new TweenEquation("Pow0_5.IN", a -> (float) Math.sqrt(a));
    /**
     * Accelerates using {@link #powInFunction(float)} and power of 0.25.
     */
    public static final TweenEquation pow0_25In = new TweenEquation("Pow0_25.IN", powInFunction(0.25f));
    /**
     * An alias for {@link #pow0_5In}, this is the inverse for {@link #pow2In}. Optimized with {@link Math#sqrt(double)}.
     */
    public static final TweenEquation sqrtIn = new TweenEquation("Sqrt.IN", a -> (float) Math.sqrt(a));
    /**
     * This is the inverse for {@link #pow3In}. Calls {@link Math#cbrt(double)} and casts it to float.
     */
    public static final TweenEquation cbrtIn = new TweenEquation("Cbrt.IN", a -> (float) Math.cbrt(a));

    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 2.
     */
    public static final TweenEquation pow2Out = new TweenEquation("Pow2.OUT", powOutFunction(2f));
    /**
     * Fast, then slow. This uses the same function as {@link #pow2Out}.
     */
    public static final TweenEquation fastSlow = new TweenEquation("FastSlow.OUT", pow2Out.fn);
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 3.
     */
    public static final TweenEquation pow3Out = new TweenEquation("Pow3.OUT", powOutFunction(3f));
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 4.
     */
    public static final TweenEquation pow4Out = new TweenEquation("Pow4.OUT", powOutFunction(4f));
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 5.
     */
    public static final TweenEquation pow5Out = new TweenEquation("Pow5.OUT", powOutFunction(5f));
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 0.75.
     */
    public static final TweenEquation pow0_75Out = new TweenEquation("Pow0_75.OUT", powOutFunction(0.75f));
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 0.5. Optimized with {@link Math#sqrt(double)}.
     */
    public static final TweenEquation pow0_5Out = new TweenEquation("Pow0_5.OUT", a -> 1f - (float) Math.sqrt(1f - a));
    /**
     * Decelerates using {@link #powOutFunction(float)} and power of 0.25.
     */
    public static final TweenEquation pow0_25Out = new TweenEquation("Pow0_25.OUT", powOutFunction(0.25f));
    /**
     * An alias for {@link #pow0_5Out}, this is the inverse of {@link #pow2Out}. Optimized with {@link Math#sqrt(double)}.
     */
    public static final TweenEquation sqrtOut = new TweenEquation("Sqrt.OUT", a -> 1f - (float) Math.sqrt(1f - a));
    /**
     * This is the inverse for {@link #pow3Out}. Optimized with {@link Math#cbrt(double)}.
     */
    public static final TweenEquation cbrtOut = new TweenEquation("Cbrt.OUT", a -> 1f - (float) Math.cbrt(1f - a));

    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 2.
     */
    public static final TweenEquation pow2OutIn = new TweenEquation("Pow2.OUTIN", powOutInFunction(2f));
    /**
     * Fast, then slow, then fast. This uses the same function as {@link #pow2OutIn}.
     */
    public static final TweenEquation fastSlowFast = new TweenEquation("FastSlowFast.OUTIN", pow2OutIn.fn);
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 3.
     */
    public static final TweenEquation pow3OutIn = new TweenEquation("Pow3.OUTIN", powOutInFunction(3f));
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 4.
     */
    public static final TweenEquation pow4OutIn = new TweenEquation("Pow4.OUTIN", powOutInFunction(4f));
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 5.
     */
    public static final TweenEquation pow5OutIn = new TweenEquation("Pow5.OUTIN", powOutInFunction(5f));
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 0.75.
     */
    public static final TweenEquation pow0_75OutIn = new TweenEquation("Pow0_75.OUTIN", powOutInFunction(0.75f));
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 0.5.
     */
    public static final TweenEquation pow0_5OutIn = new TweenEquation("Pow0_5.OUTIN", powOutInFunction(0.5f));
    /**
     * Accelerates/decelerates using {@link #powOutInFunction(float)} and power of 0.25.
     */
    public static final TweenEquation pow0_25OutIn = new TweenEquation("Pow0_25.OUTIN", powOutInFunction(0.25f));

    /**
     * Produces a TweenFunction that uses the given value and power variables.
     * When power is greater than 1, this starts slowly, speeds up in the middle and slows down at the end. The
     * rate of acceleration and deceleration changes based on the parameter. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction expFunction(final float value, final float power) {
        final float min = (float) Math.pow(value, -power), scale = 1f / (1f - min);
        return a -> {
            if (a <= 0.5f) return ((float)Math.pow(value, power * (a * 2f - 1f)) - min) * scale * 0.5f;
            return (2f - ((float)Math.pow(value, -power * (a * 2f - 1f)) - min) * scale) * 0.5f;
        };
    }

    /**
     * Produces a TweenFunction that uses the given value and power variables.
     * When power is greater than 1, this starts slowly and speeds up toward the end. The
     * rate of acceleration changes based on the parameter. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction expInFunction(final float value, final float power) {
        final float min = (float) Math.pow(value, -power), scale = 1f / (1f - min);
        return a -> ((float)Math.pow(value, power * (a - 1f)) - min) * scale;
    }

    /**
     * Produces a TweenFunction that uses the given value and power variables.
     * When power is greater than 1, this starts quickly and slows down toward the end. The
     * rate of deceleration changes based on the parameter. Negative parameters are not supported.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction expOutFunction(final float value, final float power) {
        final float min = (float) Math.pow(value, -power), scale = 1f / (1f - min);
        return a -> 1f - ((float)Math.pow(value, -power * a) - min) * scale;
    }

    /**
     * Accelerates and decelerates using {@link #expFunction(float, float)}, value of 2 and power of 5.
     */
    public static final TweenEquation exp5 = new TweenEquation("Exp5.INOUT", expFunction(2f, 5f));

    /**
     * Accelerates and decelerates using {@link #expFunction(float, float)}, value of 2 and power of 10.
     */
    public static final TweenEquation exp10 = new TweenEquation("Exp10.INOUT", expFunction(2f, 10f));

    /**
     * Accelerates using {@link #expInFunction(float, float)}, value of 2 and power of 5.
     */
    public static final TweenEquation exp5In = new TweenEquation("Exp5.IN", expInFunction(2f, 5f));

    /**
     * Accelerates using {@link #expInFunction(float, float)}, value of 2 and power of 10.
     */
    public static final TweenEquation exp10In = new TweenEquation("Exp10.IN", expInFunction(2f, 10f));

    /**
     * Decelerates using {@link #expOutFunction(float, float)}, value of 2 and power of 5.
     */
    public static final TweenEquation exp5Out = new TweenEquation("Exp5.OUT", expOutFunction(2f, 5f));

    /**
     * Decelerates using {@link #expOutFunction(float, float)}, value of 2 and power of 10.
     */
    public static final TweenEquation exp10Out = new TweenEquation("Exp10.OUT", expOutFunction(2f, 10f));


    /**
     * Accelerates and decelerates using {@link #expFunction(float, float)}, value of 2 and power of 5, but flipped.
     */
    public static final TweenEquation exp5OutIn = new TweenEquation("Exp5.OUTIN", exp5.fn.flip());

    /**
     * Accelerates and decelerates using {@link #expFunction(float, float)}, value of 2 and power of 10, but flipped.
     */
    public static final TweenEquation exp10OutIn = new TweenEquation("Exp10.OUTIN", exp10.fn.flip());


    /**
     * Produces a TweenFunction that uses the possible shapes of the Kumaraswamy distribution, but without
     * involving a random component. This can produce a wide range of shapes for the interpolation, and may require
     * generating several during development to get a particular shape you want. The a and b parameters must be greater
     * than 0.0, but have no other requirements. Most curves that this method produces are somewhat asymmetrical.
     * @see <a href="https://en.wikipedia.org/wiki/Kumaraswamy_distribution">Wikipedia's page on this distribution.</a>
     * @param a the Kumaraswamy distribution's a parameter
     * @param b the Kumaraswamy distribution's b parameter
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction kumaraswamyFunction(final double a, final double b) {
        final double A = 1.0 / a;
        final double B = 1.0 / b;
        return x -> (float) Math.pow(1.0 - Math.pow(1.0 - x, B), A);
    }

    /**
     * Produces more results toward the edges. Uses {@code kumaraswamyFunction(0.75f, 0.75f)}.
     */
    public static final TweenEquation extremeA = new TweenEquation("KumaraswamyA.INOUT", kumaraswamyFunction(0.75f, 0.75f));
    /**
     * Produces more results toward the edges. Uses {@code kumaraswamyFunction(0.5f, 0.5f)}.
     */
    public static final TweenEquation extremeB = new TweenEquation("KumaraswamyB.INOUT", kumaraswamyFunction(0.5f, 0.5f));
    /**
     * Produces more results toward the edges. Uses {@code kumaraswamyFunction(0.25f, 0.25f)}.
     */
    public static final TweenEquation extremeC = new TweenEquation("KumaraswamyC.INOUT", kumaraswamyFunction(0.25f, 0.25f));
    /**
     * Produces more results in the center. Uses {@code kumaraswamyFunction(2f, 2f)}.
     */
    public static final TweenEquation centralA = new TweenEquation("KumaraswamyA.OUTIN", kumaraswamyFunction(2f, 2f));
    /**
     * Produces more results in the center. Uses {@code kumaraswamyFunction(4f, 4f)}.
     */
    public static final TweenEquation centralB = new TweenEquation("KumaraswamyB.OUTIN", kumaraswamyFunction(4f, 4f));
    /**
     * Produces more results in the center. Uses {@code kumaraswamyFunction(6f, 6f)}.
     */
    public static final TweenEquation centralC = new TweenEquation("KumaraswamyC.OUTIN", kumaraswamyFunction(6f, 6f));
    /**
     * Produces more results near 0. Uses {@code kumaraswamyFunction(1f, 5f)}.
     */
    public static final TweenEquation mostlyLow = new TweenEquation("KumaraswamyD.IN", kumaraswamyFunction(1f, 5f));
    /**
     * Produces more results near 1. Uses {@code kumaraswamyFunction(5f, 1f)}.
     */
    public static final TweenEquation mostlyHigh = new TweenEquation("KumaraswamyD.OUT", kumaraswamyFunction(5f, 1f));

    /**
     * Produces an TweenFunction that uses the given shape and turning variables.
     * A wrapper around {@link TweenUtils#barronSpline(float, float, float)} to use it
     * as a TweenEquation or TweenFunction. Useful because it can imitate the wide variety of symmetrical
     * equations by setting turning to 0.5 and shape to some value greater than 1, while also being able to produce
     * the inverse of those equations by setting shape to some value between 0 and 1. It can also produce
     * asymmetrical equations by using a turning value other than 0.5 . These asymmetrical equations can be useful
     * because they can look like an INOUT or an OUTIN equation with one half larger than the other. Most IN or OUT
     * equations start and end with different derivatives, but if shape is greater than 1, the derivative will approach
     * 0 at both the start and end, as long as turning is in-between 0 and 1 (both exclusive). If turning is 0, this
     * will look like an OUT function when shape is greater than 1. If turning is 1, this will look like an IN function
     * when shape is greater than 1. In the case where shape is less than 1, these will be swapped.
     *
     * @param shape   must be greater than or equal to 0; values greater than 1 are more like "INOUT" equations
     * @param turning a value between 0.0 and 1.0, inclusive, where the shape changes
     * @return an TweenFunction that will use the given configuration
     */
    public static TweenFunction biasGainFunction(final float shape, final float turning) {
        return a -> barronSpline(a, shape, turning);
    }

    /**
     * Produces more results in the center; the first level of centrality. Uses {@code biasGainFunction(0.75f, 0.5f)}.
     */
    public static final TweenEquation biasGainCenteredA = new TweenEquation("BiasGainA.OUTIN", biasGainFunction(0.75f, 0.5f));
    /**
     * Produces more results in the center; the second level of centrality. Uses {@code biasGainFunction(0.5f, 0.5f)}.
     */
    public static final TweenEquation biasGainCenteredB = new TweenEquation("BiasGainB.OUTIN", biasGainFunction(0.5f, 0.5f));
    /**
     * Produces more results in the center; the third level of centrality. Uses {@code biasGainFunction(0.25f, 0.5f)}.
     */
    public static final TweenEquation biasGainCenteredC = new TweenEquation("BiasGainC.OUTIN", biasGainFunction(0.25f, 0.5f));
    /**
     * Produces more results near 0 and near 1; the third level of extremity. Uses {@code biasGainFunction(2f, 0.5f)}.
     */
    public static final TweenEquation biasGainExtremeA = new TweenEquation("BiasGainA.INOUT", biasGainFunction(2f, 0.5f));
    /**
     * Produces more results near 0 and near 1; the third level of extremity. Uses {@code biasGainFunction(3f, 0.5f)}.
     */
    public static final TweenEquation biasGainExtremeB = new TweenEquation("BiasGainB.INOUT", biasGainFunction(3f, 0.5f));
    /**
     * Produces more results near 0 and near 1; the third level of extremity. Uses {@code biasGainFunction(4f, 0.5f)}.
     */
    public static final TweenEquation biasGainExtremeC = new TweenEquation("BiasGainC.INOUT", biasGainFunction(4f, 0.5f));
    /**
     * Produces more results near 0. Uses {@code biasGainFunction(3f, 0.9f)}.
     */
    public static final TweenEquation biasGainMostlyLow = new TweenEquation("BiasGainD.IN", biasGainFunction(3f, 0.9f));
    /**
     * Produces more results near 1. Uses {@code biasGainFunction(3f, 0.1f)}.
     */
    public static final TweenEquation biasGainMostlyHigh = new TweenEquation("BiasGainD.OUT", biasGainFunction(3f, 0.1f));

    /**
     * Moves like a sine wave does; starts slowly, rises quickly, then ends slowly.
     */
    public static final TweenEquation sine = new TweenEquation("Sine.INOUT", a -> (a = MathUtils.sinDeg(a * 90f)) * a);

    /**
     * Moves like a sine wave does; starts slowly and rises quickly.
     */
    public static final TweenEquation sineIn = new TweenEquation("Sine.IN", a -> (1f - MathUtils.cosDeg(a * 90f)));
    /**
     * Moves like a sine wave does; starts quickly and slows down.
     */
    public static final TweenEquation sineOut = new TweenEquation("Sine.OUT", a -> MathUtils.sinDeg(a * 90f));
    /**
     * Moves like a sine wave does, but flipped; starts quickly, rises slowly, then ends quickly.
     */
    public static final TweenEquation sineOutIn = new TweenEquation("Sine.OUTIN", sine.fn.flip());

    /**
     * When graphed, forms two circular arcs; it starts slowly, accelerating rapidly towards the middle, then slows down
     * towards the end.
     */
    public static final TweenEquation circle = new TweenEquation("Circle.INOUT", a -> (a <= 0.5f
            ? (1f - (float)Math.sqrt(1f - a * a * 4f)) * 0.5f
            : ((float)Math.sqrt(1f - 4f * (a * (a - 2f) + 1f)) + 1f) * 0.5f));
    /**
     * When graphed, forms one circular arc, starting slowly and accelerating at the end.
     */
    public static final TweenEquation circleIn = new TweenEquation("Circle.IN", a -> (1f - (float)Math.sqrt(1f - a * a)));
    /**
     * When graphed, forms one circular arc, starting rapidly and decelerating at the end.
     */
    public static final TweenEquation circleOut = new TweenEquation("Circle.OUT", a -> ((float)Math.sqrt(a * (2f - a))));
    /**
     * When graphed, forms two circular arcs; it starts quickly, decelerating towards the middle, then speeds up
     * towards the end.
     */
    public static final TweenEquation circleOutIn = new TweenEquation("Circle.OUTIN", circle.fn.flip());

    /**
     * Produces a TweenFunction that uses the given {@code width, height, width, height, ...} float array.
     * Unlike {@link #bounceOutFunction(float...)}, this bounces at both the start and end of its interpolation.
     * Fair warning; using this is atypically complicated, and you should generally stick to using a predefined
     * TweenEquation, such as {@link #bounce4}. You can also hand-edit the values in pairs; if you do, every even
     * index is a width, and every odd index is a height. Later widths are no greater than earlier ones; this is also
     * true for heights. No width is typically greater than 1.5f, and they are always positive and less than 2f.
     *
     * @param pairs width, height, width, height... in pairs; typically none are larger than 1.5f, and all are positive
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction bounceFunction(final float... pairs) {
        final TweenFunction bOut = bounceOutFunction(pairs), iOut = o -> {
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
    public static final TweenEquation bounce2 = new TweenEquation("Bounce2.INOUT", bounceFunction(1.2f, 1f, 0.4f, 0.33f));
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 3 bounces.
     */
    public static final TweenEquation bounce3 = new TweenEquation("Bounce3.INOUT", bounceFunction(0.8f, 1f, 0.4f, 0.33f, 0.2f, 0.1f));
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 4 bounces.
     */
    public static final TweenEquation bounce4 = new TweenEquation("Bounce4.INOUT", bounceFunction(0.65f, 1f, 0.325f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 4 bounces. While both this and
     * {@link #bounce4} use 4 bounces, this matches the behavior of bounce in libGDX.
     */
    public static final TweenEquation bounce = new TweenEquation("Bounce.INOUT", bounceFunction(0.68f, 1f, 0.34f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 5 bounces.
     */
    public static final TweenEquation bounce5 = new TweenEquation("Bounce5.INOUT", bounceFunction(0.61f, 1f, 0.31f, 0.45f, 0.21f, 0.3f, 0.11f, 0.15f, 0.06f, 0.06f));

    /**
     * Produces a TweenFunction that uses the given {@code width, height, width, height, ...} float array.
     * This bounces at the end of its interpolation.
     * Fair warning; using this is atypically complicated, and you should generally stick to using a predefined
     * TweenEquation, such as {@link #bounce4Out}. You can also hand-edit the values in pairs; if you do, every even
     * index is a width, and every odd index is a height. Later widths are no greater than earlier ones; this is also
     * true for heights. No width is typically greater than 1.5f, and they are always positive and less than 2f.
     *
     * @param pairs width, height, width, height... in pairs; typically none are larger than 1.5f, and all are positive
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction bounceOutFunction(final float... pairs) {
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
            return a >= 0.98f ? MathUtils.lerp(f, 1f, 50f * (a - 0.98f)) : f;

        };
    }

    /**
     * Decelerates using {@link #bounceOutFunction(float...)}, with 2 bounces.
     */
    public static final TweenEquation bounce2Out = new TweenEquation("Bounce2.OUT", bounceOutFunction(1.2f, 1f, 0.4f, 0.33f));
    /**
     * Decelerates using {@link #bounceOutFunction(float...)}, with 3 bounces.
     */
    public static final TweenEquation bounce3Out = new TweenEquation("Bounce3.OUT", bounceOutFunction(0.8f, 1f, 0.4f, 0.33f, 0.2f, 0.1f));
    /**
     * Decelerates using {@link #bounceOutFunction(float...)}, with 4 bounces.
     */
    public static final TweenEquation bounce4Out = new TweenEquation("Bounce4.OUT", bounceOutFunction(0.65f, 1f, 0.325f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Decelerates using {@link #bounceOutFunction(float...)}, with 4 bounces. While both this and
     * {@link #bounce4Out} use 4 bounces, this matches the behavior of bounceOut in libGDX.
     */
    public static final TweenEquation bounceOut = new TweenEquation("Bounce.OUT", bounceOutFunction(0.68f, 1f, 0.34f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Decelerates using {@link #bounceOutFunction(float...)}, with 5 bounces.
     */
    public static final TweenEquation bounce5Out = new TweenEquation("Bounce5.OUT", bounceOutFunction(0.61f, 1f, 0.31f, 0.45f, 0.21f, 0.3f, 0.11f, 0.15f, 0.06f, 0.06f));

    /**
     * Produces a TweenFunction that uses the given {@code width, height, width, height, ...} float array.
     * This bounces at the start of its interpolation.
     * Fair warning; using this is atypically complicated, and you should generally stick to using a predefined
     * TweenEquation, such as {@link #bounce4In}. You can also hand-edit the values in pairs; if you do, every even
     * index is a width, and every odd index is a height. Later widths are no greater than earlier ones; this is also
     * true for heights. No width is typically greater than 1.5f, and they are always positive and less than 2f.
     *
     * @param pairs width, height, width, height... in pairs; typically none are larger than 1.5f, and all are positive
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction bounceInFunction(final float... pairs) {
        final TweenFunction bOut = bounceOutFunction(pairs);
        return a -> 1f - bOut.compute(1f - a);
    }

    /**
     * Decelerates using {@link #bounceInFunction(float...)}, with 2 bounces.
     */
    public static final TweenEquation bounce2In = new TweenEquation("Bounce2.IN", bounceInFunction(1.2f, 1f, 0.4f, 0.33f));
    /**
     * Decelerates using {@link #bounceInFunction(float...)}, with 3 bounces.
     */
    public static final TweenEquation bounce3In = new TweenEquation("Bounce3.IN", bounceInFunction(0.8f, 1f, 0.4f, 0.33f, 0.2f, 0.1f));
    /**
     * Decelerates using {@link #bounceInFunction(float...)}, with 4 bounces.
     */
    public static final TweenEquation bounce4In = new TweenEquation("Bounce4.IN", bounceInFunction(0.65f, 1f, 0.325f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Decelerates using {@link #bounceInFunction(float...)}, with 4 bounces. While both this and
     * {@link #bounce4In} use 4 bounces, this matches the behavior of bounceIn in libGDX.
     */
    public static final TweenEquation bounceIn = new TweenEquation("Bounce.IN", bounceInFunction(0.68f, 1f, 0.34f, 0.26f, 0.2f, 0.11f, 0.15f, 0.03f));
    /**
     * Decelerates using {@link #bounceInFunction(float...)}, with 5 bounces.
     */
    public static final TweenEquation bounce5In = new TweenEquation("Bounce5.IN", bounceInFunction(0.61f, 1f, 0.31f, 0.45f, 0.21f, 0.3f, 0.11f, 0.15f, 0.06f, 0.06f));

    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 2 bounces, but flipped.
     */
    public static final TweenEquation bounce2OutIn = new TweenEquation("Bounce2.OUTIN", bounce2.fn.flip());
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 3 bounces, but flipped.
     */
    public static final TweenEquation bounce3OutIn = new TweenEquation("Bounce3.OUTIN", bounce3.fn.flip());
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 4 bounces, but flipped.
     */
    public static final TweenEquation bounce4OutIn = new TweenEquation("Bounce4.OUTIN", bounce4.fn.flip());
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 4 bounces, but flipped.
     * While both this and {@link #bounce4OutIn} use 4 bounces, this matches the behavior of bounce in libGDX (flipped).
     */
    public static final TweenEquation bounceOutIn = new TweenEquation("Bounce.OUTIN", bounce.fn.flip());
    /**
     * Accelerates and decelerates using {@link #bounceFunction(float...)}, with 5 bounces, but flipped.
     */
    public static final TweenEquation bounce5OutIn = new TweenEquation("Bounce5.OUTIN", bounce5.fn.flip());

    /**
     * Produces a TweenFunction that uses the given scale variable.
     * This drops below 0.0 at the start of the range, accelerates very rapidly, exceeds 1.0 at the middle of the input
     * range, and ends returning 1.0. Negative parameters are not supported.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction swingFunction(final float scale) {
        final float sc = scale + scale;
        return a -> {
            if (a <= 0.5f) return ((sc + 1f) * (a += a) - sc) * a * a * 0.5f;
            return ((sc + 1f) * (a += a - 2f) + sc) * a * a * 0.5f + 1f;
        };
    }
    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 2.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing2 = new TweenEquation("Swing2.INOUT", swingFunction(2f));
    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 1.5.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing = new TweenEquation("Swing.INOUT", swingFunction(1.5f));
    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 3.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing3 = new TweenEquation("Swing3.INOUT", swingFunction(3f));
    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 0.75.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing0_75 = new TweenEquation("Swing0_75.INOUT", swingFunction(0.75f));
    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 0.5.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing0_5 = new TweenEquation("Swing0_5.INOUT", swingFunction(0.5f));

    /**
     * Produces a TweenFunction that uses the given scale variable.
     * This accelerates very rapidly, exceeds 1.0 at the middle of the input range, and ends returning 1.0. Negative
     * parameters are not supported.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction swingOutFunction(final float scale) {
        return a -> ((scale + 1f) * --a + scale) * a * a + 1f;
    }
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 2.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing2Out = new TweenEquation("Swing2.OUT", swingOutFunction(2f));
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 2. This uses the same function as
     * {@link #swing2Out}.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swingOut = new TweenEquation("Swing.OUT", swing2Out.fn);
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 3.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing3Out = new TweenEquation("Swing3.OUT", swingOutFunction(3f));
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 0.75.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing0_75Out = new TweenEquation("Swing0_75.OUT", swingOutFunction(0.75f));
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 0.5.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing0_5Out = new TweenEquation("Swing0_5.OUT", swingOutFunction(0.5f));

    /**
     * Produces a TweenFunction that uses the given scale variable.
     * This drops below 0.0 before the middle of the input range, later speeds up rapidly, and ends returning 1.0.
     * Negative parameters are not supported.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction swingInFunction(final float scale) {
        return a -> a * a * ((scale + 1f) * a - scale);
    }

    /**
     * Goes extra-low, using {@link #swingInFunction(float)} and scale of 2.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing2In = new TweenEquation("Swing2.IN", swingInFunction(2f));
    /**
     * Goes extra-low, using {@link #swingInFunction(float)} and scale of 2. This uses the same function as
     * {@link #swing2In}.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swingIn = new TweenEquation("Swing.IN", swing2In.fn);
    /**
     * Goes extra-low, using {@link #swingInFunction(float)} and scale of 3.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing3In = new TweenEquation("Swing3.IN", swingInFunction(3f));
    /**
     * Goes extra-low, using {@link #swingInFunction(float)} and scale of 0.75.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing0_75In = new TweenEquation("Swing0_75.IN", swingInFunction(0.75f));
    /**
     * Goes extra-low, using {@link #swingInFunction(float)} and scale of 0.5.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation swing0_5In = new TweenEquation("Swing0_5.IN", swingInFunction(0.5f));

    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 2, but flipped.
     * <br>
     * This will stay inside the 0-1 range for output.
     */
    public static final TweenEquation swing2OutIn = new TweenEquation("Swing2.OUTIN", swing2.fn.flip());
    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 1.5, but flipped.
     * <br>
     * This will stay inside the 0-1 range for output.
     */
    public static final TweenEquation swingOutIn = new TweenEquation("Swing.OUTIN", swing.fn.flip());
    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 3, but flipped.
     * <br>
     * This will stay inside the 0-1 range for output.
     */
    public static final TweenEquation swing3OutIn = new TweenEquation("Swing3.OUTIN", swing3.fn.flip());
    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 0.75, but flipped.
     * <br>
     * This will stay inside the 0-1 range for output.
     */
    public static final TweenEquation swing0_75OutIn = new TweenEquation("Swing0_75.OUTIN", swing0_75.fn.flip());
    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 0.5, but flipped.
     * <br>
     * This will stay inside the 0-1 range for output.
     */
    public static final TweenEquation swing0_5OutIn = new TweenEquation("Swing0_5.OUTIN", swing0_5.fn.flip());

    /**
     * Produces a TweenFunction that uses the given value, power, bounces, and scale variables.
     * This drops below 0.0 near the middle of the range, accelerates near-instantly, exceeds 1.0 just after that,
     * and ends returning 1.0. Negative parameters are not supported.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * This acts like {@code com.badlogic.gdx.math.Interpolation.elastic} rather than a Penner easing function.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction springFunction(final float value, final float power, final int bounces,
                                                final float scale) {
        final float bounce = bounces * (0.5f - (bounces & 1)) * MathUtils.PI2;
        return a -> (a <= 0.5f)
                ? (float)Math.pow(value, power * ((a += a) - 1f)) * MathUtils.sin(a * bounce) * scale * 0.5f
                : 1f - (float)Math.pow(value, power * ((a = 2f - a - a) - 1f)) * MathUtils.sin(a * bounce) * scale * 0.5f;
    }
    /**
     * Goes extra low, then extra-high, using {@link #springFunction(float, float, int, float)}.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * Value is 2, power is 10, bounces are 7, and scale is 1.
     */
    public static final TweenEquation spring = new TweenEquation("Spring.INOUT", springFunction(2f, 10f, 7, 1f));

    /**
     * Produces a TweenFunction that uses the given value, power, bounces, and scale variables.
     * This exceeds 1.0 just after the start of the range,
     * and ends returning 1.0. Negative parameters are not supported.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * This acts like {@code com.badlogic.gdx.math.Interpolation.elasticOut} rather than a Penner easing function.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction springOutFunction(final float value, final float power, final int bounces,
                                                   final float scale) {
        final float bounce = bounces * (0.5f - (bounces & 1));
        return a -> {
            float f = (1f - (float) Math.pow(value, power * -a) * MathUtils.sin((bounce - a * bounce) * MathUtils.PI2) * scale);
            return a <= 0.02f ? MathUtils.lerp(0f, f, a * 50f) : f;
        };
    }

    /**
     * Goes extra-high near the start, using {@link #springOutFunction(float, float, int, float)}.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * Value is 2, power is 10, bounces are 7, and scale is 1.
     */
    public static final TweenEquation springOut = new TweenEquation("Spring.OUT", springOutFunction(2f, 10f, 7, 1f));

    /**
     * Produces a TweenFunction that uses the given value, power, bounces, and scale variables.
     * This drops below 0.0 just before the end of the range,
     * but jumps up so that it ends returning 1.0. Negative parameters are not supported.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * This acts like {@code com.badlogic.gdx.math.Interpolation.elasticIn} rather than a Penner easing function.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction springInFunction(final float value, final float power, final int bounces,
                                                  final float scale) {
        final float bounce = bounces * (0.5f - (bounces & 1)) * MathUtils.PI2;
        return a -> {
            float f = (float) Math.pow(value, power * (a - 1)) * MathUtils.sin(a * bounce) * scale;
            return a >= 0.98f ? MathUtils.lerp(f, 1f, 50f * (a - 0.98f)) : f;
        };
    }
    /**
     * Goes extra-low near the end, using {@link #springInFunction(float, float, int, float)}.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * Value is 2, power is 10, bounces are 6, and scale is 1.
     */
    public static final TweenEquation springIn = new TweenEquation("Spring.IN", springInFunction(2f, 10f, 6, 1f));

    /**
     * Produces a TweenFunction that uses the given value, power, bounces, and scale variables.
     * This accelerates near-instantly, wiggles in to settle near the middle of the range, accelerates again near the
     * end, and finishes returning 1.0. Negative parameters are not supported.
     * <br>
     * This will typically stay inside the 0-1 range for output, unless value is much less than 2, power is much less
     * than 10, or scale is much larger than 1.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction springOutInFunction(final float value, final float power, final int bounces,
                                                     final float scale) {
        final float bounce = (bounces * (0.5f - (bounces & 1)) - 0.25f) * MathUtils.PI2;
        return a -> (a > 0.5f)
                ? (float)Math.pow(value, power * ((a += a - 1) - 1f)) * MathUtils.sin(a * bounce) * scale * 0.5f + 0.5f
                : 0.5f - (float)Math.pow(value, power * ((a = 1f - a - a) - 1f)) * MathUtils.sin(a * bounce) * scale * 0.5f;
    }
    /**
     * Stays within the mid-range, using {@link #springOutInFunction(float, float, int, float)}.
     * <br>
     * This will stay inside the 0-1 range for output,
     * <br>
     * Value is 2, power is 10, bounces are 7, and scale is 1.
     */
    public static final TweenEquation springOutIn = new TweenEquation("Spring.OUTIN", springOutInFunction(2f, 10f, 7, 1f));

    /**
     * Produces a TweenFunction that uses the given base, exponent, intensity, and scale variables.
     * When base and exponent are 2 and 10, this should act like {@code Elastic.INOUT} in Universal Tween Engine,
     * with a and p called scale and intensity.
     * This does not act like {@code Interpolation.elastic} in libGDX; use
     * {@link #springFunction(float, float, int, float)} for that.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * The functions this method produces are not well-behaved when their {@code alpha} parameter is less than 0.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction elasticFunction(final float base, final float exponent, final float intensity, final float scale) {
        final float a, s;
        if(scale < 1f) {
            a = 1f;
            s = intensity * 0.25f;
        } else {
            a = scale;
            s = intensity / MathUtils.PI2 * MathUtils.asin(1f / scale);
        }
        return alpha -> {
            if(alpha >= 1f) return 1f;
            float t = alpha * 2f;
            if(t < 1f) return -0.5f*(a*(float)Math.pow(base,exponent*(t-=1f)) * MathUtils.sin( (t-s)*MathUtils.PI2/intensity));
            return a*(float)Math.pow(base,-exponent*(t-=1f)) * MathUtils.sin( (t-s)*MathUtils.PI2/intensity )*0.5f + 1f;
        };
    }
    /**
     * Goes extra low, then extra-high, using {@link #elasticFunction(float, float, float, float)}.
     * This should act like {@code Elastic.INOUT} in Universal Tween Engine.
     * This does not act like {@code Interpolation.elastic} in libGDX; use {@link #spring} for that.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * Base is 2, exponent is 10, scale is 1.0, intensity is 0.45.
     */
    public static final TweenEquation elastic = new TweenEquation("Elastic.INOUT", elasticFunction(2, 10, 0.45f, 1f));

    /**
     * Produces a TweenFunction that uses the given base, exponent, intensity, and scale variables.
     * When base and exponent are 2 and 10, this should act like {@code Elastic.OUT} in Universal Tween Engine,
     * with a and p called scale and intensity.
     * This does not act like {@code Interpolation.elasticOut} in libGDX; use
     * {@link #springOutFunction(float, float, int, float)} for that.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * The functions this method produces are not well-behaved when their {@code alpha} parameter is less than 0.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction elasticOutFunction(final float base, final float exponent, final float intensity, final float scale) {
        final float a, s;
        if(scale < 1f) {
            a = 1f;
            s = intensity * 0.25f;
        } else {
            a = scale;
            s = intensity / MathUtils.PI2 * MathUtils.asin(1f / scale);
        }
        return alpha -> {
            if(alpha >= 1f) return 1f;
            return a*(float)Math.pow(base,-exponent*alpha) * MathUtils.sin((alpha-s)*MathUtils.PI2/intensity) + 1f;
        };
    }

    /**
     * Goes extra-high near the start, using {@link #elasticOutFunction(float, float, float, float)}.
     * This should act like {@code Elastic.OUT} in Universal Tween Engine.
     * This does not act like {@code Interpolation.elasticOut} in libGDX; use {@link #springOut} for that.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * Base is 2, exponent is 10, scale is 1, intensity is 0.3.
     */
    public static final TweenEquation elasticOut = new TweenEquation("Elastic.OUT", elasticOutFunction(2f, 10f, 0.3f, 1f));

    /**
     * Produces a TweenFunction that uses the given base, exponent, intensity, and scale variables.
     * When base and exponent are 2 and 10, this should act like {@code Elastic.IN} in Universal Tween Engine,
     * with a and p called scale and intensity.
     * This does not act like {@code Interpolation.elasticIn} in libGDX; use
     * {@link #springInFunction(float, float, int, float)} for that.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * The functions this method produces are not well-behaved when their {@code alpha} parameter is less than 0.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction elasticInFunction(final float base, final float exponent, final float intensity, final float scale) {
        final float a, s;
        if(scale < 1f) {
            a = 1f;
            s = intensity * 0.25f;
        } else {
            a = scale;
            s = intensity / MathUtils.PI2 * MathUtils.asin(1f / scale);
        }
        return alpha -> {
            if(alpha >= 1f) return 1f;
            return -(a*(float)Math.pow(base,exponent*(alpha-=1f)) * MathUtils.sin( (alpha-s)*MathUtils.PI2/intensity ));
        };
    }
    /**
     * Goes extra-low near the end, using {@link #elasticInFunction(float, float, float, float)}.
     * This should act like {@code Elastic.IN} in Universal Tween Engine.
     * This does not act like {@code Interpolation.elasticIn} in libGDX; use {@link #springIn} for that.
     * <br>
     * This will typically go outside the 0-1 range for output.
     * <br>
     * This should act like {@code Elastic.IN} in Universal Tween Engine.
     * Base is 2, exponent is 10, scale is 1, intensity is 0.3.
     */
    public static final TweenEquation elasticIn = new TweenEquation("Elastic.IN", elasticInFunction(2f, 10f, 0.3f, 1f));

    /**
     * Produces a TweenFunction that uses the given base, exponent, intensity, and scale variables.
     * When base and exponent are 2 and 10, this should act like {@code Elastic.INOUT} in Universal Tween Engine,
     * but with the IN and OUT halves swapped and with a and p called scale and intensity.
     * This does not act like {@code Interpolation.elastic} in libGDX; use
     * {@link #springOutInFunction(float, float, int, float)} for that.
     * <br>
     * This will typically stay inside the 0-1 range for output, unless base, exponent, or scale is changed
     * significantly. If base or exponent is much less than base=2 or exponent=10, the result may rarely go outside the
     * range; the same can happen if scale is much larger than 1.
     * <br>
     * The functions this method produces are not well-behaved when their {@code a} parameter is less than 0 or greater
     * than 1.
     * @return a TweenFunction that will use the given configuration
     */
    public static TweenFunction elasticOutInFunction(final float base, final float exponent, final float intensity, final float scale) {
        return elasticFunction(base, exponent, scale, intensity).flip();
    }
    /**
     * Stays within the mid-range, using {@link #elastic} with {@link TweenFunction#flip()} called on it.
     * This should act like {@code Elastic.INOUT} in Universal Tween Engine, but with the start and end
     * halves swapped and offset.
     * This does not act like {@code Interpolation.elastic} in libGDX; use {@link #springOutIn} for that.
     * <br>
     * This will stay inside the 0-1 range for output.
     * <br>
     * Base is 2, exponent is 10, scale is 1, intensity is 0.45.
     */
    public static final TweenEquation elasticOutIn = new TweenEquation("Elastic.OUTIN", elastic.fn.flip());


    // Aliases
    /**
     * Alias for {@link #pow2}.
     */
    public static final TweenEquation quadInOut = new TweenEquation("Quad.INOUT", pow2.fn);
    /**
     * Alias for {@link #pow2In}.
     */
    public static final TweenEquation quadIn = new TweenEquation("Quad.IN", pow2In.fn);
    /**
     * Alias for {@link #pow2Out}.
     */
    public static final TweenEquation quadOut = new TweenEquation("Quad.OUT", pow2Out.fn);
    /**
     * Alias for {@link #pow2OutIn}.
     */
    public static final TweenEquation quadOutIn = new TweenEquation("Quad.OUTIN", pow2OutIn.fn);
    /**
     * Alias for {@link #pow3}.
     */
    public static final TweenEquation cubicInOut = new TweenEquation("Cubic.INOUT", pow3.fn);
    /**
     * Alias for {@link #pow3In}.
     */
    public static final TweenEquation cubicIn = new TweenEquation("Cubic.IN", pow3In.fn);
    /**
     * Alias for {@link #pow3Out}.
     */
    public static final TweenEquation cubicOut = new TweenEquation("Cubic.OUT", pow3Out.fn);
    /**
     * Alias for {@link #pow3OutIn}.
     */
    public static final TweenEquation cubicOutIn = new TweenEquation("Cubic.OUTIN", pow3OutIn.fn);
    /**
     * Alias for {@link #pow4}.
     */
    public static final TweenEquation quartInOut = new TweenEquation("Quart.INOUT", pow4.fn);
    /**
     * Alias for {@link #pow4In}.
     */
    public static final TweenEquation quartIn = new TweenEquation("Quart.IN", pow4In.fn);
    /**
     * Alias for {@link #pow4Out}.
     */
    public static final TweenEquation quartOut = new TweenEquation("Quart.OUT", pow4Out.fn);
    /**
     * Alias for {@link #pow4OutIn}.
     */
    public static final TweenEquation quartOutIn = new TweenEquation("Quart.OUTIN", pow4OutIn.fn);
    /**
     * Alias for {@link #pow5}.
     */
    public static final TweenEquation quintInOut = new TweenEquation("Quint.INOUT", pow5.fn);
    /**
     * Alias for {@link #pow5In}.
     */
    public static final TweenEquation quintIn = new TweenEquation("Quint.IN", pow5In.fn);
    /**
     * Alias for {@link #pow5Out}.
     */
    public static final TweenEquation quintOut = new TweenEquation("Quint.OUT", pow5Out.fn);
    /**
     * Alias for {@link #pow5OutIn}.
     */
    public static final TweenEquation quintOutIn = new TweenEquation("Quint.OUTIN", pow5OutIn.fn);

    /**
     * Alias for {@link #exp10}.
     */
    public static final TweenEquation expoInOut = new TweenEquation("Expo.INOUT", exp10.fn);
    /**
     * Alias for {@link #exp10In}.
     */
    public static final TweenEquation expoIn = new TweenEquation("Expo.IN", exp10In.fn);
    /**
     * Alias for {@link #exp10Out}.
     */
    public static final TweenEquation expoOut = new TweenEquation("Expo.OUT", exp10Out.fn);
    /**
     * Alias for {@link #exp10OutIn}.
     */
    public static final TweenEquation expoOutIn = new TweenEquation("Expo.OUTIN", exp10OutIn.fn);

    /**
     * Alias for {@link #circle}.
     */
    public static final TweenEquation circInOut = new TweenEquation("Circ.INOUT", circle.fn);
    /**
     * Alias for {@link #circleIn}.
     */
    public static final TweenEquation circIn = new TweenEquation("Circ.IN", circleIn.fn);
    /**
     * Alias for {@link #circleOut}.
     */
    public static final TweenEquation circOut = new TweenEquation("Circ.OUT", circleOut.fn);
    /**
     * Alias for {@link #circleOutIn}.
     */
    public static final TweenEquation circOutIn = new TweenEquation("Circ.OUTIN", circleOutIn.fn);
    
    /**
     * Alias for {@link #swing}.
     */
    public static final TweenEquation swingInOut = swing;

    /**
     * Goes extra low, then extra-high, using {@link #swingFunction(float)} and scale of 1.2974547.
     * This matches the default Penner easing function easeInOutBack exactly.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation back = new TweenEquation("Back.INOUT", swingFunction(1.2974547f));
    /**
     * Alias for {@link #back}.
     */
    public static final TweenEquation backInOut = back;
    /**
     * Goes extra-high, using {@link #swingOutFunction(float)} and scale of 1.70158.
     * This matches the default Penner easing function easeOutBack exactly.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation backOut = new TweenEquation("Back.OUT", swingOutFunction(1.70158f));
    /**
     * Goes extra-high, using {@link #swingInFunction(float)} and scale of 1.70158.
     * This matches the default Penner easing function easeOutBack exactly.
     * <br>
     * This will typically go outside the 0-1 range for output.
     */
    public static final TweenEquation backIn = new TweenEquation("Back.IN", swingInFunction(1.70158f));
    /**
     * Should stay in-range, using {@link #swingFunction(float)} and scale of 1.2974547, but flipped.
     * This matches the exact parameter of the default Penner easing function easeInOutBack, but with the start and
     * end halves swapped and offset.
     * <br>
     * This will stay inside the 0-1 range for output.
     */
    public static final TweenEquation backOutIn = new TweenEquation("Back.OUTIN", back.fn.flip());

}
