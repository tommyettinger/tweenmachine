package com.github.tommyettinger.tweenmachine.paths;

import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.tweenmachine.TweenPath;

/**
 * @author Aurelien Ribon
 */
public class Linear implements TweenPath {
	@Override
	public float compute(float t, float[] points, int pointsCnt) {
		int segment = MathUtils.floor((pointsCnt-1) * t);
		segment = Math.max(segment, 0);
		segment = Math.min(segment, pointsCnt-2);

		t = t * (pointsCnt-1) - segment;

		return points[segment] + t * (points[segment+1] - points[segment]);
	}
}
