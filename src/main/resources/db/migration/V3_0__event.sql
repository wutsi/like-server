CREATE TABLE T_EVENT(
    id              SERIAL NOT NULL PRIMARY KEY,
    type            INTEGER,
    timestamp       TIMESTAMPTZ NOT NULL,

    canonical_url   TEXT NOT NULL,
    url_hash        VARCHAR(64) NOT NULL,
    device_uuid     VARCHAR(36),
    user_id         BIGINT
);

CREATE INDEX I_event_url ON T_EVENT(url_hash);
CREATE INDEX I_event_user_device_url ON T_EVENT(user_id, device_uuid, url_hash);

DROP TABLE T_LIKE;
