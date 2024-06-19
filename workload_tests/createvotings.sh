#!/bin/bash

TASKS=3 locust -f workload.py -u 50 -r 10 --host=http://localhost:8080 --headless