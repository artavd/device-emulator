[![Build Status](https://secure.travis-ci.org/artavd/device-emulator.svg)](http://travis-ci.org/artavd/device-emulator)

# Device Emulator

This is an application that represents the Device Emulator service. It supposed to be executed on the server with configured ports (TCP, UDP, Serial or other). Each of these ports is bound to specific device emulator (or even real device) that send a messages in the custom format. So this server can be used as aggregator for different types of messaging devices or can emulate the behaviour of them.

## Components

1. **Console application** - portable standalone application that provide shell for configuration and execution devices configuration without any deployment. Also can be used as CLI for emulator services (see below).

2. **REST services** - services that supposed to be deployed on the dedicated server and executed there. Features:

  - persist configurations in the database.
  - support OAuth authorization and authentication.
  - support of account-specific configurations.
  - support of quoted access to the server resources (admins can grant access to specified ports range to users).
  - ...

3. **HTTP application** - web application to visualize the REST services from previous topic.

## How to build?

<TBD> Gradle tasks description

## How to run?

<TBD> Modules and options description

## REST API documentation:

<TBD> Description of REST API links and functions