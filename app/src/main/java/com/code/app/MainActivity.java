package com.code.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.code.app.coldstart.AppLauncher;
import com.code.app.coldstart.core.ColdStartTask;

public class MainActivity extends AppCompatActivity {
    private ColdStartTask taskA, taskB, taskC, taskD, taskE, taskF, taskG, taskH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start_task).setOnClickListener(view -> {
            startTask();
        });
    }

    private void createInitTask() {
        taskA = new TaskA("taskA");
        taskB = new TaskB("taskB");
        taskC = new TaskC("taskC");
        taskD = new TaskD("taskD");
        taskE = new TaskE("taskE",false);
        taskF = new TaskF("taskF");
        taskG = new TaskG("taskG");
        taskH = new TaskH("taskH");
    }

    private void startTask() {
        createInitTask();
        AppLauncher
                .get()
                .builder(getApplication())
                .add(taskA)
                .add(taskB).after(taskA)
                .add(taskC)
                .add(taskD).after(taskB)
                .add(taskE).after(taskD)
                .add(taskF)
                .add(taskG)
                .add(taskH).after(taskE)
                .createProject()
                .startTaskWithAwait();

        Log.d("cold-start", "MainActivity btn_start_task click after AppLauncher Init");
    }

    class TaskA extends ColdStartTask {

        public TaskA(String methodId) {
            super(MainActivity.this.getApplication(), methodId);
        }

        @Override
        public void runOnTaskRunnable() {
            Log.d("MainActivity", "我是Task A");
        }
    }

    class TaskB extends ColdStartTask {

        public TaskB(String methodId) {
            super(MainActivity.this.getApplication(), methodId);
        }

        @Override
        public void runOnTaskRunnable() {
            Log.d("MainActivity", "我是Task B");
        }
    }

    class TaskC extends ColdStartTask {

        public TaskC(String methodId) {
            super(MainActivity.this.getApplication(), methodId);
        }

        @Override
        public void runOnTaskRunnable() {
            Log.d("MainActivity", "我是Task C");
        }
    }

    class TaskD extends ColdStartTask {

        public TaskD(String methodId) {
            super(MainActivity.this.getApplication(), methodId);
        }

        @Override
        public void runOnTaskRunnable() {
            Log.d("MainActivity", "我是Task D");
        }
    }

    class TaskE extends ColdStartTask {

        public TaskE(String methodId, boolean runOnUiThread) {
            super(MainActivity.this.getApplication(), methodId, runOnUiThread);
        }

        @Override
        public void runOnTaskRunnable() {
            Log.d("MainActivity", "我是Task E");
        }
    }

    class TaskF extends ColdStartTask {

        public TaskF(String methodId) {
            super(MainActivity.this.getApplication(), methodId);
        }

        @Override
        public void runOnTaskRunnable() {
            Log.d("MainActivity", "我是Task F");
        }
    }

    class TaskG extends ColdStartTask {

        public TaskG(String methodId) {
            super(MainActivity.this.getApplication(), methodId);
        }

        @Override
        public void runOnTaskRunnable() {
            Log.d("MainActivity", "我是Task G");
        }
    }

    class TaskH extends ColdStartTask {

        public TaskH(String methodId) {
            super(MainActivity.this.getApplication(), methodId);
        }

        @Override
        public void runOnTaskRunnable() {
            Log.d("MainActivity", "我是Task H");
        }
    }
}