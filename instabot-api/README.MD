# Instabot-api

`Instagram-bot is bot that helps you to collect more followers in a automated way.` 
There is an API to successfully operate with commands given by the core. The idea is that through this API
you can do certain functions by yourself, because in the end its a BOT and not a manual program.
Anyway there are couple of manual very important things to do to keep the bot workflow working.
When you start the api if all the parameters are set you will have two instagram accounts logged.
One main account and one fake account. The idea is that the main account (which you want to boost)
and the fake one is that account who is used for collecting information, because in the end
the main account should do four things -followUser, -unfollowUser, -likePhoto -uploadPhoto. Any other functions
that collect followers, comments, read the users information, filters - that is done by the fake one
and if you are making too much requests they will ban the fake account and not the main one.

## Important endpoints:

* API REST Documentation - `localhost:8080/instabot-api/swagger-ui.html#/`

* Manual execution:
  * POST `/follow/processTopFollowers` This will get last photos for a user, then it will collect all comments
and will create a list of the users which will be sorted on specific strategy (e.g. MostCommentsSortingStrategy).
Then the list will be divided in two lists. First 75% will be followed and for the rest 25% we will
collect their last couple of photos and we will choose couple of them to be liked (random or the latest).
  * POST `/like/processTopLikers` This is similar to the previous one, but here we will collect last couple of photos
for specific user and collect all the likers and combine them in one big list to see which ones are
that liked most of them. top 90% we will choose couple of their photos to like back and the other 10%
will be added to be followed.
  * POST `/hashtag` add hashtag to your hashtag list from which you will like photos.
  * DELETE `/hashtag` remove hashtag from your list.
  * GET `/hashtag` view all hashtags that you have.

* Automated execution:
  * `/bot/cleanFollowings`(Every day at 5PM) This function will users that you are following for at least given number of days.
  The function will finish in between 15-20 hours, so a random timeout will be set between each unfollow request.
  * `/bot/followUsers`(Every day at 5PM) This function will get the max users you can follow for a day
  from toFollow queue and will start following them. The function will finish in between 15-20 hours, so a random
  timeout will be set between each following request.
  * `/bot/likePhotos`(Every day at 5PM) This function will get the max photos you can like for a day
  from toLike queue and will start liking them. The function will finish in between 15-20 hours, so a random
  timeout will be set between each photo like request.
  * `/bot/addPhotosFromHashtag` (Every day every hour) This function will iterate over all your hashtags
  and will select couple of photos to be liked for each hashtag. The chosen photoIds will be added
  in your toLike queue.
  * `/bot/uploadPhotos` (Every day at 9PM) This function will upload photos to your timeline from given directory


## Mandatory API parameters
* `-Ddb.username` Mqsql Database username.
* `-Ddb.password` Mqsql Database password.
* `-Dig.main.bot.username` Main account username.
* `-Dig.main.bot.password` Main account password.
* `-Dig.fake.bot.username` Fake account username.
* `-Dig.fake.bot.password` Fake account password.
* `-Dwebdriver.chrome.driver="path.to.chrome.driver.exe"` Path to chrome driver used by selenium.

## Optional API parameters
* `-Dig.bot.api.max.likes.per.day` [Default: 400] Maximum likes per day.
* `-Dig.bot.api.max.follows.per.day` [Default: 200] Maximum follows per day.
* `-Dig.bot.api.max.unfollows.per.day` [Default: 250] Maximum unfollows per day.
* `-Dig.bot.api.scheduled.request.min.hours.to.complete` [Default: 15] Minimum hours to finish the 
scheduled requests (liking and following)
* `-Dig.bot.api.scheduled.request.max.hours.to.complete` [Default: 20] Maximum hours to finish the 
scheduled requests (liking and following)
* `-Dig.bot.api.unfollow.older.than.days` [Default: 3] Unfollow users which you follow for at least days
* `-Dig.bot.api.hashtag.photos.to.get` [Default: 12] Last photos to get for each hashtag
* `-Dig.bot.api.hashtag.photos.to.return` [Default: 3] Random photos to be returned for each hashtag
* `-Dig.bot.api.last.user.photos.to.get` [Default: 30] Last photos to collect for user
* `-Dig.bot.api.top.followers.percentage.to.follow` [Default: 75] Percentage of top followers to be followed and for
the rest we will like couple of their photos
* `-Dig.bot.api.top.followers.photos.to.get` [Default: 3] Last photos to get for each top follower
* `-Dig.bot.api.top.followers.photos.to.return` [Default: 1] Random photos to be liked for each top follower
* `-Dig.bot.api.top.likers.percentage.to.follow` [Default: 10] Percentage of top likers to be followed
* `-Dig.bot.api.top.likers.photos.to.get` [Default: 3] Last photos to get for each top liker
* `-Dig.bot.api.top.likers.photos.to.return` [Default: 1] Random photos to be liked for each top liker
* `-Dig.bot.api.photos.dir.path` [Default: ""] Directory of the photos (Maybe default is the dir from where the app is invoked).
Photos must be .jpg or .png and if there is file with the same name but .txt it will be used as a description.
* `-Dig.bot.api.photos.to.upload` [Default: 2] Photos to upload per each iteration
* `-Dig.bot.api.photos.sleep.between.each.upload` [Default: 60] Minutes to wait before upload the next photo

## Filter parameters
* `-Dig.bot.api.filter.skip.private.accounts` [Default: true] Skip users with private accounts
* `-Dig.bot.api.filter.skip.business.accounts` [Default: true] Skip users with business profile
* `-Dig.bot.api.filter.skip.users.without.profile.picture` [Default: true] Skip users without profile picture
* `-Dig.bot.api.filter.skip.spam.comments` [Default: true] Skip comments with potentially spam content
