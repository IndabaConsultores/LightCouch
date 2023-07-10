# 0.3.2 (UNPUBLISHED)
- [MAINTENANCE] Tested using CouchDB 3.3.2
- [MAINTENANCE] Bump Apache HTTPCLient to version to 5.2.1
- [MAINTENANCE] Remove the use of deprecated methods from HTTPClient
- [MAINTENANCE] Remove implicit dependency on Apache Commons Codec
- [NEW] Support for setting Connection TTL in the HTTP connection pool

# 0.3.1 (23/08/2022)
- [MAINTENANCE] Bump Gson version to 2.8.9
- [MAINTENANCE] Tested using CouchDB 3.2.2
- [BREAK] Moved to java 8
- [NEW] Added removeAttachment operation

# 0.3.0 (29/10/2020)
- [MAINTENANCE] Upgrade to Apache HttpClient 5.0
- [MAINTENANCE] Upgrade dependencies 

# 0.2.6 (18/02/2019)
- [NEW] Added support for /db/_purge endpoint implemented in CouchDB 2.3.0

# 0.2.5 (23/12/2018)
- [FIX] Support for string purge_seqs in CouchDB 2.3

# 0.2.4 (08/10/2018)
- [NEW] Added API for checking design doc existence

# 0.2.3 (18/09/2018)
- [NEW] Added API for specifying docIds filter to _changes operation

# 0.2.2 (21/03/2018)
- [NEW] Added explicit API for local document management.
- [NEW] Added seq_interval parameter in Changes API
- [NEW] Added _db_updates endpoint support
- [IMPROVED] Make more robust stop process in Changes hasNext

# 0.2.1 (21/02/2018)
- [NEW] Added API for specifying a mango selector _changes operation
- [IMPROVED] Test are cleaned up and executed in CouchDB 1.x an CouchDb 2.x
- [SUPPORT] Added support for travis build
