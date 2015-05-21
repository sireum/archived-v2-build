#!/bin/bash
ips=( amandroid bakar client-server core jawa parser prelude );
for i in "${ips[@]}"; do
  if [ -e ${i} ] ; then
    echo "Pulling ${i}"
    cd ${i}
    git pull --depth 1
    cd ..
  else
    echo "Cloning ${i}"
    git clone --depth 1 https://github.com/sireum/${i}.git
  fi
done
