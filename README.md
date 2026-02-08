# tweenmachine
An updated fork of AurelienRibon's Universal Tween Engine

## Get
[Use JitPack.io for now.](https://jitpack.io/#tommyettinger/tweenmachine/3ec2bd2232)

For a recent working commit, you can use this in core/build.gradle, inside the last `dependencies` block:
```groovy
dependencies {
    api "com.github.tommyettinger:tweenmachine:3ec2bd2232"
}
```

GWT has not been tested, but should work. GWT needs this in html/build.gradle, inside the last `dependencies` block:
```groovy
dependencies {
    implementation "com.github.tommyettinger:tweenmachine:3ec2bd2232:sources"
}
```

And for GWT as well, this in your GdxDefinition.gwt.xml file :
```xml
<inherits name="com.github.tommyettinger.tweenmachine" />
```

## Docs

I'm still working on JavaDocs, but if you want to see graphs of what each TweenEquation looks like,
[there are visual aids here](https://tommyettinger.github.io/tweenmachine/equations.html).

## License

[Apache 2.0](LICENSE).