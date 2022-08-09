@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  deploy_tool startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and DEPLOY_TOOL_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\deploy_tool.jar;%APP_HOME%\lib\scalardl-java-client-sdk-3.5.0.jar;%APP_HOME%\lib\org.everit.json.schema-1.12.1.jar;%APP_HOME%\lib\hashids-1.0.3.jar;%APP_HOME%\lib\scalardl-common-3.5.0.jar;%APP_HOME%\lib\scalardb-3.6.0.jar;%APP_HOME%\lib\scalardl-rpc-3.5.0.jar;%APP_HOME%\lib\guice-5.0.1.jar;%APP_HOME%\lib\picocli-4.6.1.jar;%APP_HOME%\lib\log4j-slf4j-impl-2.17.1.jar;%APP_HOME%\lib\metrics-jmx-4.2.2.jar;%APP_HOME%\lib\simpleclient_dropwizard-0.12.0.jar;%APP_HOME%\lib\cassandra-driver-core-3.6.0.jar;%APP_HOME%\lib\azure-cosmos-4.28.1.jar;%APP_HOME%\lib\metrics-core-4.2.2.jar;%APP_HOME%\lib\azure-core-1.27.0.jar;%APP_HOME%\lib\applicationautoscaling-2.17.69.jar;%APP_HOME%\lib\dynamodb-2.17.69.jar;%APP_HOME%\lib\aws-json-protocol-2.17.69.jar;%APP_HOME%\lib\protocol-core-2.17.69.jar;%APP_HOME%\lib\aws-core-2.17.69.jar;%APP_HOME%\lib\auth-2.17.69.jar;%APP_HOME%\lib\regions-2.17.69.jar;%APP_HOME%\lib\sdk-core-2.17.69.jar;%APP_HOME%\lib\apache-client-2.17.69.jar;%APP_HOME%\lib\netty-nio-client-2.17.69.jar;%APP_HOME%\lib\http-client-spi-2.17.69.jar;%APP_HOME%\lib\metrics-spi-2.17.69.jar;%APP_HOME%\lib\profiles-2.17.69.jar;%APP_HOME%\lib\json-utils-2.17.69.jar;%APP_HOME%\lib\utils-2.17.69.jar;%APP_HOME%\lib\slf4j-api-1.7.33.jar;%APP_HOME%\lib\scalardb-rpc-3.6.0.jar;%APP_HOME%\lib\grpc-netty-1.46.0.jar;%APP_HOME%\lib\grpc-services-1.46.0.jar;%APP_HOME%\lib\grpc-protobuf-1.46.0.jar;%APP_HOME%\lib\grpc-stub-1.46.0.jar;%APP_HOME%\lib\grpc-core-1.46.0.jar;%APP_HOME%\lib\grpc-protobuf-lite-1.46.0.jar;%APP_HOME%\lib\grpc-api-1.46.0.jar;%APP_HOME%\lib\protobuf-java-util-3.19.2.jar;%APP_HOME%\lib\guava-31.1-jre.jar;%APP_HOME%\lib\javax.json-api-1.1.4.jar;%APP_HOME%\lib\javax.json-1.1.4.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.13.3.jar;%APP_HOME%\lib\jackson-dataformat-xml-2.13.3.jar;%APP_HOME%\lib\jackson-annotations-2.13.3.jar;%APP_HOME%\lib\jackson-module-afterburner-2.13.3.jar;%APP_HOME%\lib\jackson-core-2.13.3.jar;%APP_HOME%\lib\jackson-databind-2.13.3.jar;%APP_HOME%\lib\toml4j-0.7.2.jar;%APP_HOME%\lib\log4j-core-2.17.1.jar;%APP_HOME%\lib\json-20190722.jar;%APP_HOME%\lib\commons-validator-1.6.jar;%APP_HOME%\lib\handy-uri-templates-2.1.8.jar;%APP_HOME%\lib\re2j-1.3.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\postgresql-42.3.3.jar;%APP_HOME%\lib\checker-qual-3.12.0.jar;%APP_HOME%\lib\error_prone_annotations-2.11.0.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\gson-2.8.9.jar;%APP_HOME%\lib\log4j-api-2.17.1.jar;%APP_HOME%\lib\bcpkix-jdk15on-1.59.jar;%APP_HOME%\lib\bcprov-jdk15on-1.59.jar;%APP_HOME%\lib\simpleclient_servlet-0.12.0.jar;%APP_HOME%\lib\simpleclient_hotspot-0.12.0.jar;%APP_HOME%\lib\jetty-servlet-9.4.43.v20210629.jar;%APP_HOME%\lib\jooq-3.13.2.jar;%APP_HOME%\lib\commons-dbcp2-2.8.0.jar;%APP_HOME%\lib\mysql-connector-java-8.0.22.jar;%APP_HOME%\lib\mssql-jdbc-8.4.1.jre8.jar;%APP_HOME%\lib\commons-digester-1.8.1.jar;%APP_HOME%\lib\httpclient-4.5.13.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar;%APP_HOME%\lib\joda-time-2.10.2.jar;%APP_HOME%\lib\simpleclient_servlet_common-0.12.0.jar;%APP_HOME%\lib\simpleclient_common-0.12.0.jar;%APP_HOME%\lib\simpleclient-0.12.0.jar;%APP_HOME%\lib\jetty-security-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-util-ajax-9.4.43.v20210629.jar;%APP_HOME%\lib\scalar-admin-1.2.0.jar;%APP_HOME%\lib\azure-core-http-netty-1.11.9.jar;%APP_HOME%\lib\reactor-netty-http-1.0.15.jar;%APP_HOME%\lib\netty-codec-http2-4.1.73.Final.jar;%APP_HOME%\lib\reactor-netty-core-1.0.15.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.73.Final.jar;%APP_HOME%\lib\netty-reactive-streams-http-2.0.5.jar;%APP_HOME%\lib\netty-codec-http-4.1.73.Final.jar;%APP_HOME%\lib\netty-resolver-dns-native-macos-4.1.72.Final-osx-x86_64.jar;%APP_HOME%\lib\netty-resolver-dns-classes-macos-4.1.72.Final.jar;%APP_HOME%\lib\netty-resolver-dns-4.1.72.Final.jar;%APP_HOME%\lib\netty-reactive-streams-2.0.5.jar;%APP_HOME%\lib\netty-handler-4.1.73.Final.jar;%APP_HOME%\lib\jnr-posix-3.0.44.jar;%APP_HOME%\lib\jnr-ffi-2.1.7.jar;%APP_HOME%\lib\micrometer-core-1.8.2.jar;%APP_HOME%\lib\HdrHistogram-2.1.12.jar;%APP_HOME%\lib\reactor-core-3.4.14.jar;%APP_HOME%\lib\reactive-streams-1.0.3.jar;%APP_HOME%\lib\jaxb-api-2.3.1.jar;%APP_HOME%\lib\annotations-2.17.69.jar;%APP_HOME%\lib\commons-pool2-2.8.1.jar;%APP_HOME%\lib\proto-google-common-protos-2.0.1.jar;%APP_HOME%\lib\protobuf-java-3.19.2.jar;%APP_HOME%\lib\ojdbc8-19.8.0.0.jar;%APP_HOME%\lib\ucp-19.8.0.0.jar;%APP_HOME%\lib\oraclepki-19.8.0.0.jar;%APP_HOME%\lib\osdt_core-19.8.0.0.jar;%APP_HOME%\lib\osdt_cert-19.8.0.0.jar;%APP_HOME%\lib\simplefan-19.8.0.0.jar;%APP_HOME%\lib\ons-19.8.0.0.jar;%APP_HOME%\lib\orai18n-19.8.0.0.jar;%APP_HOME%\lib\xdb-19.8.0.0.jar;%APP_HOME%\lib\xmlparserv2-19.8.0.0.jar;%APP_HOME%\lib\simpleclient_tracer_otel-0.12.0.jar;%APP_HOME%\lib\simpleclient_tracer_otel_agent-0.12.0.jar;%APP_HOME%\lib\jetty-server-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-http-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-io-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-util-9.4.43.v20210629.jar;%APP_HOME%\lib\perfmark-api-0.25.0.jar;%APP_HOME%\lib\netty-transport-native-epoll-4.1.73.Final-linux-x86_64.jar;%APP_HOME%\lib\netty-transport-native-kqueue-4.1.73.Final-osx-x86_64.jar;%APP_HOME%\lib\netty-transport-classes-epoll-4.1.73.Final.jar;%APP_HOME%\lib\netty-transport-classes-kqueue-4.1.73.Final.jar;%APP_HOME%\lib\netty-transport-native-unix-common-4.1.73.Final.jar;%APP_HOME%\lib\netty-codec-socks-4.1.73.Final.jar;%APP_HOME%\lib\netty-codec-dns-4.1.72.Final.jar;%APP_HOME%\lib\netty-codec-4.1.73.Final.jar;%APP_HOME%\lib\netty-transport-4.1.73.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.73.Final.jar;%APP_HOME%\lib\jffi-1.2.16.jar;%APP_HOME%\lib\jffi-1.2.16-native.jar;%APP_HOME%\lib\asm-commons-5.0.3.jar;%APP_HOME%\lib\asm-analysis-5.0.3.jar;%APP_HOME%\lib\asm-util-5.0.3.jar;%APP_HOME%\lib\asm-tree-5.0.3.jar;%APP_HOME%\lib\asm-5.0.3.jar;%APP_HOME%\lib\jnr-x86asm-1.0.2.jar;%APP_HOME%\lib\jnr-constants-0.9.9.jar;%APP_HOME%\lib\netty-tcnative-boringssl-static-2.0.47.Final.jar;%APP_HOME%\lib\LatencyUtils-2.0.3.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\third-party-jackson-core-2.17.69.jar;%APP_HOME%\lib\eventstream-1.0.1.jar;%APP_HOME%\lib\httpcore-4.4.13.jar;%APP_HOME%\lib\netty-resolver-4.1.73.Final.jar;%APP_HOME%\lib\netty-common-4.1.73.Final.jar;%APP_HOME%\lib\simpleclient_tracer_common-0.12.0.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\annotations-4.1.1.4.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.19.jar;%APP_HOME%\lib\grpc-context-1.46.0.jar;%APP_HOME%\lib\netty-tcnative-classes-2.0.47.Final.jar;%APP_HOME%\lib\woodstox-core-6.2.7.jar;%APP_HOME%\lib\stax2-api-4.2.1.jar;%APP_HOME%\lib\commons-codec-1.11.jar


@rem Execute deploy_tool
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %DEPLOY_TOOL_OPTS%  -classpath "%CLASSPATH%" com.scalar.ist.tools.DeployTool %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable DEPLOY_TOOL_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%DEPLOY_TOOL_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
