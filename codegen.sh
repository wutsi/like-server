java -jar ../wutsi-codegen/target/wutsi-codegen-0.0.9.jar server \
    -a like -p com.wutsi.like \
    -g wutsi \
    -i https://wutsi-openapi.s3.amazonaws.com/like_api.yaml \
    -o .
