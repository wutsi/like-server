java -jar ../wutsi-codegen/target/wutsi-codegen-0.0.20.jar server \
    -j 11 \
    -a like -p com.wutsi.like \
    -g wutsi \
    -i https://wutsi-openapi.s3.amazonaws.com/like_api.yaml \
    -o . \
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
