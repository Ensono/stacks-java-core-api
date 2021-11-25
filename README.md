# Stacks core-api module

## Module Overview
This module provides basic functionality to create an MVC project. It's based on Spring and contains
the required functionality to handle authentication, token acquisition, filtering and API exception 
handling. It's intended to be a module for Amido Stacks, but can also be used in your project.

## Module Structure

In the following diagram you can see all the relevant files of this module. Be aware, pulling from
the repository will have some extra files that are not relevant to the logic but needed to build and 
deploy.

### Project structure:

    java
    \_.mvn
    : |_settings.xml
    |_archetype.properties
    |_pom.xml
    \_src
    : \_main
    :   \_java
    :	 \_com.amido.stacks.core.api
    :	  \_auth
    :	   |_AuthController.java
    :	  \_impl
    :	   |_AuthControllerImpl.java
    :	  \_dto
    :	   |_ErrorResponse.java
    :	   \_request
    :	    |_GenerateTokenRequest.java
    :	   \_response
    :	    |_GenerateTokenResponse.java
    :	    |_ResourceCreatedResponse.java
    :	   \_exception
    :	    |_ApiException.java
    :	    |_ApiExceptionAdvice.java
    :	   \_filter
    :	    |_CorrelationIdFilter.java
    :	    |_CorrelationIdFilterConfiguration.java

## How to use

There are four ways to integrate this module in your project:
- Use it as a [dependency](#dependency)
- Create a localized solution using [Maven Archetypes artifacts](#localized-solution-using-maven-archetypes) available in our Artifactory repo
- Clone this repo, [locally build](#building-the-module-locally) and used as a Maven dependency
- Clone this repo, create a [custom archetype](#creating-an-archetype) and then use it as a Maven dependency

### Accessing Artifactory

Our artifacts and archetypes are hosted on an Artifactory repository. To use either, from a project
other than Amido Stacks, you will need to add that repo in your `pom.xml`:
    
    <repositories>
        <repository>
            <snapshots />
            <id>snapshots</id>
            <name>default-maven-virtual</name>
            <url>https://amidostacks.jfrog.io/artifactory/default-maven-virtual</url>
        </repository>
    </repositories>

Alternatively you can also add this configuration as a profile in your Maven's `settings.xml` file
in the `.m2` folder in your home directory (any OS):
    
    <profiles>
        <profile>
            <repositories>
                <repository>
                  <snapshots/>
                  <id>snapshots</id>
                  <name>default-maven-virtual</name>
                  <url>https://amidostacks.jfrog.io/artifactory/default-maven-virtual</url>
                </repository>
            </repositories>
            <id>artifactory</id>
        </profile>
    </profiles>
    
    <activeProfiles>
        <activeProfile>artifactory</activeProfile>
    </activeProfiles>

### Dependency

#### Maven

In the `dependencies` section of your application's `pom.xml` add:

    <dependency>
        <groupId>com.amido.stacks.modules</groupId>
        <artifactId>stacks-core-api</artifactId>
        <version>TODO</version>
    </dependency>

Then you can do a `./mvnw clean compile` to fetch it; after that you can use it like any other
dependency.

#### Others

Use it as you'd use any dependency in your build tool.

### Localized solution using Maven Archetypes

If you wish to further customise the module to have your organisation's namespaces, you can create a
[Maven archetype](https://maven.apache.org/archetype/index.html). Archetypes are Maven's tool for
scaffolding and offer a lot of functionality, we suggest spending some time looking into them. We use
the Archetypes to create a template for you to adopt this module under your organisation's namespace.
To use the deployed archetypes:
1. Make and move to a new folder
2. Then run `mvn archetype:generate -DarchetypeGroupId='com.amido.stacks.modules' -DarchetypeArtifactId='stacks-core-api-archetype' -DarchetypeVersion='<archetype version>' -DgroupId='<your-group-id>' -DartifactId='<your-artifact-id>' -Dversion='<your-version>' -Dpackage='<package-name>'`
   1. `<your-group-id>` is a placeholder for your group ID
   2. `<your-artifact-id>` is a placeholder for your artifact ID
   3. `<your-version>` is a placeholder for your version
   4. `<package-name>` is a placeholder for the root package name and structure. It should start with your `groupdId` and continue with the name of the root package.
      1. For example, using `-DgroupId=com.test` and `-Dpackage=com.test.stacks` will instruct Maven to place the code in `src/main/java/com/test/stacks` and update all the relevant references accordingly (i.e. `imports`)
3. Go to the `pom.xml` file of the project you'll be using this module in and add it as a [dependency](#dependency)

**Example**: Using `-DgroupId=com.test` and `-Dpackage=com.test.stacks` will instruct Maven to place the code in `src/main/java/com/test/stacks` and update all the relevant references accordingly (i.e. `imports`)

> **If you previously had used this module under different namespace (i.e. the default `com.amido.stacks.core-api`):**
> Maven updates the imports for the module you generated ONLY. Any existing references in other
> projects will remain to the previous namespace, you will need to update them manually, or by simply
> deleting the relevant `import` statements and re-importing using the newly made module instead.

### Building the module locally

To build the module locally:
1. Clone this repo
2. Navigate to the `java` folder
3. run `./mvnw clean install` to install the module locally.
4. Add it as any other [dependency](#dependency)

### Creating an Archetype

If you wish to further customise the module to have your organisation's namespaces, you can create a 
[Maven archetype](https://maven.apache.org/archetype/index.html). Archetypes are Maven's tool for
scaffolding and offer a lot of functionality, we suggest spending some time looking into them.
To make, install and use an archetype follow these steps:
1. Clone this repo
2. Navigate to the `<directory you cloned the project into>/java` in the terminal
3. Then issue the following Maven commands, using the included wrapper:
    1. `./mvnw archetype:create-from-project -DpropertyFile='./archetype.properties'` - To create the archetype
    2. `cd target/generated-sources/archetype` - Navigate to the folder it was crated in
    3. `..\..\..\mvnw install` - Install the archetype locally
4. Make and navigate to a directory in which you'd like to crate the localized project into, ideally outside this project's root folder
5. To create the project we do `<path-to-mvn-executable>/mvnw archetype:generate -DarchetypeGroupId='com.amido' -DarchetypeArtifactId='stacks-core-api' -DarchetypeVersion='1.0.0-SNAPSHOT' -DgroupId='<your-group-id>' -DartifactId='<your-artifact-id>' -Dversion='<your-version>' -Dpackage='<package-name>'`
    1. `<your-group-id>` is a placeholder for your group ID
    2. `<your-artifact-id>` is a placeholder for your artifact ID
    3. `<your-version>` is a placeholder for your version
    4. `<package-name>` is a placeholder for the root package name and structure. It should start with your `groupdId` and continue with the name of the root package.
        1. For example, using `-DgroupId=com.test` and `-Dpackage=com.test.stacks` will instruct Maven to place the code in `src/main/java/com/test/stacks` and update all the relevant references accordingly (i.e. `imports`)
7. Go to the `pom.xml` file of the project you'll be using this module in and add it as a [dependency](#dependency)

**Example**: Using `-DgroupId=com.test` and `-Dpackage=com.test.stacks` will instruct Maven to place the code in `src/main/java/com/test/stacks` and update all the relevant references accordingly (i.e. `imports`)

> **If you previously had used this module under different namespace (i.e. the default `com.amido.stacks.core-api`):**
> Maven updates the imports for the module you generated ONLY. Any existing references in other 
> projects will remain to the previous namespace, you will need to update them manually, or by simply
> deleting the relevant `import` statements and re-importing using the newly made module instead.

