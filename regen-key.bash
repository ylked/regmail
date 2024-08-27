#!/bin/bash

FILENAME=".secret"
INDEX="APP_AUTH_TOKEN_SECRET"
KEYSIZE=64

new_secret=$(openssl rand -hex $KEYSIZE)

echo "$INDEX=$new_secret" > $FILENAME

docker compose up -d
