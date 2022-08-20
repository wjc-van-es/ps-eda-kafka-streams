#!/usr/bin/env bash

###############################################################################################
# This idempotent script creates the necessary topic when absent.
# It can be run after the command
# docker compose up -d
###############################################################################################
declare SOURCE_TOPIC="payments"
# declare -i SOURCE_TOPIC_ABSENT=1 # 1 meaning truly absent and 0 meaning actually present
declare SOURCE_TOPIC_ABSENT=true
declare SINK_TOPIC="validated-payments"
declare SINK_TOPIC_ABSENT=true
declare -a TOPICS
declare -i INDEX=0

echo "Trying to create the topic $SOURCE_TOPIC if not yet present."

# Checking the presence of SOURCE_TOPIC
for line in $(docker exec broker kafka-topics --bootstrap-server broker:9092 --list); do
   # echo $line
   # [[ "$line" == *"$KIA_TEST_TOPIC"* ]] && echo "Line contains the KIA_TEST_TOPIC"
    if [[ "$line" == "$SOURCE_TOPIC" ]]; then
        SOURCE_TOPIC_ABSENT=false;
    fi
    if [[ "$line" == "$SINK_TOPIC" ]]; then
            SINK_TOPIC_ABSENT=false;
    fi
    TOPICS[${INDEX}]=$line;
    (( INDEX++ )) || true;
done

echo "List of all topics already present: ${TOPICS[*]}"

# Creating SOURCE_TOPIC if absent
if [ $SOURCE_TOPIC_ABSENT == true ]; then
    docker exec broker kafka-topics --bootstrap-server broker:9092 --create --topic $SOURCE_TOPIC
fi
if [ $SINK_TOPIC_ABSENT == true ]; then
    docker exec broker kafka-topics --bootstrap-server broker:9092 --create --topic $SINK_TOPIC
fi

echo "current list of topics:"
docker exec broker kafka-topics --bootstrap-server broker:9092 --list

echo "Description of the $SOURCE_TOPIC topic:"
docker exec broker kafka-topics --bootstrap-server broker:9092 --describe --topic $SOURCE_TOPIC

echo "Description of the $SINK_TOPIC topic:"
docker exec broker kafka-topics --bootstrap-server broker:9092 --describe --topic $SINK_TOPIC