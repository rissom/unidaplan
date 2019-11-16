
TABLE users {
  ID  int [pk]
  fullname          VARCHAR(200)
  username          VARCHAR(50)
  email             VARCHAR(200)
  blocked           boolean
  preferredlanguage VARCHAR(5)
  pw_hash           VARCHAR(134)
  token             VARCHAR(100)
  token_valid_to    TIMESTAMP
  lastChange        TIMESTAMP
}

TABLE language_preferences {
  ID              INTEGER [pk]
  user_id         INTEGER [ref: > users.ID]
  language        VARCHAR(5)
  rank            INTEGER
  lastChange      TIMESTAMP
  lastUser        INTEGER
}

TABLE string_key_table {
  ID              INTEGER [pk]
  description     VARCHAR(100)  // Beschreibungsfeld (nur für Dokumentationszwecke)
  lastChange      TIMESTAMP
  lastUser        INTEGER [ref: > users.ID]
}

TABLE stringtable {
  ID              INTEGER [pk]
  string_key      INTEGER [ref: > string_key_table.ID]
  language        VARCHAR(5)
  value           VARCHAR(4000)
  lastChange      TIMESTAMP
  lastUser        INTEGER [ref: > users.ID]
}