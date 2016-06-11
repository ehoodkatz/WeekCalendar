package noman.weekcalendar.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.joda.time.DateTime;

import noman.weekcalendar.R;
import noman.weekcalendar.fragment.WeekFragment;

import static noman.weekcalendar.fragment.WeekFragment.DATE_KEY;
import static noman.weekcalendar.view.WeekPager.NUM_OF_PAGES;

/**
 * Created by nor on 12/4/2015.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private final String TAG = PagerAdapter.class.getSimpleName();
    private int currentPage = NUM_OF_PAGES - 1;
    private DateTime date;
    private TypedArray typedArray;
    private Context context;

    public PagerAdapter(Context context, FragmentManager fm, DateTime date, TypedArray typedArray) {
        super(fm);
        this.date = date;
        this.typedArray = typedArray;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem() called with: " + "position = [" + position + "]");
        WeekFragment fragment = new WeekFragment();
        Bundle bundle = new Bundle();

        if (position < currentPage) {
            bundle.putSerializable(DATE_KEY, getPerviousDate());
        } else if (position > currentPage) {
            bundle.putSerializable(DATE_KEY, getNextDate());
        } else {
            bundle.putSerializable(DATE_KEY, getTodaysDate());
        }

        bundle.putFloat(WeekFragment.TEXT_SIZE_KEY, typedArray.getDimension(R.styleable.WeekCalendar_daysTextSize, -1));
        bundle.putInt(WeekFragment.TEXT_COLOR_KEY, typedArray.getColor(R.styleable.WeekCalendar_daysTextColor, Color.WHITE));
        bundle.putInt(WeekFragment.TODAYS_DATE_COLOR_KEY, typedArray.getColor(R.styleable.WeekCalendar_todaysDateBgColor, ContextCompat.getColor(context, R.color.colorAccent)));
        bundle.putInt(WeekFragment.SELECTED_DATE_COLOR_KEY, typedArray.getColor(R.styleable.WeekCalendar_selectedBgColor, ContextCompat.getColor(context, R.color.colorAccent)));
        bundle.putInt(WeekFragment.SELECTED_DATE_TEXT_COLOR_KEY, typedArray.getColor(R.styleable.WeekCalendar_selectedTextColor, Color.WHITE));
        bundle.putInt(WeekFragment.FUTURE_DATE_TEXT_COLOR_KEY, typedArray.getColor(R.styleable.WeekCalendar_futureDaysTextColor, Color.GRAY));
        bundle.putBoolean(WeekFragment.SHOW_CURRENT_DATE_KEY, typedArray.getBoolean(R.styleable.WeekCalendar_showCurrentDate, false));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_OF_PAGES;
    }

    private DateTime getTodaysDate() {
        return date;
    }

    private DateTime getPerviousDate() {
        return date.plusDays(-7);
    }

    private DateTime getNextDate() {
        return date.plusDays(7);
    }

    public void swipeBack() {
        Log.d(TAG, "swipeBack() called with: " + "");
        if(currentPage - 1 > 0){
            currentPage--;
            date = date.plusDays(-7);
        }
    }

    public void swipeForward() {
        Log.d(TAG, "swipeForward() called with: " + "");
        if(currentPage + 1 < NUM_OF_PAGES){
            date = date.plusDays(7);
            currentPage++;
        }
    }

   /* public DateTime getDate() {
        return date;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }
*/
}