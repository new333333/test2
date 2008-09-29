IMPORTANT NOTE:  BEFORE INSTALLING INTO AN EXISTING LIFERAY INSTALLATION ... BACKUP, BACKUP, BACKUP YOUR DATA

General:

    * Tomcat: enable the Liferay "extension" environment by adding ${CATALINA_HOME}/common/lib/ext/* to the common.loader path in ${CATALINA_HOME}/conf/catalina.properties
    * Need java web plugin (distributed separately from JDK) to build
    * MySQL must be installed

                    URL

        jdbc:mysql://localhost:3306/sitescape?useUnicode=true&amp;characterEncoding=UTF-8
            user
                sitescape
            password
                sitescape

Liferay 4.3.6 or 5.0RC1

    * install Liferay Tomcat 5.5 bundle with hsql DB
    * install ssf sources
    * add app.server.{username}.properties file to ssf installation directory and add an entry similar to app.server.liferay.tomcat.dir=${user.home}/bin/liferay-portal-tomcat-5.5-5.0.1  (change {username}  in app.server.{username}.properties to your system's username).  You do NOT have to change ${user.home} in the variable as this will be known at runtime.
    * ensure that the Java Web Plugin is installed (may be distributed separately).
    * ensure that ant is installed on the system and in the path
    * from a command line execute "ant build-and-full-deploy" from ssf installation location
    * install MySQL database and create "lportal" and "sitescape" databases (no need to create schema, Hibernate will do this for you).
          o Login to MySQL and issue command: CREATE database {db_name} ;
    * create ssf.xml and ssfs.xml files in $LIFERAY_HOME/conf/Catalina/localhost/.  Make sure ssf.xml has correct DB settings  (example files are attached).
    * modify ROOT.xml settings for MySQL DB co-located with ssf.xml and ssfs.xml from above.
    * on startup Hibernate will create the sitescape and liferay DB tables, however Quartz will fail so we must run the attached SQL script
    * ensure that portal-ext.properties exists in $LIFERAY_HOME/webapps/ROOT/WEB_INF/classes or other CLASSPATH accessible area.
    * added to portal-ext.properties to support screenName login and authentication pipeline changes  (THESE ARE REQUIRED.  ICEcore currently only supports screenName authentication). 

        company.security.auto.login=true
        company.security.send.password=true
        company.security.auth.type=screenName

        login.events.post=com.sitescape.team.liferay.events.LoginPostAction,com.liferay.portal.events.LoginPostAction,com.liferay.portal.events.Default
LandingPageAction 

    * login as test/test and add "admin" account (Must be "admin").  This account is currently required by ICEcore.
