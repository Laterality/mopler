package kr.latera.mopler.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.latera.mopler.R;

/**
 * Created by Jinwoo Shin on 2017-05-03.
 */

public class MonthlyCalendarActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener
{
	@BindView(R.id.cv_monthly_calendar)
	MaterialCalendarView cvCalendar;


	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_monthly);

		ButterKnife.bind(this);

		cvCalendar.setTitleMonths(R.array.months);
		cvCalendar.setWeekDayLabels(R.array.weeks);






	}

	@Override
	public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
	{
		Toast.makeText(this, String.format(Locale.KOREA, "%d년 %d월 %d일", year, month, dayOfMonth),
				Toast.LENGTH_SHORT).show();
	}
}
