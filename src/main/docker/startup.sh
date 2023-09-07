#!/bin/sh

PROJECT_NAME="dial-auth-proxy"
CLASS_NAME=com.epam.deltix.dial.authproxy.AuthProxy

echo "JAVA_ARGS=" $JAVA_ARGS

exec java $JAVA_ARGS -cp "/opt/deltix/${PROJECT_NAME}/lib/*" "${CLASS_NAME}" "$@"
