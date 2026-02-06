package com.github.tommyettinger.tweenmachine;

import com.github.tommyettinger.tweenmachine.paths.CatmullRom;
import com.github.tommyettinger.tweenmachine.paths.Linear;

/**
 * Collection of built-in paths.
 *
 * @author Aurelien Ribon
 */
public interface TweenPaths {
	public static final Linear linear = new Linear();
	public static final CatmullRom catmullRom = new CatmullRom();
}
