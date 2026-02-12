package com.github.tommyettinger.tweenmachine;

import com.badlogic.gdx.utils.NumberUtils;

/**
 * Collection of miscellaneous utilities.
 *
 * @author Aurelien Ribon
 */
public class TweenUtils {
	/**
	 * Takes an easing name and gives you the corresponding TweenEquation.
	 * You probably won't need this, but tools will love that.
	 * This is present only for compatibility with the Universal Tween Engine.
	 * This is equivalent to {@link TweenEquations#get(String)}.
	 *
	 * @param easingName The name of a TweenEquation, like "Quad.INOUT".
	 * @return The parsed TweenEquation, or null if there is no match.
	 */
	public static TweenEquation parseEasing(String easingName) {
		return TweenEquations.get(easingName);
	}

	/**
	 * Like the fract() function available in GLSL shaders, this gets the fractional part of the float
	 * input {@code t} by returning a result similar to {@code t - Math.floor(t)} . For
	 * negative inputs, this doesn't behave differently than for positive; both will always return a
	 * float that is <code>0 &lt;= t &lt; 1</code> .
	 * @param t a finite float that should be between about {@code -Math.pow(2, 23)} and {@code Math.pow(2, 23)}
	 * @return the fractional part of t, as a float <code>0 &lt;= result &lt; 1</code>
	 */
	public static float fract(float t) {
		t -= (int)t - 1;
		return t - (int)t;
	}

	/**
	 * A generalization on bias and gain functions that can represent both; this version is branch-less.
	 * This is based on <a href="https://arxiv.org/abs/2010.09714">this micro-paper</a> by Jon Barron, which
	 * generalizes the earlier bias and gain rational functions by Schlick. The second and final page of the
	 * paper has useful graphs of what the s (shape) and t (turning point) parameters do; shape should be 0
	 * or greater, while turning must be between 0 and 1, inclusive. This effectively combines two different
	 * curving functions so that they continue into each other when x equals turning. The shape parameter will
	 * cause this to imitate "smoothstep-like" splines when greater than 1 (where the values ease into their
	 * starting and ending levels). It does the inverse when less than 1 (where values start like square
	 * root does, taking off very quickly, but also end like square does, landing abruptly at the ending
	 * level). You should only give x values between 0 and 1, inclusive.
	 *
	 * @param x       progress through the spline, from 0 to 1, inclusive
	 * @param shape   must be greater than or equal to 0; values greater than 1 are "normal interpolations"
	 * @param turning a value between 0.0 and 1.0, inclusive, where the shape changes
	 * @return a float between 0 and 1, inclusive
	 */
	public static float barronSpline(final float x, final float shape, final float turning) {
		final float d = turning - x;
		final int f = NumberUtils.floatToIntBits(d) >> 31, n = f | 1;
		return (turning * n - f) * (x + f) / (1.17549435E-38f - f + (x + shape * d) * n) - f;
	}

}
