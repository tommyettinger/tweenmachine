package com.github.tommyettinger.tweenmachine;

import com.github.tommyettinger.tweenmachine.paths.CatmullRom;
import com.github.tommyettinger.tweenmachine.paths.Linear;

/**
 * Collection of built-in paths.
 *
 * @author Aurelien Ribon
 */
public interface TweenPaths {
	Linear linear = new Linear();
	CatmullRom catmullRom = new CatmullRom();
}
