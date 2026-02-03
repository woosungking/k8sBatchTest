FROM ubuntu:latest
LABEL authors="gim-useong"

ENTRYPOINT ["top", "-b"]