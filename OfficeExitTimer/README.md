# Office Exit Timer

A small native Android app with one main button:

START 9-HOUR TIMER

When pressed, it records the current wall-clock time and schedules an exact
Android alarm for exactly 9 hours later. The alarm produces a high-priority
notification saying that it is time to leave the office.

## Phone-only build

You do not need Android Studio.

The easiest phone-only route is to install a mobile Android IDE that can open
a standard Gradle Android project (for example AndroidIDE, if available for
your device), then open this project folder and run/build the debug APK.

The project is intentionally a standard Gradle project rather than an
Android-Studio-specific project.

## First run

1. Install the APK.
2. Open Office Exit Timer.
3. Android 12+ may ask you to allow "Alarms & reminders". Allow it.
4. Android 13+ asks for notification permission. Allow it.
5. Tap START 9-HOUR TIMER.

Example:
Start at 09:32 -> notification at 18:32.

## Important

On some phone manufacturers, aggressive battery-saving settings can interfere
with notifications. If reminders do not appear, set this app's battery usage
to "Unrestricted" / "Don't optimize", according to the phone's settings.

The app stores the pending time and attempts to restore the alarm after reboot.
