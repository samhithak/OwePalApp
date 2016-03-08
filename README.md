OwePal Application for Android
==============================

**To Build OwePal**

````
./gradlew clean assemble
````

**To Build OwePal with Quality and Coverage Reports**

````
./gradlew clean assemble quality coverage
````

**To View Code Quality Reports**

````
open app/build/reports/findbugs/findbugs.html
open app/build/reports/lint/lint-report.html
open app/build/reports/pmd/pmd.html
````

**To View Code Coverage and Testing Reports**

````
open app/build/outputs/reports/androidTests/connected/index.html 
open app/build/reports/jacoco/html/index.html
````
