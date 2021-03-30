java -jar ../wutsi-codegen/target/wutsi-codegen-0.0.21.jar server \
    -in https://wutsi-openapi.s3.amazonaws.com/like_api.yaml \
    -out . \
    -name like \
    -package com.wutsi.like \
    -jdk 11 \
    -github_user wutsi \
    -github_project like-server \
    -heroku wutsi-like-service \
    -service_cache \
    -service_logger \
    -service_database \
    -service_mqueue

if [ $? -eq 0 ]
then
    echo Code Cleanup...
    mvn antrun:run@ktlint-format
    mvn antrun:run@ktlint-format

else
    echo "FAILED"
    exit -1
fi
