package kr.latera.mopler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import kr.latera.mopler.R;

/**
 * Created by Jinwoo Shin on 2017-05-06.
 */

public class SplashActivity extends AppCompatActivity
{
	private static final long DELAY = 500;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		overridePendingTransition(R.anim.translate_up, R.anim.fade_out);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				startActivity(new Intent(SplashActivity.this, MainActivity.class));
				finish();
			}
		}, DELAY);
	}
}
