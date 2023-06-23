#!/bin/sh

PROJECT_NAME="auth-proxy"
CLASS_NAME=deltix.cortex.authproxy.AuthProxy

echo "JAVA_ARGS=" $JAVA_ARGS

exec java $JAVA_ARGS -cp "/opt/deltix/${PROJECT_NAME}/lib/*" "${CLASS_NAME}" "$@"
