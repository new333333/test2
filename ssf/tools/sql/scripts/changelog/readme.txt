1. *-changelog-master.xml - 

Master file listing all others.

2. *-changelog-3.3.xml - 

One time changelog obtained by reverse engineering existing Vibe 3.3/3.4 schema
(there is no schema difference between 3.3 and 3.4).
This represents a cutover point transitioning from old SQL-based schema
management to new Liquibase-based one.
The Vibe 3.4 is the last release where the database schema management was done
via plain SQL scripts.

3. *-changelog-4.0.xml - 

The schema changes for Filr 1.0 release (Note the discrepancy in the file name)

4. *-changelog-vibe4.0-filr1.1.xml - 

The schema changes for Filr 1.1 release (Note the discrepancy in the file name)

5. *-changelog-filr1.1.1.xml - 

The schema changes for Filr 1.2 and Vibe Hudson releases (Note the discrepancy in the file name)
