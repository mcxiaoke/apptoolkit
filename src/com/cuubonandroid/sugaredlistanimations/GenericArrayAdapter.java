package com.cuubonandroid.sugaredlistanimations;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * @author <a href="http://www.hugofernandes.pt">Hugo Fernandes</a>
 */
public abstract class GenericArrayAdapter<T> extends ArrayAdapter<T> {

    protected static final long ANIM_DEFAULT_SPEED = 400L;

    protected Interpolator interpolator;

    protected SparseBooleanArray positionsMapper;
    protected int height, width, previousPostition;
    protected SpeedScrollListener scrollListener;
    protected double speed;
    protected long animDuration;
    protected View v;
    protected Context context;

    protected GenericArrayAdapter(Context context, SpeedScrollListener scrollListener, List<T> items) {
        super(context, 0, items);
        this.context = context;
        this.scrollListener = scrollListener;

        previousPostition = -1;
        positionsMapper = new SparseBooleanArray();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        defineInterpolator();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getAnimatedView(position, convertView, parent);
    }

    protected abstract View getAnimatedView(int position, View convertView, ViewGroup parent);

    protected abstract void defineInterpolator();
}
