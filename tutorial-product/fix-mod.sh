#! /bin/bash

readonly SELF=$(cd $(dirname $0) && pwd) 
readonly productPath="${SELF}/target/products"
readonly productZip=name.wadewalker.tutorial.product-win32.win32.x86_64.zip
readonly installDir="${productPath}/Tutorial"

unzip -q "${productPath}/${productZip}" -d "${installDir}"

# Make things executable that Tycho goofs up
chmod a+x "${installDir}/Tutorial.exe"
find "${installDir}/plugins" -print0 -name '*.dll' | xargs --null chmod a+x
 # chmod a+x $(find "${installDir}/plugins" -name '*.dll')

