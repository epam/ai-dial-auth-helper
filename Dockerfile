FROM gradle:8.3.0-jdk17-alpine as cache
WORKDIR /home/gradle/src
ENV GRADLE_USER_HOME /cache
COPY build.gradle settings.gradle ./
RUN gradle --no-daemon build --stacktrace

FROM gradle:8.3.0-jdk17-alpine as builder
COPY --from=cache /cache /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle --no-daemon build --stacktrace -PdisableCompression=true
RUN mkdir /build && tar -xf /home/gradle/src/build/distributions/aidial-auth-helper-*.tar --strip-components=1 -C /build

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /opt/epam/aidial

RUN addgroup -S aidial --gid 1801 \
    && adduser -D -H -S aidial -G aidial -u 1801

COPY --from=builder --chown=aidial:aidial /build/ .

RUN chown -R aidial:aidial /opt/epam/aidial

USER aidial

ENTRYPOINT ["/opt/epam/aidial/bin/aidial-auth-helper"]