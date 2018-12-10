package com.suansuan.sframework.utils.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 预加载执行的task
 */
@SuppressWarnings("all")
public class PreExecuteTaskUtils {

    private static List<QPreExecuteTask> tasks = new ArrayList<>(10);

    public static synchronized void addTask(QPreExecuteTask task) {
        tasks.add(task);
    }

    public static synchronized void runAllTask() {
        if (!ArrayUtils.isEmpty(tasks)) {
            Iterator<QPreExecuteTask> it = tasks.iterator();
            while (it.hasNext()) {
                QPreExecuteTask task = it.next();
                try {
                    task.execute();
                } finally {
                    it.remove();
                }
            }
        }
    }

    /**
     * 预加载执行的task
     */
    public interface QPreExecuteTask {
        void execute();
    }
}
