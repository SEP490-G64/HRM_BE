#!/usr/bin/bash

docker compose --env-file .env --env-file .env.local  -f docker-compose.yml up --build --force-recreate