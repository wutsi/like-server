INSERT INTO T_LIKE(canonical_url, url_hash, like_date_time, user_id, device_uuid) VALUES
    ('https://www.google.com', 'ac6bb669e40e44a8d9f8f0c94dfc63734049dcf6219aac77f02edf94b9162c09', now(), 1, null);

INSERT INTO T_LIKE(canonical_url, url_hash, like_date_time, user_id, device_uuid) VALUES
    ('https://www.google.com', 'ac6bb669e40e44a8d9f8f0c94dfc63734049dcf6219aac77f02edf94b9162c09', now(), null, gen_random_uuid());

INSERT INTO T_LIKE(canonical_url, url_hash, like_date_time, user_id, device_uuid) VALUES
    ('https://www.google.com', 'ac6bb669e40e44a8d9f8f0c94dfc63734049dcf6219aac77f02edf94b9162c09', now(), 11, gen_random_uuid());

INSERT INTO T_LIKE(canonical_url, url_hash, like_date_time, user_id, device_uuid) VALUES
    ('https://www.theserverside.com', 'theserverside.com', now(), 11, gen_random_uuid());

INSERT INTO T_LIKE(canonical_url, url_hash, like_date_time, user_id, device_uuid) VALUES
    ('https://www.google.com', 'ac6bb669e40e44a8d9f8f0c94dfc63734049dcf6219aac77f02edf94b9162c09', now(), 120923, gen_random_uuid());

