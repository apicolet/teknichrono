#!/bin/sh
DIR="$( cd "$( dirname "$0" )" && pwd )"

set -e 

python3 $DIR/../python/test_clean.py
python3 $DIR/../python/create_beacons.py
python3 $DIR/../python/test_live.py