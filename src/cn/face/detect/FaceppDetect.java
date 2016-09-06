package cn.face.detect;

import java.io.ByteArrayOutputStream;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.faceplusplus.api.FaceDetecter;
import com.faceplusplus.api.FaceDetecter.Face;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

public class FaceppDetect {

	public static final String KEY = "31da7de062c50318cb8aaa1d85ed2d66";
	public static final String SECRET = "sdRrsPwLkyay9ByAG2KAk7mfyBdoScHy";
	private Context context;

	// person_name: 69b93d40a79a431da3c2f4917725c5171
	// face_id : 6d0985ce22e99911566e19d858016d76

	public FaceppDetect(Context context) {
		this.context = context;
	}

	public interface CallBack {
		void success(JSONObject result);

		void error(int e);
	}

	public static void creatPerson(final String name, final CallBack callBack) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					HttpRequests requests = new HttpRequests(KEY, SECRET, true, true);
					PostParameters params = new PostParameters();
					params.setPersonName(name);

					JSONObject jsonObject = requests.personCreate(params);

					Log.e("create Person success：", jsonObject.toString());
					if (callBack != null)
						callBack.success(jsonObject);
				} catch (FaceppParseException e) {
					e.printStackTrace();
					Log.e("create Person", "出错了");
					if (callBack != null)
						callBack.error(1);
				}

			}
		}).start();
	}

	public static void getInfoPerson(final String name, final CallBack callBack) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					HttpRequests requests = new HttpRequests(KEY, SECRET, true, true);

					PostParameters params = new PostParameters();
					params.setPersonName(name);

					JSONObject jsonObject = requests.personGetInfo(params);

					Log.e("get person success：", jsonObject.toString());
					if (callBack != null)
						callBack.success(jsonObject);

				} catch (FaceppParseException e) {
					e.printStackTrace();
					Log.e("get person", "出错了");
					if (callBack != null)
						callBack.error(1);

				}
			}
		}).start();
	}

	public static void removePersonFace(final String name, final CallBack callBack) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					HttpRequests requests = new HttpRequests(KEY, SECRET, true, true);

					PostParameters params = new PostParameters();
					params.setPersonName(name);
					params.setFaceId("all");

					JSONObject jsonObject = requests.personRemoveFace(params);

					Log.e("get person success：", jsonObject.toString());
					if (callBack != null)
						callBack.success(jsonObject);

				} catch (FaceppParseException e) {
					e.printStackTrace();
					Log.e("get person", "出错了");
					if (callBack != null)
						callBack.error(1);

				}
			}
		}).start();
	}

	public static void verifyPeson(final String face_id, final String person_name, final CallBack callback) {

		try {
			HttpRequests requests = new HttpRequests(KEY, SECRET, true, true);

			PostParameters params = new PostParameters();
			params.setPersonName(person_name);
			// 保证数据最新
			Log.e("trainverify success：", requests.trainVerify(params).toString());

			params.setFaceId(face_id);
			JSONObject jsonObject = requests.recognitionVerify(params);

			Log.e("verifyPeson success：", jsonObject.toString());

			if (jsonObject.getBoolean("is_same_person")) {
				callback.success(jsonObject);
			} else {
				callback.error(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			callback.error(1);

		}
	}

	public static void addFace2Person(final String face_id, String person_name, final CallBack callBack) {

		try {
			HttpRequests requests = new HttpRequests(KEY, SECRET, true, true);

			PostParameters params = new PostParameters();
			params.setPersonName(person_name);
			params.setFaceId(face_id);

			JSONObject jsonObject = requests.personAddFace(params);

			Log.e("Add face success：", jsonObject.toString());

			if (jsonObject.getBoolean("success")) {
				callBack.success(jsonObject);
			} else {
				callBack.error(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Add face", "出错了");
			callBack.error(1);
		}

	}

	public static void getImg() {

		try {
			HttpRequests requests = new HttpRequests(KEY, SECRET, true, true);

			PostParameters params = new PostParameters();
			// params.setFaceId("6d0985ce22e99911566e19d858016d76");

			JSONObject jsonObject = requests.infoGetFace(params);

			Log.e("TAG_getImage", jsonObject.toString());

		} catch (FaceppParseException e) {
			e.printStackTrace();
			Log.e("TAG_getImage", "出错了");

		}

	}

	public int detectOffLine(Bitmap bm) {

		FaceDetecter detecter = new FaceDetecter();
		detecter.init(context, KEY);

		Face[] faceinfo = detecter.findFaces(bm);

		if (faceinfo == null) {
			// Toast.makeText(context, "请将脸部放入圆框内", Toast.LENGTH_SHORT).show();
			detecter.release(context);
			return 0;
		} else if (faceinfo.length > 1) {
			Log.e("attention", "attention: 身后有人哟!");
			return 2;
		} else {

			Log.e("zl", "detecter:" + detecter.getResultJsonString());
			detecter.release(context);
			return 1;
		}
	}

	public void registerFace(final Bitmap bm, final CallBack callBack) {

	}

	public static void detectOnline(final Bitmap bm, final CallBack callBack) {

		try {
			HttpRequests requests = new HttpRequests(KEY, SECRET, true, true);
			Bitmap bmSmall = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);

			byte[] arrays = stream.toByteArray();

			PostParameters params = new PostParameters();
			params.setImg(arrays);

			JSONObject jsonObject = requests.detectionDetect(params);

			// Log
			Log.e("TAG", jsonObject.toString());

			if (callBack != null) {
				callBack.success(jsonObject);
			}
		} catch (FaceppParseException e) {
			e.printStackTrace();
			Log.e("detectOnline", "出错了");
		}

	}

	public static void detectOnlineByStream(final byte[] arrays, final int w, final int h, final CallBack callBack) {

		try {
			HttpRequests requests = new HttpRequests(KEY, SECRET, true, true);

			byte[] ori = new byte[w * h];
			int is = 0;
			for (int x = w - 1; x >= 0; x--) {
				for (int y = h - 1; y >= 0; y--) {
					ori[is] = arrays[y * w + x];
					is++;
				}
			}

			PostParameters params = new PostParameters();
			params.setImg(ori);

			JSONObject jsonObject = requests.detectionDetect(params);

			// Log
			Log.i("TAG", jsonObject.toString());

			if (callBack != null) {
				callBack.success(jsonObject);
			}
		} catch (FaceppParseException e) {
			e.printStackTrace();
			Log.i("TAG", "出错了");
		}

	}

}
