# instabot4business

## Summary
Instagram4business is a bot that helps you to collect more followers in a automated way. There is web API to successfully operate with commands given by the core. The idea is that through the web API you can do certain functions by yourself, because in the end its a BOT and not a manual program. Anyway there are couple of manual very important steps to do to keep the bot workflow. When you start the application if all the parameters are set you will have two instagram accounts logged in. One main account and one fake account. Main account is the one you want to boost and the fake one is used for collecting information, because main account have to do only four things -followUser, -unfollowUser, -likePhoto -uploadPhoto. Any other functions that collect followers, comments, read the users information, filters - is done by the fake one and if you are making too much requests they will ban the fake account and not the main one. More information you can find [Instabot4jbusiness Web API Endpoints](instabot-api/README.MD)

## Functionality
* follow
* unfollow
* upload pictures
* like photos


# Result
I've made a page and thats the actual result of running the bot for a week. As you can see its pretty decent.

![alt text](https://i.imgur.com/7s3lXaI.jpg)

# How to run it
## Requirements
* Java 8+
* Maven 3+
* Mysql

## Build from this directory
- 1) `mvn clean install`
- 2) `java -jar [ALL API PARAMETERS HERE] instabot-api/target/instabot-api-1.0.0-SNAPSHOT.jar`
