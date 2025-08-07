#!/bin/sh

echo "================[bootRun]================"

# Run bootRun in the background and log output
/backend/gradlew bootRun > /tmp/bootrun.log 2>&1 &
BOOTRUN_PID=$!

# Show bootRun log output in real-time
tail -f /tmp/bootrun.log &
TAIL_PID=$!

# Wait for bootRun to finish
until grep -q "Started" /tmp/bootrun.log; do
  sleep 1
done

echo "================[classes -t]================"

# Run classes task in the background and log output
/backend/gradlew classes -t > /tmp/classes.log 2>&1 &
CLASSES_PID=$!

# Show classes log output in real-time
tail -f /tmp/classes.log &
CLASSES_TAIL_PID=$!

# Keep the script running until bootRun finishes
wait $BOOTRUN_PID