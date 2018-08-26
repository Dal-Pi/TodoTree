package com.kania.todotree.view;

import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kania.todotree.R;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.view.subjectlist.SubjectListFragment;
import com.kania.todotree.view.todolist.CheckListFragment;

public class MainActivity extends AppCompatActivity
        implements SubjectListFragment.OnShowingSubjectListener {

    private static final int END_TIMER_MILLISECOND = 3000;
    private SubjectListFragment mSubjectListFragment;
    private CheckListFragment mCheckListFragment;
    private CountDownTimer mBackTimer;
    private boolean mIsNowBackTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            addFragment();
        } else {
            restoreFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (cancelSelectIfSelected()) {
            return;
        }

        if (mBackTimer != null && mIsNowBackTimer) {
            super.onBackPressed();
        } else {
            mIsNowBackTimer = true;
            Toast.makeText(this, R.string.backpress_timer_wait_text, Toast.LENGTH_SHORT).show();
            mBackTimer = new CountDownTimer(END_TIMER_MILLISECOND, END_TIMER_MILLISECOND) {
                @Override
                public void onTick(long l) {
                    //do noting
                }
                @Override
                public void onFinish() {
                    mIsNowBackTimer = false;
                }
            }.start();
        }
    }

    private boolean cancelSelectIfSelected() {
        if (mCheckListFragment != null) {
            return mCheckListFragment.cancelSelectIfSelected();
        }
        return false;
    }
    private void setSelectedSubjectId(long id) {
        if (mCheckListFragment != null)
            mCheckListFragment.setSelectedShowingSubjectId(id);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackTimer != null) {
            mBackTimer.cancel();
        }
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        mCheckListFragment = CheckListFragment.newInstance(1);
        FragmentTransaction todoFragmentTransaction = fragmentManager.beginTransaction();
        todoFragmentTransaction.add(R.id.main_container_todo_list, mCheckListFragment);
        todoFragmentTransaction.commit();

        mSubjectListFragment = SubjectListFragment.newInstance();
        FragmentTransaction subjectFragmentTransaction = fragmentManager.beginTransaction();
        subjectFragmentTransaction.add(R.id.main_container_subject_list, mSubjectListFragment);
        subjectFragmentTransaction.commit();
    }

    private void restoreFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment retainSubjectFragment = fragmentManager.
                findFragmentById(R.id.main_container_subject_list);
        if (retainSubjectFragment instanceof SubjectListFragment)
            mSubjectListFragment = (SubjectListFragment)retainSubjectFragment;

        Fragment retainTodoFragment = fragmentManager.
                findFragmentById(R.id.main_container_todo_list);
        if (retainTodoFragment instanceof CheckListFragment)
            mCheckListFragment = (CheckListFragment)retainTodoFragment;
    }

    @Override
    public void onChangeSelectedSubject(long onlyOneSubject) {
        cancelSelectIfSelected();
        setSelectedSubjectId(onlyOneSubject);
    }
}
