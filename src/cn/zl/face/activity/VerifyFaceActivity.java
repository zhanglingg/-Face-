package cn.zl.face.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;
import cn.face.detect.FaceppDetect;
import cn.face.detect.FaceppDetect.CallBack;
import snippet.Person;

@SuppressLint("NewApi")
public class VerifyFaceActivity extends Activity {

	protected static final int SUCCESS = 0;

	protected static final int DETECT_FILED = -1;

	private SurfaceView mPreviewSurface;
	private Timer timer;

	private Camera mCamera;
	private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;

	// Camera nv21格式预览帧的尺寸，默认设置640*480
	private int PREVIEW_WIDTH = 640;
	private int PREVIEW_HEIGHT = 480;

	private ImageView iv_f1;
	private ImageView iv_s2;
	private ImageView iv_t3;
	private boolean selectImage = false;
	private Bitmap bmp;
	private FaceppDetect detect;
	private Person person;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verify_activity);

		Intent intent = super.getIntent();
		if (intent.getSerializableExtra("person") != null) {
			person = (Person) intent.getSerializableExtra("person");
		}

		Log.e("TAG", person.person_name);

		if (person.person_name.length() > 0) {
			initFaceCheck();
		}
	}

	private void initFaceCheck() {
		detect = new FaceppDetect(getApplicationContext());
		mPreviewSurface = (SurfaceView) findViewById(R.id.sfv_preview);
		iv_f1 = (ImageView) findViewById(R.id.iv_f1);
		iv_s2 = (ImageView) findViewById(R.id.iv_s2);
		iv_t3 = (ImageView) findViewById(R.id.iv_t3);

		mPreviewSurface.getHolder().addCallback(mPreviewCallback);
		mPreviewSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private Callback mPreviewCallback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			closeCamera();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			openCamera();
			if (mCamera != null) {
				// 开启定时器
				startTimer();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		}
	};

	// 定时采集图片
	protected void startTimer() {
		selectImage = true;
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// Log.e("timer", "ture");
				selectImage = true;
			}
		}, 1000); // 1秒后开始采集
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void openCamera() {
		if (null != mCamera) {
			return;
		}
		// 只有一个摄相头，打开后置
		if (Camera.getNumberOfCameras() == 1) {
			mCameraId = CameraInfo.CAMERA_FACING_BACK;
		}

		try {
			mCamera = Camera.open(mCameraId);
			if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
				Toast.makeText(getApplicationContext(), "前置摄像头已开启，点击可切换", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "后置摄像头已开启，点击可切换", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			closeCamera();
			return;
		}

		Parameters params = mCamera.getParameters();
		params.setPreviewFormat(ImageFormat.NV21);
		params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
		mCamera.setParameters(params);

		// 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
		mCamera.setDisplayOrientation(90);
		mCamera.setPreviewCallback(new PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				if (selectImage) {
					selectFaceFromPreview(data, camera);
				}
				selectImage = false;

			}
		});

		try {
			mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
			mCamera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void selectFaceFromPreview(byte[] data, Camera camera) {
		// System.arraycopy(data, 0, nv21, 0, data.length);

		Size size = camera.getParameters().getPreviewSize(); // 获取预览大小
		final int w = size.width; // 宽度
		final int h = size.height;

		final YuvImage image = new YuvImage(data, ImageFormat.NV21, w, h, null);
		ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
		if (!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
			return;
		}
		byte[] tmp = os.toByteArray();
		bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);

		// 把图片旋转为正的方向
		bmp = FaceUtil.rotateImage(-90, bmp);
		iv_s2.setImageBitmap(bmp);

		detectFaceOffLine(bmp);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DETECT_FILED:
				showTextToast("露个脸呗");
				break;
			case SUCCESS:
				closeCamera();
				Intent intent = new Intent(VerifyFaceActivity.this, SuccessVerifyActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
				break;
			}
		};
	};

	private Toast toast;

	private void showTextToast(String msg) {
		if (toast == null) {
			toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}

	private void detectFaceOffLine(final Bitmap bmp) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				if (detect.detectOffLine(bmp) == 0) {
					selectImage = true;
					Message msg = Message.obtain();
					msg.what = DETECT_FILED;
					handler.sendMessage(msg);
				} else {
					getFaceIdOnline(bmp);
				}
			}
		}).start();
	}

	private void getFaceIdOnline(Bitmap bmp) {

		FaceppDetect.detectOnline(bmp, new CallBack() {

			@Override
			public void success(JSONObject result) {
				try {
					JSONArray faces = result.getJSONArray("face");
					int faceCount = faces.length();
					// mTip.setText("find:" + faceCount);
					for (int i = 0; i < faceCount; i++) {
						// 拿到单独face对象
						JSONObject face = faces.getJSONObject(i);
						String face_id = face.getString("face_id");

						Log.i("face_id", "face_id:" + face_id);
						verifyIdWithPerson(face_id);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void error(int e) {
				selectImage = true;
			}
		});

	}

	protected void verifyIdWithPerson(String face_id) {
		FaceppDetect.verifyPeson(face_id, person.person_name, new CallBack() {

			@Override
			public void success(JSONObject result) {
				Message msg = Message.obtain();
				msg.what = SUCCESS;
				handler.sendMessage(msg);
			}

			@Override
			public void error(int e) {
				selectImage = true;
			}
		});
	}

	private void closeCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		closeCamera();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		closeCamera();
		timer.cancel();
		bmp.recycle();
		bmp = null;
		super.onDestroy();
	}

}
