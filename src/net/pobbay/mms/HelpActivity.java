package net.pobbay.mms;

import net.pobbay.view.MyButton;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 帮助界面
 *
 * @author Yichou
 *
 */
public class HelpActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);

		((TextView) findViewById(R.id.tv_titlebar_title))
				.setText(R.string.select);

		MyButton btn = (MyButton) findViewById(R.id.btn_titlebar_left);
		btn.setImageResource(R.drawable.ic_back);
		btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		onBackPressed();
	}
}
