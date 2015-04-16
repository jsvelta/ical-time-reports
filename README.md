# ical-time-reports

iCal based time reporting application

## Required software

The following software is required:
 - Java Platform, Standard Edition
 - Java EE Software Development Kit
 - Netbeans IDE
 - Apache Maven

### Java Platform, Standard Edition

To build, deploy, and run the application, you need a copy of the Java Platform, Standard Edition Development Kit (JDK). You must use JDK 7 Update 65 or above or JDK 8 Update 20 or above. You can download JDK software from http://www.oracle.com/technetwork/java/javase/downloads/index.html.

### Java EE Software Development Kit

GlassFish Server Open Source Edition 4.1 is targeted as the build and runtime environment for the application. To build, deploy, and run the application, you need a copy of GlassFish Server and, optionally, NetBeans IDE. To obtain GlassFish Server, you must install the Java EE 7 Software Development Kit (SDK) Update 1, which you can download from http://www.oracle.com/technetwork/java/javaee/downloads/index.html. Make sure that you download the Java EE 7 SDK Update 1, not the Java EE 7 Web Profile SDK Update 1.

### Netbeans IDE

To run the application, you need the latest version of NetBeans IDE. You can download NetBeans IDE from https://netbeans.org/downloads/index.html. Make sure that you download the Java EE bundle.

#### To Install NetBeans IDE without GlassFish Server

When you install NetBeans IDE, do not install the version of GlassFish Server that comes with NetBeans IDE. To skip the installation of GlassFish Server, follow these steps.

 1. On the first page of the NetBeans IDE Installer wizard, deselect the check box for GlassFish Server and click *OK*.
 2. Accept both the License Agreement and the Junit License Agreement.
 3. Continue with the installation of NetBeans IDE.

#### To Add GlassFish Server as a Server Using NetBeans IDE

To run the application in NetBeans IDE, you must add your GlassFish Server as a server in NetBeans IDE. Follow these instructions to add GlassFish Server to NetBeans IDE.

 1. From the *Tools* menu, choose *Servers*.
 2. In the Servers wizard, click *Add Server*.
 3. Under *Choose Server*, select *GlassFish Server* and click *Next*.
 4. Under *Server Location*, browse to the location of the Java EE 7 SDK and click *Next*.
 5. Under *Domain Location*, select *Register Local Domain*.
 6. Click *Finish*.

### Apache Maven

To run the application from the command line, you need Maven 3.0 or higher. If you do not already have Maven, you can install it from http://maven.apache.org.

Be sure to add the maven-install/bin directory to your path.

If you are using NetBeans IDE to build and run the examples, it includes a copy of Maven.

## Starting and Stopping GlassFish Server

You can start and stop GlassFish Server using either NetBeans IDE or the command line.

### To Start GlassFish Server Using NetBeans IDE

 1. Click the *Services tab*.
 2. Expand *Servers*.
 3. Right-click the GlassFish Server instance and select *Start*.

### To Stop GlassFish Server Using NetBeans IDE

To stop GlassFish Server using NetBeans IDE, right-click the GlassFish Server instance and select *Stop*.

### To Start GlassFish Server Using the Command Line

To start GlassFish Server from the command line, open a terminal window or command prompt and execute the following:

```bat
asadmin start-domain
```

### To Stop GlassFish Server Using the Command Line

To stop GlassFish Server, open a terminal window or command prompt and execute:

```bat
asadmin stop-domain domain1
```

## Starting and Stopping the Java DB Server

GlassFish Server includes the Java DB database server.

To start the Java DB server from the command line, open a terminal window or command prompt and execute:

```bat
asadmin start-database
```

To stop the Java DB server from the command line, open a terminal window or command prompt and execute:

```bat
asadmin stop-database
```

### To Start the Database Server Using NetBeans IDE

When you start GlassFish Server using NetBeans IDE, the database server starts automatically. If you ever need to start the server manually, however, follow these steps.

 1. Click the *Services* tab.
 2. Expand *Databases*.
 3. Right-click Java DB and select *Start Server*.

### To Stop the Database Server Using NetBeans IDE

To stop the database using NetBeans IDE, right-click Java DB and select *Stop Server*.

## Running the application

This section describes how to build, package, deploy, and run the application.

### Running the application

#### To Build and Deploy the application Using NetBeans IDE

You must have already configured GlassFish Server as a Java EE server in NetBeans IDE, as described in **To Add GlassFish Server as a Server Using NetBeans IDE**.

 1. Make sure that GlassFish Server has been started (see **Starting and Stopping GlassFish Server**).
 2. If the database server is not already running, start it as described in **Starting and Stopping the Java DB Server**.
 3. From the *File* menu, choose *Open Project*.
 4. In the Open Project dialog box select the application folder (*ical-time-reports*) and click *Open Project*
 5. In the *Projects* tab, right-click the `ical-time-reports` project and select *Build*. This command creates a JDBC security realm named `jcaltimereportsRealm`, builds and packages the `entities`, `statistics`, `swing-gui` and `web` projects, and deploys `web` to GlassFish Server.

#### To Build and Deploy the application Using Maven

Make sure that GlassFish Server has started (see **Starting and Stopping GlassFish Server**).

If the database server is not already running, start it as described in **Starting and Stopping the Java DB Server**.

In a terminal window, go to `ical-time-reports` directory and enter the following command:

```bat
mvn install
```

This command creates a JDBC security realm named `jcaltimereportsRealm` and packages the `entities`, `statistics`, `swing-gui` and `web` projects, and deploys `web` to GlassFish Server.

#### Using the application
To use the application follow these instructions:
 1. In web browser, go to the following url: `http://localhost:8080/web`
 2. On the login page, enter *admin* in the *Username* field, and enter *heslo* in the *Password* field.