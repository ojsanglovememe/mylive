package com.src.isec.integration;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.integration
 * @class Activity全局管理类
 * 用于管理所有 {@link Activity},和在前台的 {@link Activity}
 * @time 2018/3/19 10:54
 * @change
 * @chang time
 * @class describe
 */
@Singleton
public class AppManager {

    protected final String TAG = this.getClass().getSimpleName();
    public static final String APPMANAGER_MESSAGE = "appmanager_message";
    //true 为不需要加入到 Activity 容器进行统一管理,默认为 false
    public static final String IS_NOT_ADD_ACTIVITY_LIST = "is_not_add_activity_list";
    public static final int START_ACTIVITY = 5000;
    public static final int SHOW_SNACKBAR = 5001;
    public static final int KILL_ALL = 5002;
    public static final int APP_EXIT = 5003;
    @Inject
    Application mApplication;
    //管理所有存活的 Activity, 容器中的顺序仅仅是 Activity 的创建顺序, 并不能保证和 Activity 任务栈顺序一致
    private List<Activity> mActivityList;
    //当前在前台的 Activity
    private Activity mCurrentActivity;

    @Inject
    public AppManager() {
    }


    /**
     * @param intent
     * @author liujiancheng
     * @time 2018/3/19  11:34
     * @describe 让在栈顶的 {@link Activity} ,打开指定的 {@link Activity}
     */
    public void startActivity(Intent intent) {
        if (getTopActivity() == null) {
            Timber.tag(TAG).w("mCurrentActivity == null when startActivity(Intent)");
            //如果没有前台的activity就使用new_task模式启动activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplication.startActivity(intent);
            return;
        }
        getTopActivity().startActivity(intent);
    }

    /**
     * @param activityClass 指定要启动的Activity
     * @author liujiancheng
     * @time 2018/3/19  11:35
     * @describe 让在栈顶的 {@link Activity} ,打开指定的 {@link Activity}
     */
    public void startActivity(Class activityClass) {
        startActivity(new Intent(mApplication, activityClass));
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  11:35
     * @describe 资源释放
     */
    public void release() {
        mActivityList.clear();
        mActivityList = null;
        mCurrentActivity = null;
        mApplication = null;
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  11:35
     * @describe 将在前台的 {@link Activity} 赋值给 {@code currentActivity}, 注意此方法是在
     * {@link Activity#onResume} 方法执行时将栈顶的 {@link Activity} 赋值给 {@code currentActivity}
     * 所以在栈顶的 {@link Activity} 执行 {@link Activity#onCreate} 方法时使用 {@link #getCurrentActivity()}
     * 获取的就不是当前栈顶的 {@link Activity}, 可能是上一个 {@link Activity}
     * 如果在 App 启动第一个 {@link Activity} 执行 {@link Activity#onCreate} 方法时使用
     * {@link #getCurrentActivity()} 则会出现返回为 {@code null} 的情况
     * 想避免这种情况请使用 {@link #getTopActivity()}
     */
    public void setCurrentActivity(Activity currentActivity) {
        this.mCurrentActivity = currentActivity;
    }


    /**
     * @return
     * @author liujiancheng
     * @time 2018/3/19  11:38
     * @describe 获取在前台的 {@link Activity} (保证获取到的 {@link Activity} 正处于可见状态, 即未调用
     * {@link Activity#onStop()}),
     * 获取的 {@link Activity} 存续时间
     * 是在 {@link Activity#onStop()} 之前, 所以如果当此 {@link Activity} 调用 {@link Activity#onStop()}
     * 方法之后, 没有其他的 {@link Activity} 回到前台(用户返回桌面或者打开了其他 App 会出现此状况)
     * 这时调用 {@link #getCurrentActivity()} 有可能返回 {@code null}, 所以请注意使用场景和
     * {@link #getTopActivity()} 不一样
     * <p>
     * Example usage:
     * 使用场景比较适合, 只需要在可见状态的 {@link Activity} 上执行的操作
     * 如当后台 {@link Service} 执行某个任务时, 需要让前台 {@link Activity} ,做出某种响应操作或其他操作,如弹出 {@link Dialog},
     * 这时在 {@link Service} 中就可以使用 {@link #getCurrentActivity()}
     * 如果返回为 {@code null}, 说明没有前台 {@link Activity} (用户返回桌面或者打开了其他 App 会出现此状况), 则不做任何操作, 不为 {@code
     * null}, 则弹出 {@link Dialog}
     */
    public Activity getCurrentActivity() {
        return mCurrentActivity != null ? mCurrentActivity : null;
    }



    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:39
     *  @describe 获取最近启动的一个 {@link Activity}, 此方法不保证获取到的 {@link Activity} 正处于前台可见状态
     * 即使 App 进入后台或在这个 {@link Activity} 中打开一个之前已经存在的 {@link Activity}, 这时调用此方法
     * 还是会返回这个最近启动的 {@link Activity}, 因此基本不会出现 {@code null} 的情况
     * 比较适合大部分的使用场景, 如 startActivity
     * <p>
     * Tips: mActivityList 容器中的顺序仅仅是 Activity 的创建顺序, 并不能保证和 Activity 任务栈顺序一致
     *
     * @return
     */
    public Activity getTopActivity() {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when getTopActivity()");
            return null;
        }
        return mActivityList.size() > 0 ? mActivityList.get(mActivityList.size() - 1) : null;
    }



    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:39
     *  @describe  返回一个存储所有未销毁的 {@link Activity} 的集合
     *
     * @return
     */
    public List<Activity> getActivityList() {
        if (mActivityList == null) {
            mActivityList = new LinkedList<>();
        }
        return mActivityList;
    }




    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:39
     *  @describe 添加 {@link Activity} 到集合
     */
    public void addActivity(Activity activity) {
        synchronized (AppManager.class) {
            List<Activity> activities = getActivityList();
            if (!activities.contains(activity)) {
                activities.add(activity);
            }
        }
    }




    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:40
     *  @describe 删除集合里的指定的 {@link Activity} 实例
     */
    public void removeActivity(Activity activity) {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when removeActivity(Activity)");
            return;
        }
        synchronized (this) {
            if (mActivityList.contains(activity)) {
                mActivityList.remove(activity);
            }
        }
    }




    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:40
     *  @describe  删除集合里的指定位置的 {@link Activity}
     *  @param location 指定的下标位
     */
    public Activity removeActivity(int location) {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when removeActivity(int)");
            return null;
        }
        synchronized (AppManager.class) {
            if (location > 0 && location < mActivityList.size()) {
                return mActivityList.remove(location);
            }
        }
        return null;
    }



    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:41
     *  @describe  关闭指定的 {@link Activity} class 的所有的实例
     *  @param activityClass 指定的Activity类型
     */
    public void killActivity(Class<?> activityClass) {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when killActivity(Class)");
            return;
        }
        synchronized (this) {
            Iterator<Activity> iterator = getActivityList().iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next();

                if (next.getClass().equals(activityClass)) {
                    iterator.remove();
                    next.finish();
                }
            }
        }
    }




    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:42
     *  @describe 指定的 {@link Activity} 实例是否存活
     */
    public boolean activityInstanceIsLive(Activity activity) {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when activityInstanceIsLive(Activity)");
            return false;
        }
        return mActivityList.contains(activity);
    }




    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:42
     *  @describe 指定的 {@link Activity} class 是否存活(同一个 {@link Activity} class 可能有多个实例)
     *  @param activityClass 指定的Activity类型
     */
    public boolean activityClassIsLive(Class<?> activityClass) {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when activityClassIsLive(Class)");
            return false;
        }
        for (Activity activity : mActivityList) {
            if (activity.getClass().equals(activityClass)) {
                return true;
            }
        }
        return false;
    }




    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:43
     *  @describe 获取指定 {@link Activity} class 的实例,没有则返回 null(同一个 {@link Activity} class 有多个实例,则返回最早创建的实例)
     *  @param activityClass 指定的Activity类型
     */
    public Activity findActivity(Class<?> activityClass) {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when findActivity(Class)");
            return null;
        }
        for (Activity activity : mActivityList) {
            if (activity.getClass().equals(activityClass)) {
                return activity;
            }
        }
        return null;
    }



    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:44
     *  @describe  关闭所有 {@link Activity}
     */
    public void killAll() {
        synchronized (this) {
            Iterator<Activity> iterator = getActivityList().iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next();
                iterator.remove();
                next.finish();
            }
        }
    }



    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:45
     *  @describe 关闭所有 {@link Activity},排除指定的 {@link Activity}
     *  @param excludeActivityClasses 需要排除的Activity类型集合
     */
    public void killAll(Class<?>... excludeActivityClasses) {
        List<Class<?>> excludeList = Arrays.asList(excludeActivityClasses);
        synchronized (AppManager.class) {
            Iterator<Activity> iterator = getActivityList().iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next();

                if (excludeList.contains(next.getClass()))
                    continue;

                iterator.remove();
                next.finish();
            }
        }
    }



    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:45
     *  @describe 关闭所有 {@link Activity},排除指定的 {@link Activity}
     *   @param excludeActivityName {@link Activity} 的完整全路径
     */
    public void killAll(String... excludeActivityName) {
        List<String> excludeList = Arrays.asList(excludeActivityName);
        synchronized (AppManager.class) {
            Iterator<Activity> iterator = getActivityList().iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next();

                if (excludeList.contains(next.getClass().getName()))
                    continue;

                iterator.remove();
                next.finish();
            }
        }
    }



    /**
     *  @author liujiancheng
     *  @time 2018/3/19  11:46
     *  @describe  退出应用程序
     */
    public void appExit() {
        try {
            killAll();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
