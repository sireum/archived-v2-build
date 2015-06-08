#!/bin/bash
ips=( amandroid bakar client-server core jawa parser prelude );
for i in "${ips[@]}"; do
  echo "Cloning ${i}"
  git clone --depth 1 https://github.com/sireum/${i}.git
  cd ${i}
  git --no-pager log -1
  cd ..
  echo ""
done
