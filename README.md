# Automatic Correlation (ACor) information system

<p align="center" width="20%">
  <img src="/images/ivars_logo.jpg" alt="ACor logo" width="20%">
</p>

## Setting up the Back-end

### Prerequisites
- Java 17 or above installed on the host system
  - [Download Java](https://jdk.java.net/)
- Gradle 7.x installed on the host system
  - [Download Gradle](https://gradle.org/releases/)
- MySQL 8.0.31 or MariaDB installed and running
  - [MySQL Downloads](https://dev.mysql.com/downloads/mysql/)
  - [MariaDB Downloads](https://mariadb.org/download/)
- Redis 2.6.1, JDBC 2.6.1, or Hazelcast 2.6.1 set up as per your configuration needs
  - Redis: [Redis Downloads](https://redis.io/download)
  - JDBC: Included in Spring Session JDBC dependencies
  - Hazelcast: [Hazelcast Downloads](https://hazelcast.com/download/)
- `zip` command installed on the host system
  - Typically available on Unix-like systems; for Windows, consider tools like 7-Zip or built-in PowerShell commands.
- Python2
  - requests
  - numpy
  - ply
  - gnuplots
- Python3
  - bs4
- AIPS
  - [Download AIPS] (http://www.aips.nrao.edu/index.shtml)
- ParselToungue
  - [Download AIPS] (https://github.com/jive-vlbi/ParselTongue)
- Perl
- SFXC
  - [Download SFXC] (https://github.com/aardk/sfxc)
- j2ms2 and tConvert
  - [Download j2ms2 and tConvert] (https://code.jive.eu/verkout/jive-casa?lang=cs-CZ)
- MPI

### Libraries and Versions
- Guava: 32.1.2-jre
- ICU4J: 73.2
- Ktor: 2.3.5
- Spring Boot (various starters including Actuator, Data JDBC, Data JPA, Data REST, Integration, Security, Web, Web Services, WebSocket): 2.6.2
- Spring Integration (various modules including HTTP, JDBC, JPA, Mail, Security, Stomp, WebSocket, WS): Check versions in `build.gradle`
- Gson: 2.8.9
- Pact Gradle Plugin: 4.3.2
- Json-Gson: 1.0
- jsonBuilder: 0.2.0
- Lombok: Check version in `build.gradle`
- MySQL Connector/J: 8.0.31
- Hibernate Validator: 7.0.1.Final
- Validation API: 2.0.1.Final
- json-simple: 1.1.1
- org.json: 20220320
- Google Collections: snapshot-20080530
- Google APIs (Calendar): v3-rev411-1.25.0
- Spring Boot OAuth2 Client: Check version in your `build.gradle`
- Spring Security OAuth2 Autoconfigure: 2.2.6.RELEASE
- Google Auth Library OAuth2 HTTP: 0.1.0
- WebJars (various libraries including Bootstrap, jQuery, SockJS, Stomp-WebSocket): Check versions in your `build.gradle`
- Apache POI (POI, POI-OOXML): 5.2.3
- JXLS JExcel: 1.0.9
- FastExcel Reader: 0.15.7
- FastExcel: 0.15.7

### API environment variables

> [!IMPORTANT]
> The *"Is required?"* column value set to ❌ indicates the default value,
otherwise, the second field contains the required value description.

> [!CAUTION]
> Changing hosts/ports and other non required variables is not fully supported yet,
because these values might be hardcoded in some locations over the project.

#### Global environment variables:

| Variable                 | Description / Default value | Is required? |
| ------------------------ | --------------------------- | ------------ |
| `DB_PASSWORD`            | -                           | ✅           |
| `DB_USER_NAME`           | -                           | ✅           |
| `GOOGLE_CLIENT_ID`       | -                           | ✅           |
| `GOOGLE_CLIENT_SECRET`   | -                           | ✅           |
| `MAIL_HOST`              | smtp.gmail.com              | ❌           |
| `MAIL_PORT`              | 587                         | ❌           |
| `MAIL_USER_NAME`         | -                           | ✅           |
| `MAIL_USER_PASSWORD`     | -                           | ✅           |
| `SERVER_IP`              | localhost                   | ❌           |
| `SERVER_PORT`            | 8080                        | ❌           |
| `CLIENT_IP`              | localhost                   | ❌           |
| `CLIENT_PORT`            | 4200                        | ❌           |
| `WEB_MYSQL_USER`         | -                           | ✅           |
| `WEB_MYSQL_PASSWORD`     | -                           | ✅           |
| `WEB_MYSQL_HOST`         | localhost                   | ❌           |
| `WEB_MYSQL_PORT`         | 3306                        | ❌           |
| `WEB_MYSQL_DB`           | vlbi                        | ❌           |
| `SCOPES`                 | -                           | ✅           |
| `CALENDAR_ID`            | -                           | ✅           |
| `TOKEN_PATH`             | -                           | ✅           |
| `CREDS_PATH`             | -                           | ✅           |
| `SSL_KEY`                | -                           | ✅           |
| `SSL_PASSWORD`           | -                           | ✅           |
| `SSL_ALIAS`              | -                           | ✅           |
| `flexbuff2_ip`           | -                           | ✅           |                               
| `flexbuff1_user`         | -                           | ✅           |    
| `flexbuff1_user2`        | -                           | ✅           | 
| `dirrectory`             | path to sfxc output         | ✅           |
| `dirrectory2`            | path to specific correlator output         | ✅           |
| `data_directory`         | -                           | ✅           |
| `update_measure_data`    | path to update_measure_data | ✅           |
| `j2ms2`                  | path to j2ms2               | ✅           |
| `tConvert`               | path to tConvert            | ✅           |
| `python_dir_2`           | -                           | ✅           |
| `produce_html_plotpage`  | path to produce_html_plotpage  | ✅           |
| `create_fits_files`      | path to create_fits_files      | ✅           |
| `create_fits_files_multi`| path to create_fits_files_multi | ✅           |
| `create_fits_single`     | path to create_fits_single      | ✅           |
| `standardplots`          | path to standardplots           | ✅           |
| `eop_user_name`          | -                           | ✅           |
| `eop_password`           | -                           | ✅           |
| `hpc_ip`                 | IP adress where SFXC is installed          | ✅           |
| `PYSCHED_SCRIPT_PATH`    | path to pysched remote script          | ✅           |
| `API_SCRIPT_PATH`        | path to script for antennas usage api           | ✅           |
| `GOOGLE_SCRIPT_PATH`     | path to script for antannas usage from google calendar          | ✅           |
| `GOOGLE_EVENT_SCRIPT_PATH`| path to script for new google event creation          | ✅           |
| `GOOGLE_COLOR_SCRIPT_PATH`| path to script for returning observation status code          | ✅           |


### Additional shell scripts

| Script                                        | Description                 | Prerequisites        | Input parameters  |Usage example   |
| --------------------------------------------- | --------------------------- | ---------------------|-------------------|----------------|
| /scripts/createResults.sh                     |                             |                      |                   |                |
| /scripts/docorrelation.sh                     |                             |                      |                   |                |
| /scripts/get_gps_for_ib_and_ir.sh             |                             |                      |                   |                |
| /scripts/make_dir.sh                          |                             |                      |                   |                |
| /scripts/send_data_files_from_flexbuff1.sh    |                             |                      |                   |                |
| /scripts/send_data_files_from_flexbuff2.sh    |                             |                      |                   |                | 
| /call_google_calendar_api.py (internal)       | Get all Google Calendar observations in the specific date   | requests package	            |           |
| /create_google_calendar_event.py (internal)   | Create a new observation in Google Calendar               | google.auth.transport.requests, google.oauth2.credentials, google_auth_oauthlib.flow, googleapiclient.discovery, googleapiclient.errors, datetime, sys, os.path, urllib.parse packages              |    |
| /call_RT16_api_from_java.py (internal)        | Get all observations from RT-16 API in the specific date      | same as previous^^^              |    |
 | scripts/aips_setup.sh | Create file AIPSSETUP and set AIPSSETUP parameters DATAA, DATAB, DATAC, DATAD. DATAA is directory for contiuum pass results. DATAB is a directory for line pass directory. DATAC directory for ANTAB and UVFLAG fails. DATAD results for AIPS. The script also copies ANTAB and UVFLAG files from FLEXBUFF to the DATAC directory| | observation name, year, first three letters of the month name, day, month number  | aips_setup.sh 18g1b 2021 Jul 22 07| 
 | scripts/createResults.sh | Create zip file of observation results | |observation name, experiment type (sfxc or kana) | createResults.sh aa1 sfxc|
 | scripts/create_fits_files.sh | Create FITS and MS file for a specific scan if a single scan correlation is chosen | | observation name, scan |  create_fits_files.sh aa1 No0001|
| scripts/create_fits_files_multi.sh | create FITS and MS file for all scans for specific pass | | observation name, pass name (line or contiuum) |   create_fits_files_multi.sh aa1 line|
| scripts/create_standart_plots.sh | create standart plots | | source, observation name, pass name, ms file, station |   create_standart_plots.sh cepa aa1 line aa1_line.ms ir |
| scripts/docorrelation.sh |Create all necessary directories for single scan correlation and run single scan correlation. Also, execute create fits files.sh script | | observation name, scan, scan lower| docorrelation.sh No0001 no0001 |
| scripts/get_gps_for_ib_and_ir.sh | copy GPS files from flexbuff to HPC  | | station, first three letters of month name + year, observation name, experiment type  | get_gps_for_ib_and_ir.sh ir jul21 aa1 sfxc|
| scripts/make_dir.sh | Create a directory for correlation and copy the VEX file from FLEXBUFF. And copy SFXC resources to correlation directory | | observation name, experiment type, year, first three letters of the month name, day, month nr | make_dir.sh aa1 sfxc 2021 Jul 22 07 |
| scripts/run_aips.sh | excute aips_setup.sh, irib.py and convert_aips_plots.py | | observation name, year, first three letters of the month name, day, month nr | run_aips.sh aa1 2024 Jul 22 07 |
| scripts/run_correlate_all_scans.sh | Send data from FLEXBUFFS to LOFAR data service. Execute fring_fit.py and correlate_all_scans.py scripts | |  observation name, pass name | run_correlate_all_scans.sh aa1 line|

### Additional python scripts

| Script name          | Input parameters | Description         | Usage example             | 
|----------------------|------------------| --------------------|---------------------------|
| /python/irib.py      | observation name | Calibrate the data  | ParselToung  irib.py  aa1 |
| /python/fring_fit.py | observation name | run clock search    | python2 ring_fit.py aa1   |
| /python/convert_aips_plots.py | aips result directory | convert aips results from EPS to PDF | python3 convert_aips_plots.py result |
| /python/correlate_all_scans.py | observation name, pass name | generate  control file for SFXC. Execute html_plot_page_multi.py and create_standart_plots.py scripts. | python3 correlate_all_scans.py |
| /python/html_plot_page_multi.py  | observation name, scan nr, pass name | generate index page for ACor system | python2 html_plot_page_multi.py aa1 No001 line|
| /python/create_standart_plots.py | observation name, pass name | Execute create_standart_plots.sh script for all scans. |  python3 create_standart_plots.py  aa1 line |
| /python/vex_update.py * | vex file name vix file name | update vex file |  python2  vex_update.py aa1.vex aa1.vix  |
| /python/gps.py * | vex file name | create "CLOCK" block in vex file |  python2 gps aa1.vex|
| /python/eop.py * | vex file | create "EOP" block in vex file |  python2 eop.py  aa1.vex |

* these scripts are updated version of scripts that included in SFXC code. 
       


## Setting up the Front-end
### Prerequisites
- Node.js 18.14.0 or above
  - Angular CLI 15.1.6 or above
- NPM 9.4.2 or above 
- SSL
- pm2

### Libraries and Versions
- @angular-devkit/build-angular: 16.2.12
- @angular/animations: 16.2.12
- @angular/cdk: 16.2.13
- @angular/cli: 17.0.10
- @angular/common: 16.2.12
- @angular/compiler-cli: 16.2.12
- @angular/compiler: 16.2.12
- @angular/core: 16.2.12
- @angular/forms: 16.2.12
- @angular/material: 16.2.13
- @angular/platform-browser-dynamic: 16.2.12
- @angular/platform-browser: 16.2.12
- @angular/router: 16.2.12
- @stomp/ng2-stompjs: 8.0.0
- @stomp/stompjs: 6.1.2
- @syncfusion/ej2-angular-grids: 23.1.44
- @syncfusion/ej2-material-theme: 20.4.54
- @types/jasmine: 3.10.2
- @types/jquery: 3.5.10
- @types/node: 12.20.38
- @types/sockjs-client: 1.5.1
- @types/stompjs: 2.3.5
- angular-material-autocomplete-list: 1.1.0
- bn-ng-idle: 1.0.1
- bootstrap: 5.3.0-alpha1
- https-proxy-agent: 5.0.1
- jasmine-core: 3.10.1
- jquery: 3.6.0
- karma-chrome-launcher: 3.1.0
- karma-coverage: 2.0.3
- karma-jasmine-html-reporter: 1.7.0
- karma-jasmine: 4.0.1
- karma: 6.3.19
- moment: 2.30.1
- net: 1.0.2
- ng: 0.0.0
- ng2-pdf-viewer: 9.1.4
- rxjs: 7.4.0
- sockjs-client: 1.5.2
- sockjs: 0.3.24
- stompjs: 2.3.3
- tslib: 2.6.2
- typescript: 5.1.6
- zone.js: 0.13.3



# Notes 
- [!CAUTION] Frontend views are not fully tested in all scenarios, so caution is advised.
- [!IMPORTANT] Published code includes lines that have been commented out during development or testing. Before proceeding with a production release, please ensure that all such lines are uncommented using the specified TODO comments.

Example TODO comment:
```
// TODO Uncomment these lines for production release:
```

# Deployment 
1) Gradle build
2) set war file name in serviceScripts/VLBI_Web_App.sh
3) serviceScripts/VLBI_Web_App.sh start
4) pm2 start angularclient/start.sh 

# Acknowledgements
This software was written by Jānis Šteinbergs and Karina Šķirmante. If you make use of this software to get results that appear in a publication or presentation please include this acknowledgement: &quot;We have made use of Automatic Correlation (ACor) information system, a tool developed by Jānis Šteinbergs and Karina Šķirmante.&quot;

This work was supported by Latvian Council of Science Project "A single-baseline radio interferometer in a new age of transient astrophysics" Nr.: lzp-2022/1-0083.
