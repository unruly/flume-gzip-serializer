# flume-gzip-serializer

[![Build Status](https://travis-ci.org/unruly/flume-gzip-serializer.svg?branch=master)](https://travis-ci.org/unruly/flume-gzip-serializer)

A custom serializer to gzip files before sending to an HDFS sink.

## Motivation

A Flume agent, with specific configuration, will write corrupt gzip files to AWS S3.

This issue occurs when using an HDFS sink pointing to S3, a CompressedStream fileType and gzip codeC e.g.

```
agent.sinks.s3Sink.type = hdfs
agent.sinks.s3Sink.hdfs.path = s3n://my-bucket/
agent.sinks.s3Sink.hdfs.fileType = CompressedStream
agent.sinks.s3Sink.hdfs.codeC = gzip
```

At time of writing, this is a Major bug in Flume and remains unresolved (https://issues.apache.org/jira/browse/FLUME-2967).

## Usage

### Build Instructions

In the root of the project, run `mvn clean package`

This will create the project artifact `flume-gzip-serializer.jar` in `target/`

### Deployment

Copy the `.jar` file to `${FLUME_HOME}/plugins.d/gzip-serializer/lib/` (see [Flume Docs for Installing Third-party Plugins](http://flume.apache.org/FlumeUserGuide.html#installing-third-party-plugins))

### Configuration

In the Flume config file, set up your HDFS sink to use the flume-gzip-serializer as below:

```
agent.sinks.s3Sink.serializer = co.unruly.flume.GzipSerializer$Builder
agent.sinks.s3Sink.hdfs.fileType = DataStream
agent.sinks.s3Sink.hdfs.writeFormat = Text
agent.sinks.s3Sink.hdfs.fileSuffix = .gz
```

### Options

By default, this serializer will add new lines between events. To override this behaviour, you can configure Flume as below:

```
agents.sinks.s3Sink.serializer.appendNewline = false
```

## Tests

To run tests:

`mvn clean test`