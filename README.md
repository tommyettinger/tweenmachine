# tweenmachine
An updated fork of AurelienRibon's Universal Tween Engine

## Differences
The original Universal Tween Engine (UTE) is an excellent, versatile tool for interpolating be-*tween* arbitrary float
values in whatever objects you want. It also hasn't been updated in a long time! This repo is an attempt to make UTE
perform better on modern JVMs (including desktop OSes and Android) by removing the pooling that UTE does, while
expanding the options for TweenEquation types beyond the [standard Penner easing equations](https://easings.net).

Pooling was removed because, well, it wasn't performing very well as enough tweens started to be used. Various
operations involving pooling needed to perform linear-time operations on the entire pool if even one tween was removed.
These operations slow down as the pool gets bigger, but the pooling is largely irrelevant in the first place on modern
JVMs, which have had tremendous effort put into state-of-the-art garbage collectors. Some anecdotal evidence suggests
that simply removing pooling has already improved performance relative to UTE.

The process for defining a new TweenEquation in UTE was a little cumbersome. It had a predefined group of TweenEquation
constants stored in an enum, and no new equations could be added to that. Similarly, `TweenUtils.parseEasing(String)`
could only load `TweenEquation`s from a predefined group. This has been changed significantly in tweenmachine, basing
the approach for equations on `Interpolations` from the [digital](https://github.com/tommyettinger/digital) library.
Now you can create a `TweenEquation` given just a `String` tag and a single function, which may be a Java 8+ lambda,
method reference, or an old-style anonymous inner class implementing `TweenFunction`. This does not use any Java 8 APIs,
so it should work fine on RoboVM, which allows lambdas but doesn't define any existing `FunctionalInterface` types.
Creating a new `TweenEquation` automatically registers it in `TweenEquations`, and that allows you to get your new
equation with `TweenEquations.get(String)`. You can also get all known equations with
`TweenEquations.getTweenEquations()` or their names with `TweenEquations.getTags()`.

This on its own isn't much, but the approach here is also used to define quite a few more `TweenEquation` instances
out-of-the-box. Where before, we had `IN`, `OUT`, and `INOUT` variants for most `TweenEquation`s, now we have `OUTIN`
variants for almost anything that has an `IN` and `OUT` variant. The `OUTIN` variants act like `INOUT`, but with the
shapes for the start and end halves swapped, so they usually start quickly, slow down in the middle, and speed up at the
end. There are also various ways to define new `TweenFunction` instances quickly using parameters, based on
`Interpolation` in libGDX in most cases. A few of these act like UTE functions instead of Interpolation ones where the
names would have overlapped. `Elastic` equations act like UTE, where `Spring` equations act like libGDX `elastic` ones.
Similarly, `Swing` equations here act like the ones defined by libGDX, but `Back` equations act like the ones defined by
UTE (and the Penner easing equations). There are now asymmetrical `Kumaraswamy` equations based on the versatile
[Kumaraswamy distribution](https://en.wikipedia.org/wiki/Kumaraswamy_distribution#Quantile_function), and
often-symmetrical `BiasGain` equations based on 
[Jon Barron's parameterized generalization of bias and gain functions](https://arxiv.org/abs/2010.09714). There are also
the assorted equations from `Interpolation` in libGDX, such as `smooth`, `smoother`, `smooth2`, and so on, and more
options are available where only a few parameters were defined before.

This library depends on libGDX 1.12.1 or newer. It doesn't use any features that changed in 1.13.1 or 1.14.0, so using
either of those newer versions is fine as well.

## Get

You can use Maven Central to get a stable version like any other normal dependency.

In your core/build.gradle file, inside the last `dependencies` block:
```groovy
api "com.github.tommyettinger:tweenmachine:7.0.0"
```

If you use GWT, then you also need this, in html/build.gradle, inside the last `dependencies` block:

```groovy
implementation "com.github.tommyettinger:tweenmachine:7.0.0:sources"
```

If you use GWT, regardless of what is in Gradle, you need this in your GdxDefinition.gwt.xml file :
```xml
<inherits name="com.github.tommyettinger.tweenmachine" />
```

*Alternatively...*

[You can also Use JitPack.io !](https://jitpack.io/#tommyettinger/tweenmachine/c36c9c26c7) You would typically get the
latest commit that *isn't* `-SNAPSHOT` if you want the most recent code. When you click the Commits tab, the most
recent commits are listed, the first after `-SNAPSHOT` is usually a good choice. You only need to change `c36c9c26c7`
in the following examples to the 10-hex-digit commit identifier in the left column.

For a recent working commit, you can use this in core/build.gradle, inside the last `dependencies` block:
```groovy
dependencies {
    api "com.github.tommyettinger:tweenmachine:c36c9c26c7"
}
```

GWT has not been tested, but should work. GWT needs this in html/build.gradle, inside the last `dependencies` block:
```groovy
dependencies {
    implementation "com.github.tommyettinger:tweenmachine:c36c9c26c7:sources"
}
```

This still needs the GdxDefinition.gwt.xml line given above, for GWT only.

## Docs

JavaDocs should be hosted on [GitHub Pages for this repo](docs/apidocs/index.html) for the latest stable release.

If you want to see graphs of what each TweenEquation looks like,
[there are visual aids here](https://tommyettinger.github.io/tweenmachine/equations.html).

## License

[Apache 2.0](LICENSE).