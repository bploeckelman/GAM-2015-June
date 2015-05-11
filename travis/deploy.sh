#!/bin/bash

mkdir deploy
cp -r html/build/dist/* deploy/
cp desktop/build/libs/desktop-*.jar deploy/LudumDare32.jar
cd deploy
tar -czf ../LudumDare32.tar.gz ./
cd ..
curl -F filedata=@LudumDare32.tar.gz -H "$MOSHEN_UPLOAD_KEY" http://moshen.net/LudumDare32/deploy/
