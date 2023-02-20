## Architecture Overview

### Language
Utilized Kotlin due to my familiarity with it and some experience with Spring Boot

### Libraries/Tech
As mentioned above, made use of Spring Boot for the http server for the assignment. On top of that, I used Postgres DB for the storage of the video metadata 
since it's a popular relational DB for a small scale like this. 

## Running manually
To build the code and run manually, you'll need to have java installed, as well as gradle. 

After that, it's as simple as using: `gradle build`, followed by:

`./gradlew build && java -jar /build/libs/woven-videoserver-1.0.0.jar`
 
OR

once the jar is built, you can run the `newman run test/tester.json` to replicate the github actions test case

## Design Considerations

For the storage of video metadata I used a relational DB, but for the videos themselves, they're saved in the file system. The reasoning for this is that
they're very large files and the scaling in a relational DB would be terrible if this got scaled up. Instead, an object storage like a file system
is preferable, and if ever moved into the cloud something like Amazon S3.

