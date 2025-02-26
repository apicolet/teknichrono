FROM python:3.14-rc-alpine3.21

COPY src/test/scripts /app
COPY requirements.txt /app/requirements.txt
WORKDIR /app
RUN pip install -r /app/requirements.txt
CMD ["sh", "/app/bash/all_tests.sh"]
