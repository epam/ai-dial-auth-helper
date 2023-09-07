FROM amazoncorretto:11-alpine3.17

ADD ./build/distributions/aidial-auth-helper-*.tar /opt/epam/aidial/
RUN mv /opt/epam/aidial/aidial-auth-helper-*/* /opt/epam/aidial/
RUN rmdir /opt/epam/aidial/aidial-auth-helper-*

RUN addgroup -S aidial --gid 1801 \
    && adduser -D -H -S aidial -G aidial -u 1801 \
    && chown aidial:aidial -R /opt/epam/aidial

USER aidial

WORKDIR /opt/epam/aidial

ENTRYPOINT ["/opt/epam/aidial/bin/aidial-auth-helper"]