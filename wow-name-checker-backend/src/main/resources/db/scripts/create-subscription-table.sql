CREATE TABLE subscription
(
    id     binary(16) PRIMARY KEY,
    email  text NOT NULL,
    name   text NOT NULL,
    region text NOT NULL,
    realm  text NOT NULL
);