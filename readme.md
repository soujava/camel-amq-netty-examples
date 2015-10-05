# Camel - ActiveMQ - Netty - Examples and POCs


This module was created to test the Netty TooLongFrameException when used with OSGi containers.

This includes 2 test cases that are evidencing the problem from a unit test perspective.

Future implementations will include more examples of failing scenarios and problems found on day-to-day working with the stack.

# Camel - ActiveMQ - Netty - Examples and POCs
=========================

To build this project use

```mvn install```

To run this project from within Maven use

```mvn camel:run```



To run this project with FUSE or any other OSGi Karaf container, after starting the container `./fuse`

(make sure you did clean install before running this)

Add the features url:
```
features:addurl mvn:br.org.soujava/camel-amq-netty-examples/1.0/xml/features
```

Then install it:
```
features:install camel-amq-netty-examples
```