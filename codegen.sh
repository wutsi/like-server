java -jar ../wutsi-codegen/target/wutsi-codegen-0.0.17.jar server \
    -j 11 \
    -a like -p com.wutsi.like \
    -g wutsi \
    -i https://wutsi-openapi.s3.amazonaws.com/like_api.yaml \
    -o . \
    -heroku wutsi-test-like-service \
    -service_cache \
    -service_logger \
    -service_database

mvn antrun:run@ktlint-format
mvn antrun:run@ktlint-format
