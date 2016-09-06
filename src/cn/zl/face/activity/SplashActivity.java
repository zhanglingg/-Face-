package cn.zl.face.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class SplashActivity extends Activity {

	private static final int TIME = 2500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		findViewById(R.id.splash).postDelayed(new Runnable() {
			public void run() {
				onGoHome();
			}
		}, TIME);

	}

	private void onGoHome() {
		Intent intent = null;

		intent = new Intent(getApplicationContext(), LoginActivity.class);

		startActivity(intent);
		finish();
		// overridePendingTransition(R.anim.move_in_right,
		// R.anim.move_out_left);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
