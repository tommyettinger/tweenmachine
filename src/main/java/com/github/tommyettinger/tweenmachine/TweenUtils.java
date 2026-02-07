package com.github.tommyettinger.tweenmachine;

import com.github.tommyettinger.tweenmachine.equations.Back;
import com.github.tommyettinger.tweenmachine.equations.Bounce;
import com.github.tommyettinger.tweenmachine.equations.Circ;
import com.github.tommyettinger.tweenmachine.equations.Cubic;
import com.github.tommyettinger.tweenmachine.equations.Elastic;
import com.github.tommyettinger.tweenmachine.equations.Expo;
import com.github.tommyettinger.tweenmachine.equations.Linear;
import com.github.tommyettinger.tweenmachine.equations.Quad;
import com.github.tommyettinger.tweenmachine.equations.Quart;
import com.github.tommyettinger.tweenmachine.equations.Quint;
import com.github.tommyettinger.tweenmachine.equations.Sine;

/**
 * Collection of miscellaneous utilities.
 *
 * @author Aurelien Ribon
 */
public class TweenUtils {
	private static TweenEquation[] easings;

	/**
	 * Takes an easing name and gives you the corresponding TweenEquation.
	 * You probably won't need this, but tools will love that.
	 *
	 * @param easingName The name of an easing, like "Quad.INOUT".
	 * @return The parsed equation, or null if there is no match.
	 */
	public static TweenEquation parseEasing(String easingName) {
		if (easings == null) {
			easings = new TweenEquation[] {Linear.INOUT,
				Quad.IN, Quad.OUT, Quad.INOUT,
				Cubic.IN, Cubic.OUT, Cubic.INOUT,
				Quart.IN, Quart.OUT, Quart.INOUT,
				Quint.IN, Quint.OUT, Quint.INOUT,
				Circ.IN, Circ.OUT, Circ.INOUT,
				Sine.IN, Sine.OUT, Sine.INOUT,
				Expo.IN, Expo.OUT, Expo.INOUT,
				Back.IN, Back.OUT, Back.INOUT,
				Bounce.IN, Bounce.OUT, Bounce.INOUT,
				Elastic.IN, Elastic.OUT, Elastic.INOUT
			};
		}

		for (int i=0; i<easings.length; i++) {
			if (easingName.equals(easings[i].toString()))
				return easings[i];
		}

		return null;
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
