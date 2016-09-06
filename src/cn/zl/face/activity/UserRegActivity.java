package cn.zl.face.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.face.detect.FaceppDetect;
import cn.face.detect.FaceppDetect.CallBack;
import snippet.Person;

public class UserRegActivity extends Activity {

	private static final int TIME = 2500;
	private EditText userEd;
	Person mPerson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
		userEd = (EditText) findViewById(R.id.userEd);

	}

	public void onRE(View v) {
		String name = userEd.getText().toString();
		if (name == null || name.length() == 0) {
			Toast.makeText(this, "请输入用户名 ", 3000).show();
			return;
		}
		FaceppDetect.creatPerson(name, new CallBack() {

			@Override
			public void success(JSONObject result) {
				// TODO Auto-generated method stub
				Log.e("", "result:" + result);
				mPerson = new Person();
				try {
					mPerson.init(result);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				runOnUiThread(new Runnable() {
					public void run() {
						if (mPerson.person_name == null || mPerson.person_name.length() == 0) {
							Toast.makeText(UserRegActivity.this, "注册失败 ", 3000).show();
							return;
						}
						Toast.makeText(UserRegActivity.this, "注册成功 ", 3000).show();
						Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
						intent.putExtra("person", mPerson);
						startActivity(intent);
						finish();
					}
				});

			}

			@Override
			public void error(int e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(UserRegActivity.this, "注册失败 ", 3000).show();
					}
				});

			}
		});
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
			finish();
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
