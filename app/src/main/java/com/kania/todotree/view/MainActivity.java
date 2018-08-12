package com.kania.todotree.view;

import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kania.todotree.R;
import com.kania.todotree.TodoTree;

public class MainActivity extends AppCompatActivity {

    private static final int END_TIMER_MILLISECOND = 3000;
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
            Fragment retainFragment = getSupportFragmentManager().
                    findFragmentById(R.id.main_container_list);
            if (retainFragment instanceof CheckListFragment)
                mCheckListFragment = (CheckListFragment)retainFragment;
            else {
                //TODO else case
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        if (mCheckListFragment == null) {
            mCheckListFragment = getSupportFragmentManager().
                    findFragmentById(R.id.main_container_list);
            if (mCheckListFragment != null) {

            } else {
                Log.e(TodoTree.TAG, "[MainActivity::onCreate] cannot find CheckListFragment when restarting");
                addFragment();
            }
        }
*/
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
        //TODO check fragment select. if select just deselect, if not, start end timer.(select move task first)
        if (mCheckListFragment != null) {
            if (mCheckListFragment.cancelSelectIfSelected()) {
                return;
            }
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
        mCheckListFragment = CheckListFragment.newInstance(1);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_container_list, mCheckListFragment);
        fragmentTransaction.commit();
    }
}
