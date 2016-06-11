package noman.weekcalendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;

import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;

import noman.weekcalendar.R;
import noman.weekcalendar.adapter.PagerAdapter;
import noman.weekcalendar.eventbus.BusProvider;
import noman.weekcalendar.eventbus.Event;
import noman.weekcalendar.fragment.WeekFragment;

/**
 * Created by nor on 12/5/2015.
 */
public class WeekPager extends ViewPager {
    private final String TAG = WeekPager.class.getSimpleName();
    private PagerAdapter adapter;
    private int pos;
    private boolean check;
    public static int NUM_OF_PAGES;
    private TypedArray typedArray;

    public WeekPager(Context context) {
        super(context);
        initialize(null);
    }

    public WeekPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        if (attrs != null) {
            typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.WeekCalendar);
            NUM_OF_PAGES = typedArray.getInt(R.styleable.WeekCalendar_numOfPages, 100);
        }
        setId(idCheck());
        if (!isInEditMode()) {
            BusProvider.getInstance().register(this);
            initPager(new DateTime());
        }
    }

    private void initPager(DateTime dateTime) {
        pos = NUM_OF_PAGES - 1;
        adapter = new PagerAdapter(getContext(), ((AppCompatActivity) getContext()).getSupportFragmentManager(), dateTime, typedArray);
        setAdapter(adapter);
        addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected() called with: " + "position = [" + position + "]");
                if (!check) {
                    if (position < pos) {
                        adapter.swipeBack();
                    } else if (position > pos) {
                        adapter.swipeForward();
                    }

                    if (position != NUM_OF_PAGES - 1) {
                        BusProvider.getInstance().post(new Event.SetFutureDayTextColor(new boolean[7]));
                    } else {
                        BusProvider.getInstance().post(new Event.SetFutureDayTextColor(getFutureDays()));
                    }
                }
                pos = position;
                check = false;
            }

        });
        setOverScrollMode(OVER_SCROLL_NEVER);
        setCurrentItem(pos);
        if (typedArray != null) {
            setBackgroundColor(typedArray.getColor(R.styleable.WeekCalendar_daysBackgroundColor, ContextCompat.getColor(getContext(), R.color.colorPrimary)));
        }
        if (WeekFragment.selectedDateTime == null) {
            WeekFragment.selectedDateTime = new DateTime();
        }
    }

    private boolean[] getFutureDays() {
        boolean[] futureDays = new boolean[7];
        ArrayList<DateTime> days = new ArrayList<>();
        DateTime midDate = WeekFragment.CalendarStartDate.withDayOfWeek(DateTimeConstants.THURSDAY);

        Log.d(TAG, String.format("getFutureDays: midDate=[%s] for CalenderStartDate=[%s]", midDate, WeekFragment.CalendarStartDate));

        for (int i = -3; i <= 3; i++) {
            days.add(midDate.plusDays(i));
        }

        StringBuilder builder = new StringBuilder();

        for (int index = 0; index < days.size(); index++) {
            futureDays[index] = isFutureDate(days.get(index));
            builder.append(String.format("date=[%s] isFutureDate=[%s]\n", days.get(index), futureDays[index]));
        }

        Log.d(TAG, "getFutureDays() returned: " + builder.toString());

        return futureDays;
    }

    private boolean isFutureDate(DateTime dateTime) {
        Log.d(TAG, String.format("isFutureDate: CalenderStartDate=[%s], currentDate=[%s]", WeekFragment.CalendarStartDate, dateTime));
        boolean result = dateTime.toLocalDate().isAfter(WeekFragment.CalendarStartDate.toLocalDate());
        Log.d(TAG, "isFutureDate() returned: " + result);
        return result;
    }

    @Subscribe
    public void setCurrentPage(Event.SetCurrentPageEvent event) {
        check = true;
        if (event.getDirection() == 1) {
            adapter.swipeForward();
        } else {
            adapter.swipeBack();
        }
        setCurrentItem(getCurrentItem() + event.getDirection());
    }

    @Subscribe
    public void reset(Event.ResetEvent event) {
        WeekFragment.selectedDateTime = new DateTime(WeekFragment.CalendarStartDate);
        //WeekFragment.CalendarStartDate = new DateTime();
        initPager(WeekFragment.CalendarStartDate);
    }

    @Subscribe
    public void setSelectedDate(Event.SetSelectedDateEvent event) {
        WeekFragment.selectedDateTime = event.getSelectedDate();
        initPager(event.getSelectedDate());
    }

    @Subscribe
    public void setStartDate(Event.SetStartDateEvent event) {
        WeekFragment.CalendarStartDate = event.getStartDate();
        WeekFragment.selectedDateTime = event.getStartDate();
        initPager(event.getStartDate());
    }

    private int idCheck() {
        int id = 0;
        while (findViewById(++id) != null) ;
        return id;
    }
}