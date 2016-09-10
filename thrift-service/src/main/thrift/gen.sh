#!/usr/bin/env bash

thrift -gen java -out ../java/ $1.thrift
thrift -gen js -out ../webapp/js/service/ $1.thrift