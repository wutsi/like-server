CREATE TABLE T_LIKE(
    id              SERIAL NOT NULL PRIMARY KEY,
    canonical_url   TEXT NOT NULL,
    url_hash        VARCHAR(64) NOT NULL,
    device_uuid     VARCHAR(36),
    user_id         BIGINT,
    like_date_time  TIMESTAMP
);

CREATE INDEX I_url ON T_LIKE(url_hash);
CREATE INDEX I_user_device_url ON T_LIKE(user_id, device_id, url_hash);
