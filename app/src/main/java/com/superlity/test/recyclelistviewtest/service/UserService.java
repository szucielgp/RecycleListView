package com.superlity.test.recyclelistviewtest.service;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.avos.avoscloud.SaveCallback;
import com.superlity.test.recyclelistviewtest.leancloud.entity.User;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by lzw on 14-9-15.
 */
public class UserService {
    public static final int ORDER_UPDATED_AT = 1;
    public static final int ORDER_DISTANCE = 0;

    public static AVUser findUser(String id) throws AVException {
        AVQuery<AVUser> q = AVUser.getQuery(AVUser.class);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        return q.get(id);
    }

    public static AVQuery<AVUser> getFriendQuery() {
        AVUser curUser = AVUser.getCurrentUser();
        AVQuery<AVUser> q = null;
        try {
            q = curUser.followeeQuery(AVUser.class);
        } catch (Exception e) {
            //在 currentUser.objectId 为 null 的时候抛出的，不做处理
            // Logger.e(e.getMessage());
        }
        return q;
    }

    public static void findFriendsWithCachePolicy(AVQuery.CachePolicy cachePolicy, FindCallback<AVUser>
            findCallback) {
        AVQuery<AVUser> q = getFriendQuery();
        q.setCachePolicy(cachePolicy);
        q.setMaxCacheAge(TimeUnit.MINUTES.toMillis(1));
        q.findInBackground(findCallback);
    }

//  public static List<AVUser> findFriends() throws Exception {
//    final List<AVUser> friends = new ArrayList<AVUser>();
//    final AVException[] es = new AVException[1];
//    final CountDownLatch latch = new CountDownLatch(1);
//    findFriendsWithCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK, new FindCallback<AVUser>() {
//      @Override
//      public void done(List<AVUser> avUsers, AVException e) {
//        if (e != null) {
//          es[0] = e;
//        } else {
//          friends.addAll(avUsers);
//        }
//        latch.countDown();
//      }
//    });
//    latch.await();
//    if (es[0] != null) {
//      throw es[0];
//    } else {
//      List<String> userIds = new ArrayList<String>();
//      for (AVUser user : friends) {
//        userIds.add(user.getObjectId());
//      }
//      CacheService.cacheUsers(userIds);
//      List<AVUser> newFriends = new ArrayList<>();
//      for (AVUser user : friends) {
//        newFriends.add(CacheService.lookupUser(user.getObjectId()));
//      }
//      return newFriends;
//    }
//  }


    public static AVUser signUp(String name, String password) throws AVException {
        AVUser user = new AVUser();
        user.setUsername(name);
        user.setPassword(password);
        user.signUp();
        return user;
    }

    public static void saveAvatar(String path) throws IOException, AVException {
        AVUser user = AVUser.getCurrentUser();
        final AVFile file = AVFile.withAbsoluteLocalPath(user.getUsername(), path);
        file.save();
        user.put(User.AVATAR, file);
        user.save();
        user.fetch();
    }

    public static void updateUserInfo() {
        AVUser user = AVUser.getCurrentUser();
        if (user != null) {
            AVInstallation installation = AVInstallation.getCurrentInstallation();
            if (installation != null) {
                user.put(User.INSTALLATION, installation);
                user.saveInBackground();
            }
        }
    }


    public static void addFriend(String friendId, final SaveCallback saveCallback) {
        AVUser user = AVUser.getCurrentUser();
        user.followInBackground(friendId, new FollowCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                if (saveCallback != null) {
                    saveCallback.done(e);
                }
            }
        });
    }

    public static void removeFriend(String friendId, final SaveCallback saveCallback) {
        AVUser user = AVUser.getCurrentUser();
        user.unfollowInBackground(friendId, new FollowCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                if (saveCallback != null) {
                    saveCallback.done(e);
                }
            }
        });
    }
}
