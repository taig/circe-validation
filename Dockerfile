FROM        openjdk:8u212-jdk-alpine3.9

RUN         apk upgrade --update
RUN         apk add --no-cache bash build-base git nodejs ruby-full ruby-dev
RUN         gem install --no-document jekyll

# Install sbt
RUN         wget -O /usr/local/bin/sbt https://git.io/sbt && \
            chmod 0755 /usr/local/bin/sbt

# Cache sbt
RUN         mkdir -p \
              ./cache/project/ \
              ./cache/src/main/scala/ \
              ./cache/src/test/scala/
ADD         ./project/build.properties ./cache/project/
RUN         cd ./cache/ && sbt -v exit

# Cache scala compiler bridge
ADD         ./scalaVersion.sbt ./cache/
RUN         echo "class App" > ./cache/src/main/scala/App.scala
RUN         cd ./cache/ && sbt -v +compile

# Cache plugins
ADD         ./project/plugins.sbt ./cache/project/
RUN         cd ./cache/ && sbt -v +compile

# Cache dependencies
ADD         ./project ./cache/project/
ADD         ./build.sbt ./cache/
RUN         mkdir -p ./cache/docs/
RUN         echo "Lorem ipsum" > ./cache/docs/index.md
RUN         cd ./cache/ && sbt -v ";+test;website/makeMicrosite"

# Clean cache
RUN         rm -r ./cache/

WORKDIR     /home/circe-validation/