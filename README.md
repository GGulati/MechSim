MechSim
======
https://code.google.com/p/mechsim/

## Overview ##
Welcome to MechSim, short for Mechanical Simulation (the name's really creative). It allows the programmer to start working on the actual logic part of the robot very quickly by inheriting from the class Robot and overriding the Think method. You can add Sensors and MechanicalDevices to your robot.

MechSim has a .jar file library and JavaDocs for you to use while working with it. There's also a simple sample robot that uses the distance sensor to avoid walls.

Feel free to get started by downloading the JAR library and documentation, take a look at the demo, or go through the tutorial.

## Release Log ##

 * 01/26/13 Released on GitHub
 * 11/01/11 Source Code Released
  * Commented and annotated
  * Basic understanding of Java, OOP, and some math is required to fully understand the source code
  * Released under the GNU GPL v3 license
   * You can modify the software or create derivative works, but they must be freeware
 * 11/01/11 `v1.0.2.0` Bug fixes and optimization
  * Fixed a problem with the DistanceSensor, ColorSensor and InfraredSensor with detecting PhysicsObjects both in front of the orientation and directly behind the orientation. Now works as expected.
  * Rectangle-rectangle nudging improved to be less jittery. Now looks rather smooth, especially at low velocities.
  * Physics system optimized
   * Circle-circle collision detection sped up significantly
   * Collision response time improved slightly
 * 10/03/11 `v1.0.1.1` Bug fixes
  * Fixed minor rectangle-rectangle collision bug 
  * Removed a constructor for MechSim.Sensors.BumperSensor that didn't work properly
  * Fixed non-constant acceleration bug in MechSim.Mechanics.Motor; now accelerates at the same rate regardless of frame rate
 * 10/02/11 `v1.0.0.0` Feature complete