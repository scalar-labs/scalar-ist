CREATE KEYSPACE IF NOT EXISTS ist WITH REPLICATION ={'class': 'SimpleStrategy', 'replication_factor': '1'};

CREATE TABLE ist.consent_statement
(
    root_consent_statement_id          TEXT,
    company_id                         TEXT,
    organization_id                    TEXT,
    consent_statement_id               TEXT,

    version                            TEXT,
    status                             TEXT,
    group_company_ids                  TEXT,
    title                              TEXT,
    abstract                           TEXT,
    changes                            TEXT,
    purpose_ids                        TEXT,
    data_set_schema_ids                TEXT,
    benefit_ids                        TEXT,
    third_party_ids                    TEXT,
    optional_third_parties             TEXT,
    data_retention_policy_id           TEXT,
    consent_statement                  TEXT,
    optional_purposes                  TEXT,
    parent_consent_statement_id        TEXT,
    created_by                         TEXT,
    created_at                         BIGINT,
    updated_at                         BIGINT,

    before_version                     TEXT,
    before_status                      TEXT,
    before_group_company_ids           TEXT,
    before_title                       TEXT,
    before_abstract                    TEXT,
    before_changes                     TEXT,
    before_purpose_ids                 TEXT,
    before_data_set_schema_ids         TEXT,
    before_benefit_ids                 TEXT,
    before_third_party_ids             TEXT,
    before_optional_third_parties      TEXT,
    before_data_retention_policy_id    TEXT,
    before_consent_statement           TEXT,
    before_optional_purposes           TEXT,
    before_parent_consent_statement_id TEXT,
    before_created_by                  TEXT,
    before_created_at                  BIGINT,
    before_updated_at                  BIGINT,

    tx_id                              TEXT,
    tx_prepared_at                     BIGINT,
    tx_committed_at                    BIGINT,
    tx_state                           INT,
    tx_version                         INT,

    before_tx_id                       TEXT,
    before_tx_prepared_at              BIGINT,
    before_tx_committed_at             BIGINT,
    before_tx_state                    INT,
    before_tx_version                  INT,

    PRIMARY KEY (root_consent_statement_id, company_id, organization_id, consent_statement_id, version),
) WITH CLUSTERING ORDER BY (company_id ASC, organization_id ASC, consent_statement_id ASC, version ASC);
CREATE INDEX consent_statement_id_for_consent_statement_table_idx ON ist.consent_statement (consent_statement_id);
CREATE INDEX company_id_idx ON ist.consent_statement (company_id);
CREATE INDEX organization_id_idx ON ist.consent_statement (organization_id);
CREATE INDEX group_company_ids_idx ON ist.consent_statement (group_company_ids);
CREATE INDEX parent_statement_id_idx ON ist.consent_statement (parent_consent_statement_id);

CREATE TABLE ist.purpose
(
    company_id                 TEXT,
    created_at                 BIGINT,
    organization_id            TEXT,

    category_of_purpose        TEXT,
    purpose_name               TEXT,
    description                TEXT,
    legal_text                 TEXT,
    user_friendly_text         TEXT,
    guidance                   TEXT,
    note                       TEXT,
    is_active                  BOOLEAN,
    created_by                 TEXT,
    updated_at                 BIGINT,

    before_category_of_purpose TEXT,
    before_purpose_name        TEXT,
    before_description         TEXT,
    before_legal_text          TEXT,
    before_user_friendly_text  TEXT,
    before_guidance            TEXT,
    before_note                TEXT,
    before_is_active           BOOLEAN,
    before_created_by          TEXT,
    before_updated_at          BIGINT,

    tx_id                      TEXT,
    tx_prepared_at             BIGINT,
    tx_committed_at            BIGINT,
    tx_state                   INT,
    tx_version                 INT,

    before_tx_id               TEXT,
    before_tx_prepared_at      BIGINT,
    before_tx_committed_at     BIGINT,
    before_tx_state            INT,
    before_tx_version          INT,

    PRIMARY KEY (company_id, created_at, organization_id),
) WITH CLUSTERING ORDER BY (created_at DESC, organization_id ASC);
CREATE INDEX purpose_organization_id_idx ON ist.purpose (organization_id);
CREATE INDEX category_of_purpose_idx ON ist.purpose (category_of_purpose);

CREATE TABLE ist.data_set_schema
(
    company_id              TEXT,
    created_at              BIGINT,
    organization_id         TEXT,

    data_set_name           TEXT,
    description             TEXT,
    data_location           TEXT,
    category_of_data        TEXT,
    data_type               TEXT,
    classification          TEXT,
    data_set_schema         TEXT,
    is_active               BOOLEAN,
    created_by              TEXT,
    updated_at              BIGINT,

    before_data_set_name    TEXT,
    before_description      TEXT,
    before_data_location    TEXT,
    before_category_of_data TEXT,
    before_data_type        TEXT,
    before_classification   TEXT,
    before_data_set_schema  TEXT,
    before_is_active        BOOLEAN,
    before_created_by       TEXT,
    before_updated_at       BIGINT,

    tx_id                   TEXT,
    tx_prepared_at          BIGINT,
    tx_committed_at         BIGINT,
    tx_state                INT,
    tx_version              INT,

    before_tx_id            TEXT,
    before_tx_prepared_at   BIGINT,
    before_tx_committed_at  BIGINT,
    before_tx_state         INT,
    before_tx_version       INT,

    PRIMARY KEY (company_id, created_at, organization_id),
) WITH CLUSTERING ORDER BY (created_at DESC, organization_id ASC);
CREATE INDEX data_set_schema_organization_id_idx ON ist.data_set_schema (organization_id);

CREATE TABLE ist.data_retention_policy
(
    company_id                 TEXT,
    created_at                 BIGINT,
    policy_type                TEXT,
    organization_id            TEXT,

    policy_name                TEXT,
    length_of_use              TEXT,
    length_of_retention        TEXT,
    description                TEXT,
    is_active                  BOOLEAN,
    created_by                 TEXT,
    updated_at                 BIGINT,

    before_policy_name         TEXT,
    before_length_of_use       TEXT,
    before_length_of_retention TEXT,
    before_description         TEXT,
    before_is_active           BOOLEAN,
    before_created_by          TEXT,
    before_updated_at          BIGINT,

    tx_id                      TEXT,
    tx_prepared_at             BIGINT,
    tx_committed_at            BIGINT,
    tx_state                   INT,
    tx_version                 INT,

    before_tx_id               TEXT,
    before_tx_prepared_at      BIGINT,
    before_tx_committed_at     BIGINT,
    before_tx_state            INT,
    before_tx_version          INT,

    PRIMARY KEY (company_id, created_at, policy_type, organization_id),
) WITH CLUSTERING ORDER BY (created_at DESC, policy_type ASC, organization_id ASC);
CREATE INDEX data_retention_policy_organization_id_idx ON ist.data_retention_policy (organization_id);
CREATE INDEX created_by_idx ON ist.data_retention_policy (created_by);

CREATE TABLE ist.third_party
(
    company_id                  TEXT,
    created_at                  BIGINT,
    third_party_domain          TEXT,

    third_party_name            TEXT,
    corporate_number            TEXT,
    third_party_metadata        TEXT,
    organizations               TEXT,
    is_active                   BOOLEAN,
    created_by                  TEXT,
    updated_at                  BIGINT,

    before_third_party_name     TEXT,
    before_corporate_number     TEXT,
    before_third_party_metadata TEXT,
    before_organizations        TEXT,
    before_is_active            BOOLEAN,
    before_created_by           TEXT,
    before_updated_at           BIGINT,

    tx_id                       TEXT,
    tx_prepared_at              BIGINT,
    tx_committed_at             BIGINT,
    tx_state                    INT,
    tx_version                  INT,

    before_tx_id                TEXT,
    before_tx_prepared_at       BIGINT,
    before_tx_committed_at      BIGINT,
    before_tx_state             INT,
    before_tx_version           INT,

    PRIMARY KEY (company_id, created_at, third_party_domain),
) WITH CLUSTERING ORDER BY (created_at DESC, third_party_domain ASC);

CREATE TABLE ist.company
(
    company_id              TEXT,
    created_at              BIGINT,

    company_name            TEXT,
    corporate_number        TEXT,
    company_metadata        TEXT,
    is_active               BOOLEAN,
    created_by              TEXT,
    updated_at              BIGINT,

    before_company_name     TEXT,
    before_corporate_number TEXT,
    before_company_metadata TEXT,
    before_is_active        BOOLEAN,
    before_created_by       TEXT,
    before_updated_at       BIGINT,

    tx_id                   TEXT,
    tx_prepared_at          BIGINT,
    tx_committed_at         BIGINT,
    tx_state                INT,
    tx_version              INT,

    before_tx_id            TEXT,
    before_tx_prepared_at   BIGINT,
    before_tx_committed_at  BIGINT,
    before_tx_state         INT,
    before_tx_version       INT,

    PRIMARY KEY (company_id, created_at),
) WITH CLUSTERING ORDER BY (created_at DESC);

CREATE TABLE ist.organization
(
    company_id                   TEXT,
    organization_id              TEXT,
    created_at                   BIGINT,

    organization_metadata        TEXT,
    is_active                    BOOLEAN,
    created_by                   TEXT,
    updated_at                   BIGINT,

    before_organization_metadata TEXT,
    before_is_active             BOOLEAN,
    before_created_by            TEXT,
    before_updated_at            BIGINT,

    tx_id                        TEXT,
    tx_prepared_at               BIGINT,
    tx_committed_at              BIGINT,
    tx_state                     INT,
    tx_version                   INT,

    before_tx_id                 TEXT,
    before_tx_prepared_at        BIGINT,
    before_tx_committed_at       BIGINT,
    before_tx_state              INT,
    before_tx_version            INT,

    PRIMARY KEY (company_id, organization_id, created_at),
) WITH CLUSTERING ORDER BY (organization_id ASC, created_at DESC);
CREATE INDEX organization_organization_id_idx ON ist.organization (organization_id);

CREATE TABLE ist.user_profile
(
    company_id              TEXT,
    holder_id               TEXT,

    organization_ids        TEXT,
    roles                   TEXT,
    created_by              TEXT,
    created_at              BIGINT,
    updated_at              BIGINT,

    before_organization_ids TEXT,
    before_roles            TEXT,
    before_created_by       TEXT,
    before_created_at       BIGINT,
    before_updated_at       BIGINT,

    tx_id                   TEXT,
    tx_prepared_at          BIGINT,
    tx_committed_at         BIGINT,
    tx_state                INT,
    tx_version              INT,

    before_tx_id            TEXT,
    before_tx_prepared_at   BIGINT,
    before_tx_committed_at  BIGINT,
    before_tx_state         INT,
    before_tx_version       INT,

    PRIMARY KEY (company_id, holder_id),
) WITH CLUSTERING ORDER BY (holder_id ASC);
CREATE INDEX holder_id_idx ON ist.user_profile (holder_id);

CREATE TABLE ist.consent
(
    data_subject_id         TEXT,
    consent_statement_id    TEXT,

    consent_status          TEXT,
    consent_id              TEXT,
    consented_detail        TEXT,
    rejected_detail         TEXT,
    created_at              BIGINT,
    updated_at              BIGINT,

    before_consent_status   TEXT,
    before_consent_id       TEXT,
    before_consented_detail TEXT,
    before_rejected_detail  TEXT,
    before_created_at       BIGINT,
    before_updated_at       BIGINT,

    tx_id                   TEXT,
    tx_prepared_at          BIGINT,
    tx_committed_at         BIGINT,
    tx_state                INT,
    tx_version              INT,

    before_tx_id            TEXT,
    before_tx_prepared_at   BIGINT,
    before_tx_committed_at  BIGINT,
    before_tx_state         INT,
    before_tx_version       INT,

    PRIMARY KEY (data_subject_id, consent_statement_id),
) WITH CLUSTERING ORDER BY (consent_statement_id ASC);
CREATE INDEX consent_statement_id_for_consent_table_idx ON ist.consent (consent_statement_id);
CREATE INDEX consent_id_idx ON ist.consent (consent_id);


CREATE TABLE ist.benefit
(
    company_id                 TEXT,
    created_at                 BIGINT,
    organization_id            TEXT,

    category_of_benefit        TEXT,
    benefit_name               TEXT,
    description                TEXT,
    provider                   TEXT,
    time_of_provision          TEXT,
    is_active                  BOOLEAN,
    created_by                 TEXT,
    updated_at                 BIGINT,

    before_category_of_benefit TEXT,
    before_benefit_name        TEXT,
    before_description         TEXT,
    before_provider            TEXT,
    before_time_of_provision   TEXT,
    before_is_active           BOOLEAN,
    before_created_by          TEXT,
    before_updated_at          BIGINT,

    tx_id                      TEXT,
    tx_prepared_at             BIGINT,
    tx_committed_at            BIGINT,
    tx_state                   INT,
    tx_version                 INT,

    before_tx_id               TEXT,
    before_tx_prepared_at      BIGINT,
    before_tx_committed_at     BIGINT,
    before_tx_state            INT,
    before_tx_version          INT,

    PRIMARY KEY (company_id, created_at, organization_id),
);

DROP KEYSPACE IF EXISTS coordinator;
CREATE KEYSPACE coordinator WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};

CREATE TABLE coordinator.state
(
    tx_id         TEXT PRIMARY KEY,
    tx_created_at BIGINT,
    tx_state      INT
);
