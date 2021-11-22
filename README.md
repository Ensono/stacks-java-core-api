# Stacks core-api module

## Module Overview
This module provides basic functionality to create an MVC project. It's based on Spring and contains
the required functionality to handle authentication, token acquisition, filtering and API exception 
handling.


## Module Structure

In the following diagram you can see all the relevant files of this module. Be aware, pulling from
the repository will have some extra files that are not relevant to the logic but needed to build and 
deploy

Project structure:

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