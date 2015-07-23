# Room Full of Cats [![Build Status](https://travis-ci.org/gamefolk/room-full-of-cats-mobile.svg?branch=master)](https://travis-ci.org/gamefolk/room-full-of-cats-mobile)

*Room Full of Cats* is a native game for Android, iOS, and desktop devices.

## Running the game

Get the source, making sure to pull in all the necessary submodules.

```sh
$ git clone --recursive https://github.com/gamefolk/room-full-of-cats-mobile.git
```

To build the game, you must have a Java version of at least 8u40, and an Oracle
JDK. If you use OpenJDK, you must install OpenJFX as well.

Runtime dependencies are handled by the gradle build system.

### Android

Running on Android requires an installation of the [Android SDK], with Android
Platform Lollipop downloaded (API Level 21).

```sh
$ ./gradlew androidInstall            # launch on attached Android device
```

### iOS

Running on iOS requires an up-to-date Mac with the latest version of [Xcode]
installed.

```sh
$ ./gradlew launchIPhoneSimulator     # launch on iPhone emulator
$ ./gradlew launchIPadSimulator       # launch on iPad emulator
$ ./gradlew launchIOSDevice           # launch on attached iOS device
```
If you get strange errors such as compilation failures due to missing files,
unexplained crashes on startup, etc., try running `./gradlew clean` before
building again.

### Desktop (Experimental)

```sh
$ ./gradlew run
```

If you experiences crashes when starting the game, please ensure that your
computer is a [JavaFX Certified System Configuration]. See heading __JavaFX
Media__.

## Background

Room Full of Cats is based on the [javafxports] and [robovm] projects, which
allow JavaFX 8 to run on Java and iOS through cross-compilation of Java
bytecode.

## Notes

Music, graphics, and environment (`app/src/main/resources/assets`), are not
included in this repository.

[javafxports]: http://javafxports.org
[robovm]: http://robovm.com
[Android SDK]: https://developer.android.com/sdk/installing/index.html
[Xcode]: https://developer.apple.com/xcode/
[JavaFX Certified System Configuration]: http://www.oracle.com/technetwork/java/javafx/downloads/supportedconfigurations-1506746.html
