package cn.zl.face.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class DrawImageView extends ImageView {

	public DrawImageView(Context context) {
		super(context);
	}

	public DrawImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	Paint paint = new Paint();

	{
		paint.setAntiAlias(true);
		paint.setColor(Color.GREEN);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(10f);// 设置线宽
		paint.setAlpha(100);
	};

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		float width = this.getWidth();
		float height = this.getHeight();
		Log.e("tag", width + " : " + height);
//		int left = (int) (width / 2 - 300);
//		int right = (int) (width / 2 + 300);
//		int top = (int) (height / 2 - 300);
//		int bottom = (int) (height / 2 + 300);

		canvas.drawCircle(width / 2, height / 2, 300, paint);
	}

}
