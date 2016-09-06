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
import cn.zl.face.view.RatingBar;
import cn.zl.face.view.RatingView;
import snippet.Person;

@SuppressLint("NewApi")
public class RegisterActivity extends Activity {

	protected static final int DETECT_FILED = -1;

	protected static final int ADDFACESUCCESS = 8;

	protected static final int SELECTIMAGE = 2;

	protected static final int OTHERPERSON = 3;
	// Camera nv21格式预览帧的尺寸，默认设置640*480
	private int PREVIEW_WIDTH = 640;
	private int PREVIEW_HEIGHT = 480;

	private SurfaceView mPreviewSurface;
	private Timer timer;

	private Camera mCamera;
	private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;

	private ImageView iv_f1;
	private ImageView iv_s2;
	private ImageView iv_t3;
	private boolean selectImage = false;
	private Bitmap bmp;
	private FaceppDetect detect;
	private RatingView ratingView;
	private RatingBar bar1;
	private RatingBar bar2;
	private RatingBar bar3;
	private RatingBar bar4;
	private int addFaceNum = 0;
	private Person person;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);

		Intent intent = super.getIntent();
		if (intent.getSerializableExtra("person") != null) {
			person = (Person) intent.getSerializableExtra("person");
		}

		Log.e("TAG", person.person_name);

		if (person.person_name.length() > 0) {
			initFaceCheck();
		}
		initratingView();
	}

	private void initratingView() {

		bar1 = new RatingBar(addFaceNum, "认证");
		bar2 = new RatingBar(addFaceNum, "认证");
		bar3 = new RatingBar(addFaceNum, "认证");
		bar4 = new RatingBar(addFaceNum, "认证");

		ratingView.addRatingBar(bar1);
		ratingView.addRatingBar(bar2);
		ratingView.addRatingBar(bar3);
		ratingView.addRatingBar(bar4);

		ratingView.setDefaultColor(0x00ff00);
		ratingView.show();

	}

	private void initFaceCheck() {
		detect = new FaceppDetect(RegisterActivity.this);
		mPreviewSurface = (SurfaceView) findViewById(R.id.sfv_preview);
		iv_f1 = (ImageView) findViewById(R.id.iv_f1);
		iv_s2 = (ImageView) findViewById(R.id.iv_s2);
		iv_t3 = (ImageView) findViewById(R.id.iv_t3);

		ratingView = (RatingView) findViewById(R.id.ratingView);

		mPreviewSurface.getHolder().addCallback(mPreviewCallback);
		mPreviewSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		ratingView.setAnimatorListener(new RatingView.AnimatorListener() {

			@Override
			public void onRotateStart() {

			}

			@Override
			public void onRotateEnd() {

			}

			@Override
			public void onRatingStart() {

			}

			@Override
			public void onRatingEnd() {
				Log.e("startTimer", "startTimer");
				if (mCamera != null) {
					// 开启定时器
					startTimer();
				}
			}
		});
	}

	private Callback mPreviewCallback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			closeCamera();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			openCamera();

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
		}, 10); // 0.01秒后开始采集
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
					Log.e("selectImage", "执行了");
					selectImage = false;
				}

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

		Size size = camera.getParameters().getPreviewSize(); // 获取预览大小
		int w = size.width; // 宽度
		int h = size.height;

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
			case ADDFACESUCCESS:
				showProgres();
				break;
			case OTHERPERSON:
				showTextToast("小心身后有人");
				break;
			default:
				break;
			}
		};
	};

	private void showTextToast(String msg) {
		if (toast == null) {
			toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}

	private void showProgres() {
		if (addFaceNum < 10) {
			addFaceNum++;
			ratingView.clear();
			bar1.setRate(addFaceNum);
			bar2.setRate(addFaceNum);
			bar3.setRate(addFaceNum);
			bar4.setRate(addFaceNum);

			ratingView.ratingShow();
		} else {
			showTextToast("认证成功");
			selectImage = false;
			//closeCamera();
		}
	}

	private void detectFaceOffLine(Bitmap bmp2) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				int result = detect.detectOffLine(bmp);
				if (result == 1) {
					if (addFaceNum < 10) {
						selectImage = true;
					}
					getFaceIdOnline(bmp);

				} else {

					selectImage = true;
					Log.e("selectImage", "selectImage filed");
					Message msg = Message.obtain();
					if (result == 2) {
						msg.what = OTHERPERSON; // 身后有人
					} else {
						msg.what = DETECT_FILED; // 没有露脸
					}

					handler.sendMessage(msg);
				}
			}
		}).start();

	}

	private void getFaceIdOnline(final Bitmap bmp) {

		FaceppDetect.detectOnline(bmp, new CallBack() {

			@Override
			public void success(JSONObject result) {
				try {
					JSONArray faces = result.getJSONArray("face");
					int faceCount = faces.length();
					
					for (int i = 0; i < faceCount; i++) {
						// 拿到单独face对象
						JSONObject face = faces.getJSONObject(i);
						String face_id = face.getString("face_id");

						Log.i("face_id", "face_id:" + face_id);
						registerFaceId2Person(face_id);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void error(int e) {
			}
		});

	}

	protected void registerFaceId2Person(String face_id) {
		FaceppDetect.addFace2Person(face_id, person.person_name, new CallBack() {

			@Override
			public void success(JSONObject result) {
				Message msg = Message.obtain();
				msg.what = ADDFACESUCCESS;
				handler.sendMessage(msg);
			}

			@Override
			public void error(int e) {
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
