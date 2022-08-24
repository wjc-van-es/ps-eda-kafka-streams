# ps-eda-kafka-streams

## Content
This repository contains example source code from the PluralSight course:

### [Designing Event-driven Applications Using Apache Kafka Ecosystem](https://app.pluralsight.com/library/courses/designing-event-driven-applications-apache-kafka-ecosystem/table-of-contents)

### by Bogdan Sucaciu

### Code example from Module 5: [Building Your First Streaming Application](https://app.pluralsight.com/course-player?clipId=27fdcb71-0174-43c2-8a7a-e02613bce273) and
### Code example from Module 6: [Building a Streaming Application with KSQL](https://app.pluralsight.com/course-player?clipId=e0ae3c21-62f3-4974-8153-55d3b9d59fd4)
I enjoyed this course very much and I would recommend it to anyone who needs an introduction in event-driven design and would like
to use Apache Kafka software to implement it.

## Purpose
In the course the example was presented running with locally installed kafka, zookeeper & the Confluent schema-registry software components.
This requires some work, which may be instructive if you want to learn about some basic principles of Kafka and how to configure it.
However, it is much more convenient to run all necessary software components in separate docker containers. Especially when you are
already familiar with Docker and Docker Compose technology.

### The software components
The example code, which is basically a producer and a Streams API client (Consumes from one topic and produces to another) 
both using AVRO schema's for (de)serializing both the key and value part of the messages being written to (and read from) 
the topic. This example code is still build and run as small Java applications executing their respective main methods 
on your local computer. Presumably with help from your favorite IDE. (So this part isn´t deployed to docker containers yet).

The Kafka cluster the example code communicates with, however, is entirely deployed as docker containers:
- one container with a single Apache kafka broker, listening on port 9092,
- one container with a single Zookeeper instance, listening on port 2181,
- one container with the Confluent schema-registry server, listening on port 8081.
- one container with the Confluent ksqldb-server, listening on port 8088,
- one container with the ksqldb-cli

---
**Note**

Take a look at the [Docker Prerequisites](https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html#prerequisites)
of the Quickstart documentation. As more containers are spun up in this example you may have to adjust the RAM memory
allocated in your Windows and Mac OS environment, because the respective Docker VM solutions have a limited default 
configured. When you have installed the Docker engine on a Linux distro, you are probably already good to go.

---

### Making good use of the Confluent Platform Community Edition components
To get this set up to work quickly, I created a [docker/docker-compose.yml](docker/docker-compose.yml) file based on the
one found in
[GitHub repo: confluentinc cp-all-in-one-community 7.2.1-post](https://github.com/confluentinc/cp-all-in-one/tree/7.2.1-post/cp-all-in-one-community).
- 7.2.1-post is the current default branch reflecting the latest versions of the Apache Kafka & Confluent technology
  stack at the date of writing (August 2022).
- cp-all-in-one-community refers to all components of Confluent platform technology stack that fall under the
  [confluent-community-license](https://www.confluent.io/confluent-community-license/). All source code under this licence
  may be accessed, modified and redistributed freely except for creating a SaaS that tries to compete with Confluent.
- To run the original cp-all-in-one-community docker compose offering and explore their code examples see:
    - [cp-all-in-one-community documentation](https://docs.confluent.io/platform/current/tutorials/build-your-own-demos.html?utm_source=github&utm_medium=demo&utm_campaign=ch.examples_type.community_content.cp-all-in-one#cp-all-in-one-community)
    - [CE Docker Quickstart documentation](https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html)
    - [Further code examples in various languages](https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/clients-all-examples.html#clients-all-examples)

From this all-in-one [`docker-compose.yml`](https://github.com/confluentinc/cp-all-in-one/blob/7.2.1-post/cp-all-in-one-community/docker-compose.yml)
, which defines all the components that are a part of the Confluent platform community edition,
we only took the three services that are needed to make the example code work and copied them in our own Docker Compose yaml file.
So, under the hood, we are using Docker images, made available by Confluent (for which we are grateful).

## Changes made to the original example source code.

### Introduction of the [fraud-detection-interface](fraud-detection-interface/pom.xml) maven module
The most important change I made was to create a separate fraud-detection-interface maven module. This module contains
Java classes that are generated from an AVRO schema, [avro/order_schema.avsc](fraud-detection-interface/src/main/resources/avro/order_schema.avsc)
 using the `org.apache.avro:avro-maven-plugin`.

These Java classes are part of the messages put together, serialized to an AVRO byte stream and send to the topic by the
producers and then read from the topic by the consumers. So the transactions-producer, background-producer and the
kafka-streams modules depend on the fraud-detection-interface module.

In the original example the Java classes were generated manually on the command line and copied as model packages into
the other maven modules.

My introduction of the separate fraud-detection-interface maven module
- makes running the example less complex as the generated Java classes are created automatically as part of a maven build
  of the project.
- reduces code duplication.
  I think both are great benefits that didn´t take much effort to accomplish.

---
**Note**

For IntelliJ to notice the content of the [fraud-detection-interface/src/main/generated](fraud-detection-interface/src/main/generated)
directory, you need to mark the directory as *Generated Sources Root* by right-clicking on it in the Project view window and
choosing *Mark Directory as* > *Generated Sources Root* from the context menu.

---

### Updating all maven dependencies
I made an effort to update all maven dependencies to the versions available now (August 2022).

## Prerequisites
- A JDK should be installed, version 8 is the minimal requirement, but I tested this example with version 17.
- Maven, I tested the example with version 3.8.1
- Docker (including Docker Compose, the docker-compose-plugin is the most recent version v2.6.0, where the commands
  start with `docker compose` rather than `docker-compose`. The latter is a deprecated older version 1.29.2)

## Usage
- Open a terminal in the project/repository root dir
    ```bash
    $ cd docker
    $ docker compose up -d
    $ docker compose ps
    ```
- When the last command shows you that all three services are up and running, you can proceed to create the
  `user-tracking-avro` topic in the same terminal with
   ```bash
   $ ./create-topic.sh
   ```
- Build the example code with maven (from the project/repository root dir)
  ```bash
  $ mvn clean compile -e
  ```

### The Streaming API code example of module 5
- On the command line or within your IDE
    - Run the Main class of the kafka-streams module [com.pluralsight.kafka.streams.FraudDetectionApplication](kafka-streams/src/main/java/com/pluralsight/kafka/streams/FraudDetectionApplication.java).
        - This application will keep running until you stop its process with Ctrl+C
    - Run the Main class of the transactions-producer module [com.pluralsight.kafka.streams.Main from transactions-producer](transactions-producer/src/main/java/com/pluralsight/kafka/streams/Main.java).
        - This application will exit after publishing five events on the `payments` topic, but you may run it multiple
          times to see multiples of ten events being processed by the consumer.

### The KSQL CLI example of module 6
- We assume that the first part of bringing up the containers with docker compose and the creation of the topics as
  described above has already been executed.
- On the command line or within your IDE
    - Run the Main class of the background-producer module [com.pluralsight.kafka.streams.Main from background-producer](background-producer/src/main/java/com/pluralsight/kafka/streams/Main.java).
        - This application will exit after publishing five events on the same `payments` topic, as the key and the value
          type is the same as from the events produced by the transactions-producer. 
        - If you didn't run the transactions-producer from the previous module in this session make sure to run the
          background-producer once to get the event value AVRO format Order registered with the schema registry.
- In a terminal we can now start the KSQL CLI with
    ```bash
    $ docker exec --interactive --tty ksqldb-cli /bin/ksql http://ksqldb-server:8088
    ```
  - We can check for streams, tables and topics present
    ```bash
    ksql> show topics;

    Kafka Topic                 | Partitions | Partition Replicas
    ---------------------------------------------------------------
    default_ksql_processing_log | 1          | 1
    payments                    | 1          | 1
    validated-payments          | 1          | 1
    ---------------------------------------------------------------
    ksql> show streams;
    
    Stream Name         | Kafka Topic                 | Key Format | Value Format | Windowed
    ------------------------------------------------------------------------------------------
    KSQL_PROCESSING_LOG | default_ksql_processing_log | KAFKA      | JSON         | false
    ------------------------------------------------------------------------------------------
    
    ksql> show tables;
    
    Table Name | Kafka Topic | Key Format | Value Format | Windowed
    -----------------------------------------------------------------
    -----------------------------------------------------------------
    ```
  - Create a stream from which we then create a table
    ```bash
    ksql> create stream ksql_payments
    >with ( kafka_topic='payments', value_format='AVRO' );
    
    Message
    ----------------
    Stream created
    ----------------
    ksql> show streams;

    Stream Name         | Kafka Topic                 | Format
    ------------------------------------------------------------
    KSQL_PAYMENTS       | payments                    | AVRO   
    KSQL_PROCESSING_LOG | default_ksql_processing_log | JSON
    ------------------------------------------------------------
  
    ksql> create table WARNINGS
    >as select userId, count(*)
    >from ksql_payments
    >window hopping (size 10 minutes, advance by 1 minute )
    >group by userId
    >having count(*) > 4;
    ```
  - We can run the background-producer a couple of more times and then query the table
    ```bash
    ksql> select * from WARNINGS;

    +--------------------------------------------------------+--------------------------------------------------------+--------------------------------------------------------+--------------------------------------------------------+
    |USERID                                                  |WINDOWSTART                                             |WINDOWEND                                               |KSQL_COL_0                                              |
    +--------------------------------------------------------+--------------------------------------------------------+--------------------------------------------------------+--------------------------------------------------------+
    |1234                                                    |1661348100000                                           |1661348700000                                           |5                                                       |
    |1234                                                    |1661348160000                                           |1661348760000                                           |5                                                       |
    |1234                                                    |1661348220000                                           |1661348820000                                           |5                                                       |
    |1234                                                    |1661348280000                                           |1661348880000                                           |5                                                       |
    |1234                                                    |1661348340000                                           |1661348940000                                           |5                                                       |
    |1234                                                    |1661348400000                                           |1661349000000                                           |5                                                       |
    |1234                                                    |1661348460000                                           |1661349060000                                           |17                                                      |
    |1234                                                    |1661348520000                                           |1661349120000                                           |25                                                      |
    |1234                                                    |1661348580000                                           |1661349180000                                           |25                                                      |
    |1234                                                    |1661348640000                                           |1661349240000                                           |25                                                      |
    |1234                                                    |1661348700000                                           |1661349300000                                           |20                                                      |
    |1234                                                    |1661348760000                                           |1661349360000                                           |20                                                      |
    |1234                                                    |1661348820000                                           |1661349420000                                           |20                                                      |
    |1234                                                    |1661348880000                                           |1661349480000                                           |20                                                      |
    |1234                                                    |1661348940000                                           |1661349540000                                           |20                                                      |
    |1234                                                    |1661349000000                                           |1661349600000                                           |20                                                      |
    |1234                                                    |1661349060000                                           |1661349660000                                           |8                                                       |
    Query terminated
    ksql>
    ```
  - We can also print the content of a topic as soon as new events are written into it:
    ```bash
    ksql> print 'payments';
    Press CTRL-C to interrupt

    ```
    You won´t see any messages already written to the topic, but if you run both the background-producer and the transactions-producer
    once you will see something like:
    ```bash
    Key format: JSON or KAFKA_STRING
    Value format: AVRO
    rowtime: 2022/08/24 19:29:27.398 Z, key: 1, value: {"userId": "1234", "totalAmount": 100.0, "nbOfItems": 1001}, partition: 0
    rowtime: 2022/08/24 19:29:28.467 Z, key: 2, value: {"userId": "1234", "totalAmount": 200.0, "nbOfItems": 2002}, partition: 0
    rowtime: 2022/08/24 19:29:29.469 Z, key: 3, value: {"userId": "1234", "totalAmount": 300.0, "nbOfItems": 3003}, partition: 0
    rowtime: 2022/08/24 19:29:30.471 Z, key: 4, value: {"userId": "1234", "totalAmount": 400.0, "nbOfItems": 4004}, partition: 0
    rowtime: 2022/08/24 19:29:31.472 Z, key: 5, value: {"userId": "1234", "totalAmount": 500.0, "nbOfItems": 5005}, partition: 0
    rowtime: 2022/08/24 19:30:01.523 Z, key: 1, value: {"userId": "", "totalAmount": 5.0, "nbOfItems": 5}, partition: 0
    rowtime: 2022/08/24 19:30:03.591 Z, key: 2, value: {"userId": "123", "totalAmount": 100.0, "nbOfItems": 1001}, partition: 0
    rowtime: 2022/08/24 19:30:05.592 Z, key: 2, value: {"userId": "123", "totalAmount": 100.0, "nbOfItems": 1001}, partition: 0
    rowtime: 2022/08/24 19:30:07.593 Z, key: 2, value: {"userId": "123", "totalAmount": 100.0, "nbOfItems": 1001}, partition: 0
    rowtime: 2022/08/24 19:30:09.594 Z, key: 2, value: {"userId": "123", "totalAmount": 100.0, "nbOfItems": 1001}, partition: 0
    rowtime: 2022/08/24 19:30:11.595 Z, key: 2, value: {"userId": "123", "totalAmount": 100.0, "nbOfItems": 1001}, partition: 0
    rowtime: 2022/08/24 19:30:13.596 Z, key: 3, value: {"userId": "ghi", "totalAmount": 10001.0, "nbOfItems": 1}, partition: 0
    rowtime: 2022/08/24 19:30:15.597 Z, key: 4, value: {"userId": "abc", "totalAmount": 100.0, "nbOfItems": 10}, partition: 0
    rowtime: 2022/08/24 19:30:17.598 Z, key: 5, value: {"userId": "JKL", "totalAmount": 1.0, "nbOfItems": 1}, partition: 0

    ^CTopic printing ceased
    ksql> exit
    Exiting ksqlDB.
    $
    ```
    After pressing Ctrl-C the KSQL> prompt returns and with exit you leave the KSQL CLI.

## The Schema registration process
The inner workings of schema registration are already explained in the [README.md](https://github.com/wjc-van-es/ps-eda-kafka-docker-avro-app/blob/main/README.md)
file of the GitHub repository for the previous module of the course.
