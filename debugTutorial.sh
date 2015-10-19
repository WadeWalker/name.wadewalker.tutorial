#! /bin/bash
readonly PORT=${1:-8000}
echo Waiting to debug on port ${PORT}
tutorial-product/target/products/Tutorial/Tutorial.exe -console -vmArgs -Xdebug "-Xrunjdwp:server=y,transport=dt_socket,address=${PORT}" &

