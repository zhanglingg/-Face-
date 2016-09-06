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

public class LoginActivity extends Activity {

	private static final int TIME = 2500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		userEd = (EditText) findViewById(R.id.userEd);
	}

	Person mPerson;
	private EditText userEd;
	public void onLogin(View v) {
		String name = userEd.getText().toString();
		if (name == null || name.length() == 0) {
			Toast.makeText(this, "请输入用户名 ", 3000).show();
			return;
		}
		FaceppDetect.getInfoPerson(name, new CallBack() {

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
							Toast.makeText(LoginActivity.this, "登录失败 ", 3000).show();
							return;
						}
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
						Toast.makeText(LoginActivity.this, "登录失败 ", 3000).show();
					}
				});
			}
		});
	}

	final int REQUESTCODE_REG = 0xff01;

	public void onRE(View v) {
		Intent intent = new Intent(getApplicationContext(), UserRegActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
