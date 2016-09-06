package snippet;

import java.io.Serializable;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonSerializable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2871021292399104222L;

	public void init(JSONObject obj) throws JSONException {
		// TODO Auto-generated method stub
		Iterator<?> objIt = obj.keys();
		
		while (objIt.hasNext()) {
			String key = objIt.next().toString();
			Object value = null;

			value = obj.get(key.toString());

			setParam(key, value.toString());

		}
	}

	protected abstract void setParam(String key, String value);

}
