package com.github.tommyettinger.tweenmachine;

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
}
