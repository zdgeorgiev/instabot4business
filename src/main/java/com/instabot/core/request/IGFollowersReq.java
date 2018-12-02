package com.instabot.core.request;

public class IGFollowersReq {

	//	private static void saveFollowers(Instagram4j user, long userId, String nextMaxId)
	//			throws ClientProtocolException, IOException, InterruptedException {
	//		try {
	//			while (true) {
	//				InstagramGetUserFollowersResult fr = user.sendRequest(new InstagramGetUserFollowersRequest(userId, nextMaxId));
	//
	//				if (fr.getUsers().equals(null))
	//					break;
	//
	//				followers.addAll(fr.getUsers().stream().map(x -> x.username).collect(Collectors.toList()));
	//
	//				nextMaxId = fr.getNext_max_id();
	//				if (nextMaxId == null) {
	//					break;
	//				}
	//			}
	//		} catch (Exception e) {
	//			System.err.println("Is user logged? " + user.isLoggedIn());
	//			Thread.sleep(30000);
	//			saveFollowers(user, userId, nextMaxId);
	//		} finally {
	//			System.out.println(nextMaxId);
	//			System.out.println(followers.size());
	//			FileUtils.writeLines(new File("c:\\\\\\\\tmp\\\\\\\\test" + userId + ".txt"), followers);
	//		}
	//	}
}
