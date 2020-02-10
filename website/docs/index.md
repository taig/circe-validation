---
layout: home
title:  "Home"
section: "home"
---

# @NAME@

[![GitLab CI](https://gitlab.com/taig-github/circe-validation/badges/master/build.svg?style=flat-square)](https://gitlab.com/taig-github/circe-validation/pipelines)
[![GitLab CI](https://gitlab.com/taig-github/circe-validation/badges/master/coverage.svg?style=flat-square)](https://circe-validation.taig.io/coverage)
[![Maven Central](https://img.shields.io/maven-central/v/io.taig/circe-validation_2.13.svg?style=flat-square)](https://search.maven.org/search?q=g:io.taig%20AND%20a:circe-validation)
[![License](https://img.shields.io/github/license/taig/circe-validation?style=flat-square)](LICENSE)

## Installation


```scala
libraryDependencies += "@ORGANIZATION@" %%% "@MODULE@" % "@VERSION@"
```

## About

This repository is primarily an experimental exploration of validation handling with circe. My main concerns about the current implementation are:

 * `ValidatingDecoder` lives in `io.circe` to override the package scoped method `decodeAccumulating`
 * Validation failures cannot be distinguished from circe's `DecodingFailures`

## Usage

```scala mdoc:silent
import cats.implicits._
import cats.data.Validated._
import cats.data.ValidatedNel
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.Decoder
import io.taig.circe.validation._

object Validation {
  def email(value: String): ValidatedNel[String, String] =
    if (value.contains("@")) valid(value)
    else invalidNel("not an email")

  def min(length: Int)(value: String): ValidatedNel[String, String] =
    if (value.length < length) invalidNel(s"min $length")
    else valid(value)
}

case class Name(value: String)
case class Email(value: String)
case class Person(name: Name, email: Email)

def liftName(value: String): ValidatedNel[String, Name] =
  Validation.min(3)(value) map Name

def liftEmail(value: String): ValidatedNel[String, Email] =
  Validation.email(value) |+| Validation.min(5)(value) map Email

implicit val decoderName: Decoder[Name] = Decoder[String].verify(liftName)
implicit val decoderEmail: Decoder[Email] = Decoder[String].verify(liftEmail)
implicit val decoderPerson: Decoder[Person] = deriveDecoder

val text = """{ "name":"Qt", "email":"foo" }"""
val Right(json) = parse(text)
val cursor = json.hcursor

// Default behavior is decoding without accumulation
val Left(decodingFailure) = Decoder[Person].apply(cursor)
val Invalid(accumulatedDecodingFailures) = Decoder[Person].decodeAccumulating(cursor)
```

```scala mdoc
decodingFailure.show
accumulatedDecodingFailures.show
```