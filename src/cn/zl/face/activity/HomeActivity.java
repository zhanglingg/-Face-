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

public class HomeActivity extends Activity {

	private static final int TIME = 2500;
	private FaceppDetect detect;

	Person mPerson;
	private EditText userEd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		userEd = (EditText) findViewById(R.id.userEd);
		detect = new FaceppDetect(this);

		mPerson = (Person) getIntent().getSerializableExtra("person");

		TextView name = (TextView) findViewById(R.id.name);
		// name.setText("你好:" + mPerson.person_name);

		String test = null;
		name.setText("fdfd" + test);

		// detect.creatPerson(new CallBack() {
		//
		// @Override
		// public void success(JSONObject result) {
		// // TODO Auto-generated method stub
		// Log.e("", "result:" + result);
		// }
		//
		// @Override
		// public void error(int e) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

		// {"added_face":0,"person_id":"a4e5cfa02b788b85163e613c961d171d","person_name":"zhanglin","added_group":0,"tag":"","response_code":200}

	}

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
							Toast.makeText(HomeActivity.this, "登录失败 ", 3000).show();
							return;
						}
						Toast.makeText(HomeActivity.this, "登录成功 ", 3000).show();
						TextView name = (TextView) findViewById(R.id.name);
						name.setText(mPerson.person_name);
					}
				});

			}

			@Override
			public void error(int e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(HomeActivity.this, "登录失败 ", 3000).show();
					}
				});
			}
		});
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
							Toast.makeText(HomeActivity.this, "注册失败 ", 3000).show();
							return;
						}
						Toast.makeText(HomeActivity.this, "注册成功 ", 3000).show();
						TextView name = (TextView) findViewById(R.id.name);
						name.setText(mPerson.person_name);
					}
				});

			}

			@Override
			public void error(int e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(HomeActivity.this, "注册失败 ", 3000).show();
					}
				});

			}
		});
	}

	public void onOk(View v) {
		if (mPerson == null || mPerson.person_name == null || mPerson.person_name.length() == 0) {
			Toast.makeText(HomeActivity.this, "请登录 ", 3000).show();
			return;
		}
		Intent intent = new Intent(HomeActivity.this, VerifyFaceActivity.class);
		intent.putExtra("person", mPerson);
		startActivity(intent);
	}

	public void onUP(View v) {
		if (mPerson == null || mPerson.person_name == null || mPerson.person_name.length() == 0) {
			Toast.makeText(HomeActivity.this, "请登录 ", 3000).show();
			return;
		}
		Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
		intent.putExtra("person", mPerson);
		startActivity(intent);
	}

	public void onClear(View v) {
		if (mPerson == null || mPerson.person_name == null || mPerson.person_name.length() == 0) {
			Toast.makeText(HomeActivity.this, "请登录 ", 3000).show();
			return;
		}
		FaceppDetect.removePersonFace(mPerson.person_name, new CallBack() {

			@Override
			public void success(JSONObject result) {
				// TODO Auto-generated method stub
				Log.e("", "result:" + result);

				runOnUiThread(new Runnable() {
					public void run() {

						Toast.makeText(HomeActivity.this, "清除成功 ", 3000).show();
						TextView name = (TextView) findViewById(R.id.name);
						name.setText(mPerson.person_name);
					}
				});

			}

			@Override
			public void error(int e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(HomeActivity.this, "清除失败 ", 3000).show();
					}
				});

			}
		});
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", 2000).show();
			} else {
				System.exit(-1);

			}
			exitTime = System.currentTimeMillis();

			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
