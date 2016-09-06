package snippet;

import org.json.JSONException;
import org.json.JSONObject;

public class Person extends JsonSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4020075616486864792L;

	public String added_face; //
	public String person_id = ""; //
	public String person_name = ""; //
	public String added_group = ""; //
	public String tag; //

	@Override
	protected void setParam(String key, String value) {
		// TODO Auto-generated method stub

		if (key.equals("added_face")) {
			added_face = value;

		} else if (key.equals("person_id")) {
			person_id = value;
		} else if (key.equals("person_name")) {
			person_name = value;
		} else if (key.equals("added_group")) {
			added_group = value;
		} else if (key.equals("tag")) {
			tag = value;
		}
	}

}
