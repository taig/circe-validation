# circe Validation

> Use cats Validated to create (Accumulating) circe Decoders

Please visit the [documentation](https://circe-validation.taig.io/) microsite to learn how to install and use _circe Validation_.

## Building the microsite

The microsite relies on [`sbt-microsites`](https://github.com/47deg/sbt-microsites) and does therefore require `ruby` and `jekyll` to be installed on your system. When these requirements are met, the microsite can be built as follows.

```
sbt website/makeMicrosite
cd website/target/site/
jekyll serve
```

Alternatively, when `ruby` and `jekyll` are not available the microsite can be built via docker.

```
docker build -t circe-validation .
docker run -it -p 4000:4000  -v $PWD:/home/circe-validation/ circe-validation 
sbt website/makeMicrosite
cd website/target/site/
jekyll serve --host=0.0.0.0
```

The site can now be opened in a web browser at [`http://localhost:4000/`](http://localhost:4000/).
