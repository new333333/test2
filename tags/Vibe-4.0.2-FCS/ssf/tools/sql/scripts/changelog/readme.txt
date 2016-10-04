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

The schema changes for Filr 1.2 and Vibe Hudson (4.0) releases (Note the discrepancy in the file name).
For Vibe, this is the first release where database schema management is done via Liquibase.

6. *-changelog-filraxion.xml - 

The schema changes for Filr Axion (2.0) and Vibe 4.0.1 releases.

7. *-changelog-filrproton.xml - 

The schema changes for Filr Proton (only partial up to source code split!) and Vibe 4.0.2 releases.
