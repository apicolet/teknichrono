#!/bin/sh
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

python3 $DIR/../python/test.clean.py
python3 $DIR/../python/test.moto.loop.tt.1.py