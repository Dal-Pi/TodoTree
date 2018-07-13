package com.kania.todotree.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kania.todotree.R;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.data.TodoData;

public class MainActivity extends AppCompatActivity
        implements CheckListFragment.OnListFragmentInteractionListener,
        AddSubjectDialog.OnCompleteAddSubject,
        AddTodoDialog.OnCompleteAddTodo {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new FloatingButtonClickListener());
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
    public void onListFragmentInteraction(TodoData item) {
        //Toast.makeText(this, item.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleteAddSubject(SubjectData completedSubject) {
        if (completedSubject != null) {

        }
    }

    @Override
    public void onCompleteAddTodo(TodoData completedTodo) {
        if (completedTodo != null) {

        }
    }

    public class FloatingButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            DialogFragment addTodoDialog = AddTodoDialog.newInstance(null);
            addTodoDialog.show(getSupportFragmentManager(), AddTodoDialog.class.getName());
        }
    }
}
