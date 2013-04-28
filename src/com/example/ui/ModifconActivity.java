package com.example.ui;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.test_app.R;
public class ModifconActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifcon);

	}
	@Override
    protected void onStart() {
            super.onStart();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
             finish();
             return true;
        }

    return super.onOptionsItemSelected(item);
    }

}
