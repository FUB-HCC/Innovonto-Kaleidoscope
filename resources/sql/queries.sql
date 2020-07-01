-- loosely based on: https://stackoverflow.com/questions/11337324/how-to-efficient-insert-and-fetch-uuid-in-core-data/11337522#11337522
-- https://github.com/layerware/hugsql/blob/master/examples/princess-bride/src/princess_bride/db/sql/characters.sql
-- :name create-session-table! :!
create table sessions (
    id TEXT,
    sparql_endpoint TEXT,
    created_at timestamp not null default current_timestamp
)

-- :name drop-session-table! :!
-- :doc drop session table if exists
drop table if exists sessions

-- :name create-session! :! :n
-- :doc creates a new session record
INSERT INTO sessions
(id,sparql_endpoint)
VALUES (:id, :sparql-endpoint)


-- :name get-all-sessions :? :*
-- :doc retrieve all sessions.
SELECT * FROM sessions

-- :name get-session-by-id :? :1
-- :doc retrieve a session given the id.
SELECT * FROM sessions
WHERE id = :id

-- :name get-session-by-endpoint :? :1
-- :doc retrieve a session given the sparql endpoint.
SELECT * FROM sessions
WHERE sparql_endpoint = :sparql-endpoint
