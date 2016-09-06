package snippet;

import org.json.JSONException;
import org.json.JSONObject;

public class face extends JsonSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4020075616486864792L;

	public String face_id = ""; //

	public String tag; //

	@Override
	protected void setParam(String key, String value) {
		// TODO Auto-generated method stub

		if (key.equals("face_id")) {
			face_id = value;
		} else if (key.equals("tag")) {
			tag = value;
		}
	}

}
