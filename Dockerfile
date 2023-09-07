FROM amazoncorretto:11-alpine3.17

ADD ./build/distributions/aidial-auth-helper-*.tar /opt/epam/aidial/
RUN mv /opt/epam/aidial/aidial-auth-helper-*/* /opt/epam/aidial/
RUN rmdir /opt/epam/aidial/aidial-auth-helper-*

ENTRYPOINT ["/opt/epam/aidial/bin/aidial-auth-helper"]