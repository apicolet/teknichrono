#!/bin/sh
DIR="$( cd "$( dirname "$0" )" && pwd )"

set -e 

python3 $DIR/../python/test_clean.py
python3 $DIR/../python/test_moto_loop_race_1.py