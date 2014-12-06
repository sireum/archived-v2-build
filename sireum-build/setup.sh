#!/bin/bash
ips=( amandroid bakar client-server core jawa parser prelude );
for i in "${ips[@]}"; do
  if [ -e codebase/${i} ] ; then
    echo "Pulling ${i}"
    cd codebase/${i}
    git pull
    cd ../..
  else
    echo "Cloning ${i}"
    git clone https://github.com/sireum/${i}.git codebase/${i}
  fi
done