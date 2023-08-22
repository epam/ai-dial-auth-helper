FROM amazoncorretto:11-alpine3.17

ARG version
ARG project=dial-auth-proxy
ENV project=${project}

COPY ./build/install/$project /opt/deltix/$project
COPY ./src/main/docker/startup.sh /opt/deltix/$project/startup.sh
RUN chmod +x /opt/deltix/$project/startup.sh \
    && apk add --no-cache curl su-exec \
    && chmod +s /sbin/su-exec \
    && addgroup -S deltix --gid 1801 \
    && adduser -D -H -S deltix -G deltix -u 1801 \
    && mkdir /var/lib/$project \
    && chown deltix:deltix -R /var/lib/$project

USER deltix
WORKDIR /var/lib/$project
ENTRYPOINT "/opt/deltix/$project/startup.sh"
